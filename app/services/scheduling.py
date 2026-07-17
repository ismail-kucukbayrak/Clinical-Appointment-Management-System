from datetime import date, datetime, timedelta

from sqlalchemy.orm import Session

from app.config import CLINIC_CLOSE_HOUR, CLINIC_OPEN_HOUR, SLOT_MINUTES
from app.models import Appointment, AppointmentStatus

MAX_BOOKING_DAYS_AHEAD = 14


def generate_daily_slots(day: date) -> list[datetime]:
    slots = []
    current = datetime.combine(day, datetime.min.time()).replace(hour=CLINIC_OPEN_HOUR)
    end = datetime.combine(day, datetime.min.time()).replace(hour=CLINIC_CLOSE_HOUR)
    while current < end:
        slots.append(current)
        current += timedelta(minutes=SLOT_MINUTES)
    return slots


def get_available_slots(db: Session, doctor_id: int, day: date) -> list[datetime]:
    all_slots = generate_daily_slots(day)

    day_start = datetime.combine(day, datetime.min.time())
    day_end = day_start + timedelta(days=1)

    taken = (
        db.query(Appointment.slot_start)
        .filter(
            Appointment.doctor_id == doctor_id,
            Appointment.status == AppointmentStatus.SCHEDULED,
            Appointment.slot_start >= day_start,
            Appointment.slot_start < day_end,
        )
        .all()
    )
    taken_times = {row[0] for row in taken}

    now = datetime.now()
    return [slot for slot in all_slots if slot not in taken_times and slot > now]


def get_bookable_days() -> list[date]:
    today = date.today()
    days = []
    for offset in range(MAX_BOOKING_DAYS_AHEAD):
        day = today + timedelta(days=offset)
        if day.weekday() < 5:  # Monday-Friday only
            days.append(day)
    return days
