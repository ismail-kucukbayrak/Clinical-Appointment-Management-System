from sqlalchemy import Boolean, Column, DateTime, Float, ForeignKey, Integer, String, func
from sqlalchemy.orm import relationship

from app.database import Base


class Report(Base):
    __tablename__ = "reports"

    id = Column(Integer, primary_key=True, index=True)
    appointment_id = Column(Integer, ForeignKey("appointments.id"), unique=True, nullable=False)

    prescription_code = Column(String, nullable=False)
    notes = Column(String, nullable=False)
    fee = Column(Float, nullable=False)
    is_paid = Column(Boolean, nullable=False, default=False)
    created_at = Column(DateTime, server_default=func.now())

    appointment = relationship("Appointment", back_populates="report")
