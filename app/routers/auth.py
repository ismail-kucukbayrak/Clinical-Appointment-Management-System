from fastapi import APIRouter, Depends, Form, Request
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates
from sqlalchemy.orm import Session

from app.config import SESSION_COOKIE_NAME
from app.database import get_db
from app.deps import get_current_user
from app.models import User, UserRole
from app.security import create_session_token, hash_password, verify_password

router = APIRouter()
templates = Jinja2Templates(directory="app/templates")

ROLE_HOME = {
    UserRole.ADMIN: "/admin",
    UserRole.PATIENT: "/patient",
    UserRole.DOCTOR: "/doctor",
    UserRole.BILLING_OFFICER: "/billing-officer",
}

ROLE_BY_SLUG = {
    "admin": UserRole.ADMIN,
    "patient": UserRole.PATIENT,
    "doctor": UserRole.DOCTOR,
    "billing-officer": UserRole.BILLING_OFFICER,
}

ROLE_LABELS = {
    "admin": "Admin",
    "patient": "Patient",
    "doctor": "Doctor",
    "billing-officer": "Billing Officer",
}


@router.get("/", response_class=HTMLResponse)
def root(request: Request, db: Session = Depends(get_db)):
    user = get_current_user(request, db)
    if user:
        return RedirectResponse(ROLE_HOME[user.role], status_code=303)
    return RedirectResponse("/login", status_code=303)


@router.get("/login", response_class=HTMLResponse)
def login_select(request: Request, db: Session = Depends(get_db)):
    user = get_current_user(request, db)
    if user:
        return RedirectResponse(ROLE_HOME[user.role], status_code=303)
    return templates.TemplateResponse(
        "auth/login_select.html",
        {
            "request": request,
            "role_slug": "patient",
            "role_label": ROLE_LABELS["patient"],
            "error": None,
        },
    )


@router.get("/login/{role_slug}", response_class=HTMLResponse)
def login_page(request: Request, role_slug: str, db: Session = Depends(get_db)):
    user = get_current_user(request, db)
    if user:
        return RedirectResponse(ROLE_HOME[user.role], status_code=303)

    if role_slug not in ROLE_BY_SLUG:
        return RedirectResponse("/login", status_code=303)

    return templates.TemplateResponse(
        "auth/login_select.html",
        {
            "request": request,
            "role_slug": role_slug,
            "role_label": ROLE_LABELS[role_slug],
            "error": None,
        },
    )


@router.get("/login/{role_slug}/form", response_class=HTMLResponse)
def login_form_partial(request: Request, role_slug: str, db: Session = Depends(get_db)):
    user = get_current_user(request, db)
    if user:
        destination = ROLE_HOME[user.role]
        if request.headers.get("HX-Request") == "true":
            response = HTMLResponse(content="", status_code=200)
            response.headers["HX-Redirect"] = destination
            return response
        return RedirectResponse(destination, status_code=303)

    if role_slug not in ROLE_BY_SLUG:
        return RedirectResponse("/login", status_code=303)

    return templates.TemplateResponse(
        "auth/_login_form.html",
        {
            "request": request,
            "role_slug": role_slug,
            "role_label": ROLE_LABELS[role_slug],
            "error": None,
        },
    )


@router.post("/login/{role_slug}", response_class=HTMLResponse)
def login_submit(
    request: Request,
    role_slug: str,
    identifier: str = Form(...),
    password: str = Form(...),
    db: Session = Depends(get_db),
):
    if role_slug not in ROLE_BY_SLUG:
        return RedirectResponse("/login", status_code=303)

    expected_role = ROLE_BY_SLUG[role_slug]
    identifier = identifier.strip()

    if expected_role == UserRole.ADMIN:
        user = db.query(User).filter(User.username == identifier).first()
    else:
        user = db.query(User).filter(User.national_id == identifier).first()

    is_htmx = request.headers.get("HX-Request") == "true"

    if user is None or user.role != expected_role or not verify_password(password, user.password_hash):
        template_name = "auth/_login_form.html" if is_htmx else "auth/login_select.html"
        invalid_message = (
            "Invalid username or password."
            if expected_role == UserRole.ADMIN
            else "Invalid national ID or password."
        )
        return templates.TemplateResponse(
            template_name,
            {
                "request": request,
                "role_slug": role_slug,
                "role_label": ROLE_LABELS[role_slug],
                "error": invalid_message,
            },
            status_code=400,
        )

    token = create_session_token(user.id)
    destination = ROLE_HOME[user.role]

    if is_htmx:
        response = HTMLResponse(content="", status_code=200)
        response.headers["HX-Redirect"] = destination
    else:
        response = RedirectResponse(destination, status_code=303)

    response.set_cookie(
        key=SESSION_COOKIE_NAME,
        value=token,
        httponly=True,
        samesite="lax",
        max_age=60 * 60 * 12,
    )
    return response


@router.get("/logout")
def logout():
    response = RedirectResponse("/login", status_code=303)
    response.delete_cookie(SESSION_COOKIE_NAME)
    return response


@router.get("/register", response_class=HTMLResponse)
def register_page(request: Request, db: Session = Depends(get_db)):
    user = get_current_user(request, db)
    if user:
        return RedirectResponse(ROLE_HOME[user.role], status_code=303)
    return templates.TemplateResponse("auth/register.html", {"request": request, "error": None})


@router.post("/register", response_class=HTMLResponse)
def register_submit(
    request: Request,
    full_name: str = Form(...),
    national_id: str = Form(...),
    password: str = Form(...),
    confirm_password: str = Form(...),
    db: Session = Depends(get_db),
):
    full_name = full_name.strip()
    national_id = national_id.strip()

    error = None
    if not national_id.isdigit() or len(national_id) != 11:
        error = "National ID must be exactly 11 digits."
    elif password != confirm_password:
        error = "Passwords do not match."
    elif len(password) < 4:
        error = "Password must be at least 4 characters."
    elif db.query(User).filter(User.national_id == national_id).first():
        error = "An account with this national ID already exists."

    if error:
        return templates.TemplateResponse(
            "auth/register.html",
            {"request": request, "error": error},
            status_code=400,
        )

    patient = User(
        role=UserRole.PATIENT,
        national_id=national_id,
        full_name=full_name,
        password_hash=hash_password(password),
    )
    db.add(patient)
    db.commit()
    db.refresh(patient)

    token = create_session_token(patient.id)
    response = RedirectResponse("/patient", status_code=303)
    response.set_cookie(
        key=SESSION_COOKIE_NAME,
        value=token,
        httponly=True,
        samesite="lax",
        max_age=60 * 60 * 12,
    )
    return response
