from datetime import date, datetime, timedelta

from fastapi import APIRouter, Depends, Form, Request
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates
from sqlalchemy.orm import Session

from app.database import get_db
from app.deps import get_current_user
from app.models import Appointment, AppointmentStatus, Doctor, Report, UserRole

router = APIRouter(prefix="/doctor")
templates = Jinja2Templates(directory="app/templates")


def _require_doctor(request: Request, db: Session):
    user = get_current_user(request, db)
    if user is None or user.role != UserRole.DOCTOR:
        return None, None
    doctor = db.query(Doctor).filter(Doctor.user_id == user.id).first()
    return user, doctor


@router.get("", response_class=HTMLResponse)
def dashboard(request: Request, db: Session = Depends(get_db)):
    user, doctor = _require_doctor(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    today = date.today()
    day_start = datetime.combine(today, datetime.min.time())
    day_end = day_start + timedelta(days=1)

    appointments = (
        db.query(Appointment)
        .filter(
            Appointment.doctor_id == doctor.id,
            Appointment.slot_start >= day_start,
            Appointment.slot_start < day_end,
            Appointment.status != AppointmentStatus.CANCELLED,
        )
        .order_by(Appointment.slot_start)
        .all()
    )

    return templates.TemplateResponse(
        "doctor/dashboard.html",
        {
            "request": request,
            "user": user,
            "doctor": doctor,
            "appointments": appointments,
            "today": today,
        },
    )


@router.post("/appointments/{appointment_id}/complete", response_class=HTMLResponse)
def complete_appointment(request: Request, appointment_id: int, db: Session = Depends(get_db)):
    user, doctor = _require_doctor(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    appointment = db.get(Appointment, appointment_id)
    if appointment and appointment.doctor_id == doctor.id and appointment.status == AppointmentStatus.SCHEDULED:
        appointment.status = AppointmentStatus.COMPLETED
        db.commit()
        return RedirectResponse(f"/doctor/appointments/{appointment_id}/report", status_code=303)

    return RedirectResponse("/doctor", status_code=303)


@router.get("/appointments/{appointment_id}/report", response_class=HTMLResponse)
def report_form(request: Request, appointment_id: int, db: Session = Depends(get_db)):
    user, doctor = _require_doctor(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    appointment = db.get(Appointment, appointment_id)
    if appointment is None or appointment.doctor_id != doctor.id:
        return RedirectResponse("/doctor", status_code=303)

    if appointment.report is not None:
        return RedirectResponse("/doctor", status_code=303)

    return templates.TemplateResponse(
        "doctor/report_form.html",
        {"request": request, "appointment": appointment, "error": None},
    )


@router.post("/appointments/{appointment_id}/report", response_class=HTMLResponse)
def report_submit(
    request: Request,
    appointment_id: int,
    prescription_code: str = Form(""),
    notes: str = Form(""),
    fee: str = Form(""),
    db: Session = Depends(get_db),
):
    user, doctor = _require_doctor(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    appointment = db.get(Appointment, appointment_id)
    if appointment is None or appointment.doctor_id != doctor.id:
        return RedirectResponse("/doctor", status_code=303)

    if appointment.status != AppointmentStatus.COMPLETED or appointment.report is not None:
        return RedirectResponse("/doctor", status_code=303)

    prescription_code = prescription_code.strip() or "None"
    notes = notes.strip() or "None"
    try:
        fee_value = float(fee) if fee.strip() else 0.0
    except ValueError:
        fee_value = 0.0
    if fee_value < 0:
        fee_value = 0.0

    report = Report(
        appointment_id=appointment.id,
        prescription_code=prescription_code,
        notes=notes,
        fee=fee_value,
        is_paid=(fee_value == 0),
    )
    db.add(report)
    db.commit()

    return RedirectResponse("/doctor", status_code=303)
