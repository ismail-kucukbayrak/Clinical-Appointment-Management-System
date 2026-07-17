from datetime import date, datetime

from fastapi import APIRouter, Depends, Form, Request
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates
from sqlalchemy.orm import Session

from app.database import get_db
from app.deps import get_current_user
from app.models import Appointment, AppointmentStatus, Doctor, Polyclinic, UserRole
from app.services.scheduling import get_available_slots, get_bookable_days

router = APIRouter(prefix="/patient")
templates = Jinja2Templates(directory="app/templates")


def _require_patient(request: Request, db: Session):
    user = get_current_user(request, db)
    if user is None or user.role != UserRole.PATIENT:
        return None
    return user


@router.get("", response_class=HTMLResponse)
def dashboard(request: Request, db: Session = Depends(get_db)):
    user = _require_patient(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    appointments = (
        db.query(Appointment)
        .filter(Appointment.patient_id == user.id)
        .order_by(Appointment.slot_start.desc())
        .all()
    )
    polyclinics = db.query(Polyclinic).order_by(Polyclinic.name).all()

    return templates.TemplateResponse(
        "patient/dashboard.html",
        {
            "request": request,
            "user": user,
            "appointments": appointments,
            "polyclinics": polyclinics,
            "now": datetime.now(),
        },
    )


@router.get("/book/doctors", response_class=HTMLResponse)
def book_doctors(request: Request, polyclinic_id: int, db: Session = Depends(get_db)):
    user = _require_patient(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    doctors = db.query(Doctor).filter(Doctor.polyclinic_id == polyclinic_id).all()
    return templates.TemplateResponse(
        "patient/_doctors.html",
        {"request": request, "doctors": doctors},
    )


@router.get("/book/days", response_class=HTMLResponse)
def book_days(request: Request, doctor_id: int, db: Session = Depends(get_db)):
    user = _require_patient(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    bookable_days = get_bookable_days()

    return templates.TemplateResponse(
        "patient/_days.html",
        {"request": request, "doctor_id": doctor_id, "bookable_days": bookable_days, "selected_day": None},
    )


@router.get("/book/slots", response_class=HTMLResponse)
def book_slots(request: Request, doctor_id: int, day: str, db: Session = Depends(get_db)):
    user = _require_patient(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    parsed_day = date.fromisoformat(day)
    slots = get_available_slots(db, doctor_id, parsed_day)
    doctor = db.get(Doctor, doctor_id)

    return templates.TemplateResponse(
        "patient/_slot_options.html",
        {
            "request": request,
            "doctor_id": doctor_id,
            "doctor": doctor,
            "slots": slots,
        },
    )


@router.get("/book/confirm-button", response_class=HTMLResponse)
def book_confirm_button(request: Request, doctor_id: int, slot: str = "", db: Session = Depends(get_db)):
    user = _require_patient(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    return templates.TemplateResponse(
        "patient/_confirm_button.html",
        {"request": request, "doctor_id": doctor_id, "slot": slot},
    )


@router.post("/book", response_class=HTMLResponse)
def book_appointment(
    request: Request,
    doctor_id: int = Form(...),
    slot: str = Form(...),
    db: Session = Depends(get_db),
):
    user = _require_patient(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    is_htmx = request.headers.get("HX-Request") == "true"

    if not slot:
        return RedirectResponse("/patient", status_code=303)

    slot_start = datetime.fromisoformat(slot)

    conflict = (
        db.query(Appointment)
        .filter(
            Appointment.doctor_id == doctor_id,
            Appointment.slot_start == slot_start,
            Appointment.status == AppointmentStatus.SCHEDULED,
        )
        .first()
    )
    if conflict:
        destination = "/patient?error=slot_taken"
        if is_htmx:
            response = HTMLResponse(content="", status_code=200)
            response.headers["HX-Redirect"] = destination
            return response
        return RedirectResponse(destination, status_code=303)

    appointment = Appointment(
        patient_id=user.id,
        doctor_id=doctor_id,
        slot_start=slot_start,
        status=AppointmentStatus.SCHEDULED,
    )
    db.add(appointment)
    db.commit()

    if is_htmx:
        response = HTMLResponse(content="", status_code=200)
        response.headers["HX-Redirect"] = "/patient"
        return response
    return RedirectResponse("/patient", status_code=303)


@router.post("/appointments/{appointment_id}/cancel", response_class=HTMLResponse)
def cancel_appointment(request: Request, appointment_id: int, db: Session = Depends(get_db)):
    user = _require_patient(request, db)
    if user is None:
        return RedirectResponse("/login", status_code=303)

    appointment = db.get(Appointment, appointment_id)
    if appointment and appointment.patient_id == user.id and appointment.status == AppointmentStatus.SCHEDULED:
        appointment.status = AppointmentStatus.CANCELLED
        db.commit()

    return RedirectResponse("/patient", status_code=303)
