from fastapi import APIRouter, Depends, Form, Request
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates
from sqlalchemy.orm import Session

from app.database import get_db
from app.deps import get_current_user
from app.models import Appointment, Report, User, UserRole

router = APIRouter(prefix="/billing-officer")
templates = Jinja2Templates(directory="app/templates")


def _require_billing_officer(request: Request, db: Session):
    user = get_current_user(request, db)
    if user is None or user.role != UserRole.BILLING_OFFICER:
        return None
    return user


@router.get("", response_class=HTMLResponse)
def dashboard(request: Request, db: Session = Depends(get_db)):
    user = _require_billing_officer(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    return templates.TemplateResponse(
        "billing_officer/dashboard.html",
        {"request": request, "user": user, "patient": None, "reports": None, "search_attempted": False},
    )


@router.get("/search", response_class=HTMLResponse)
def search(request: Request, national_id: str = "", db: Session = Depends(get_db)):
    user = _require_billing_officer(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    national_id = national_id.strip()
    patient = None
    if national_id:
        patient = (
            db.query(User)
            .filter(User.national_id == national_id, User.role == UserRole.PATIENT)
            .first()
        )

    reports = []
    if patient:
        reports = (
            db.query(Report)
            .join(Appointment, Report.appointment_id == Appointment.id)
            .filter(Appointment.patient_id == patient.id)
            .order_by(Report.created_at.desc())
            .all()
        )

    return templates.TemplateResponse(
        "billing_officer/_results.html",
        {
            "request": request,
            "patient": patient,
            "reports": reports,
            "searched_id": national_id,
            "search_attempted": True,
        },
    )


@router.post("/reports/{report_id}/mark-paid", response_class=HTMLResponse)
def mark_paid(request: Request, report_id: int, national_id: str = Form(...), db: Session = Depends(get_db)):
    user = _require_billing_officer(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    report = db.get(Report, report_id)
    if report and not report.is_paid:
        report.is_paid = True
        db.commit()

    national_id = national_id.strip()
    patient = (
        db.query(User)
        .filter(User.national_id == national_id, User.role == UserRole.PATIENT)
        .first()
    )
    reports = []
    if patient:
        reports = (
            db.query(Report)
            .join(Appointment, Report.appointment_id == Appointment.id)
            .filter(Appointment.patient_id == patient.id)
            .order_by(Report.created_at.desc())
            .all()
        )

    return templates.TemplateResponse(
        "billing_officer/_results.html",
        {
            "request": request,
            "patient": patient,
            "reports": reports,
            "searched_id": national_id,
            "search_attempted": True,
        },
    )
