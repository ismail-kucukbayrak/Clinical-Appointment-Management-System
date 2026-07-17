import enum

from sqlalchemy import Column, DateTime, Enum, ForeignKey, Integer
from sqlalchemy.orm import relationship

from app.database import Base


class AppointmentStatus(str, enum.Enum):
    SCHEDULED = "scheduled"
    COMPLETED = "completed"
    CANCELLED = "cancelled"


class Appointment(Base):
    __tablename__ = "appointments"

    id = Column(Integer, primary_key=True, index=True)
    patient_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    doctor_id = Column(Integer, ForeignKey("doctors.id"), nullable=False)
    slot_start = Column(DateTime, nullable=False, index=True)
    status = Column(Enum(AppointmentStatus), nullable=False, default=AppointmentStatus.SCHEDULED)

    patient = relationship("User", foreign_keys=[patient_id])
    doctor = relationship("Doctor", back_populates="appointments")
    report = relationship(
        "Report",
        back_populates="appointment",
        uselist=False,
        cascade="all, delete-orphan",
    )
