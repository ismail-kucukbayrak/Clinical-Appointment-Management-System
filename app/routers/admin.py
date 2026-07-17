from fastapi import APIRouter, Depends, Form, Request
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates
from sqlalchemy.orm import Session

from app.database import get_db
from app.deps import get_current_user
from app.models import Doctor, Polyclinic, User, UserRole
from app.security import hash_password

router = APIRouter(prefix="/admin")
templates = Jinja2Templates(directory="app/templates")

LIST_TEMPLATES = {
    "polyclinics": "admin/_polyclinics_list.html",
    "doctors": "admin/_doctors_list.html",
    "billing-officers": "admin/_billing_officers_list.html",
}

FORM_TEMPLATES = {
    "polyclinics": "admin/_polyclinic_form.html",
    "doctors": "admin/_doctor_form.html",
    "billing-officers": "admin/_billing_officer_form.html",
}


def _require_admin(request: Request, db: Session):
    user = get_current_user(request, db)
    if user is None or user.role != UserRole.ADMIN:
        return None
    return user


def _list_context(db: Session, category: str) -> dict:
    if category == "polyclinics":
        return {"polyclinics": db.query(Polyclinic).order_by(Polyclinic.name).all()}
    if category == "doctors":
        return {"doctors": db.query(Doctor).join(User, Doctor.user_id == User.id).order_by(User.full_name).all()}
    if category == "billing-officers":
        return {
            "billing_officers": db.query(User)
            .filter(User.role == UserRole.BILLING_OFFICER)
            .order_by(User.full_name)
            .all()
        }
    return {}


def _form_context(db: Session, category: str) -> dict:
    if category == "doctors":
        return {"polyclinics": db.query(Polyclinic).order_by(Polyclinic.name).all()}
    return {}


@router.get("", response_class=HTMLResponse)
def dashboard(request: Request, db: Session = Depends(get_db)):
    user = _require_admin(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)
    return templates.TemplateResponse(
        "admin/dashboard.html",
        {
            "request": request,
            "user": user,
            "list_template": LIST_TEMPLATES["polyclinics"],
            **_list_context(db, "polyclinics"),
        },
    )


@router.get("/{category}/list", response_class=HTMLResponse)
def list_partial(request: Request, category: str, db: Session = Depends(get_db)):
    user = _require_admin(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    if category not in LIST_TEMPLATES:
        return RedirectResponse("/admin", status_code=303)

    return templates.TemplateResponse(
        LIST_TEMPLATES[category],
        {"request": request, **_list_context(db, category)},
    )


@router.get("/{category}/add-form", response_class=HTMLResponse)
def add_form_partial(request: Request, category: str, db: Session = Depends(get_db)):
    user = _require_admin(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    if category not in FORM_TEMPLATES:
        return RedirectResponse("/admin", status_code=303)

    return templates.TemplateResponse(
        FORM_TEMPLATES[category],
        {"request": request, "error": None, **_form_context(db, category)},
    )


@router.post("/polyclinics", response_class=HTMLResponse)
def add_polyclinic(request: Request, name: str = Form(...), db: Session = Depends(get_db)):
    user = _require_admin(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    name = name.strip()
    error = None
    if not name:
        error = "Polyclinic name cannot be empty."
    elif db.query(Polyclinic).filter(Polyclinic.name == name).first():
        error = "A polyclinic with this name already exists."

    if error:
        response = templates.TemplateResponse(
            "admin/_polyclinic_form.html", {"request": request, "error": error}, status_code=400
        )
        response.headers["HX-Retarget"] = "#admin-form-area"
        response.headers["HX-Reswap"] = "innerHTML"
        return response

    db.add(Polyclinic(name=name))
    db.commit()

    return templates.TemplateResponse(
        "admin/_polyclinics_list.html", {"request": request, **_list_context(db, "polyclinics")}
    )


@router.post("/polyclinics/{polyclinic_id}/delete", response_class=HTMLResponse)
def delete_polyclinic(request: Request, polyclinic_id: int, db: Session = Depends(get_db)):
    user = _require_admin(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    polyclinic = db.get(Polyclinic, polyclinic_id)
    if polyclinic:
        db.delete(polyclinic)
        db.commit()

    return templates.TemplateResponse(
        "admin/_polyclinics_list.html", {"request": request, **_list_context(db, "polyclinics")}
    )


@router.post("/doctors", response_class=HTMLResponse)
def add_doctor(
    request: Request,
    full_name: str = Form(...),
    national_id: str = Form(...),
    password: str = Form(...),
    polyclinic_id: int = Form(...),
    db: Session = Depends(get_db),
):
    user = _require_admin(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    full_name = full_name.strip()
    national_id = national_id.strip()

    error = None
    if not national_id.isdigit() or len(national_id) != 11:
        error = "National ID must be exactly 11 digits."
    elif len(password) < 4:
        error = "Password must be at least 4 characters."
    elif db.query(User).filter(User.national_id == national_id).first():
        error = "An account with this national ID already exists."
    elif db.get(Polyclinic, polyclinic_id) is None:
        error = "Selected polyclinic does not exist."

    if error:
        response = templates.TemplateResponse(
            "admin/_doctor_form.html",
            {"request": request, "error": error, **_form_context(db, "doctors")},
            status_code=400,
        )
        response.headers["HX-Retarget"] = "#admin-form-area"
        response.headers["HX-Reswap"] = "innerHTML"
        return response

    new_user = User(
        role=UserRole.DOCTOR,
        national_id=national_id,
        full_name=full_name,
        password_hash=hash_password(password),
    )
    db.add(new_user)
    db.flush()

    doctor = Doctor(user_id=new_user.id, polyclinic_id=polyclinic_id)
    db.add(doctor)
    db.commit()

    return templates.TemplateResponse(
        "admin/_doctors_list.html", {"request": request, **_list_context(db, "doctors")}
    )


@router.post("/doctors/{doctor_id}/delete", response_class=HTMLResponse)
def delete_doctor(request: Request, doctor_id: int, db: Session = Depends(get_db)):
    user = _require_admin(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    doctor = db.get(Doctor, doctor_id)
    if doctor:
        doctor_user = db.get(User, doctor.user_id)
        db.delete(doctor)
        if doctor_user:
            db.delete(doctor_user)
        db.commit()

    return templates.TemplateResponse(
        "admin/_doctors_list.html", {"request": request, **_list_context(db, "doctors")}
    )


@router.post("/billing-officers", response_class=HTMLResponse)
def add_billing_officer(
    request: Request,
    full_name: str = Form(...),
    national_id: str = Form(...),
    password: str = Form(...),
    db: Session = Depends(get_db),
):
    user = _require_admin(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    full_name = full_name.strip()
    national_id = national_id.strip()

    error = None
    if not national_id.isdigit() or len(national_id) != 11:
        error = "National ID must be exactly 11 digits."
    elif len(password) < 4:
        error = "Password must be at least 4 characters."
    elif db.query(User).filter(User.national_id == national_id).first():
        error = "An account with this national ID already exists."

    if error:
        response = templates.TemplateResponse(
            "admin/_billing_officer_form.html", {"request": request, "error": error}, status_code=400
        )
        response.headers["HX-Retarget"] = "#admin-form-area"
        response.headers["HX-Reswap"] = "innerHTML"
        return response

    billing_officer = User(
        role=UserRole.BILLING_OFFICER,
        national_id=national_id,
        full_name=full_name,
        password_hash=hash_password(password),
    )
    db.add(billing_officer)
    db.commit()

    return templates.TemplateResponse(
        "admin/_billing_officers_list.html", {"request": request, **_list_context(db, "billing-officers")}
    )


@router.post("/billing-officers/{billing_officer_id}/delete", response_class=HTMLResponse)
def delete_billing_officer(request: Request, billing_officer_id: int, db: Session = Depends(get_db)):
    user = _require_admin(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    billing_officer = db.get(User, billing_officer_id)
    if billing_officer and billing_officer.role == UserRole.BILLING_OFFICER:
        db.delete(billing_officer)
        db.commit()

    return templates.TemplateResponse(
        "admin/_billing_officers_list.html", {"request": request, **_list_context(db, "billing-officers")}
    )
