from sqlalchemy import Column, ForeignKey, Integer
from sqlalchemy.orm import relationship

from app.database import Base


class Doctor(Base):
    __tablename__ = "doctors"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), unique=True, nullable=False)
    polyclinic_id = Column(Integer, ForeignKey("polyclinics.id"), nullable=False)

    user = relationship("User", back_populates="doctor_profile")
    polyclinic = relationship("Polyclinic", back_populates="doctors")
    appointments = relationship("Appointment", back_populates="doctor", cascade="all, delete-orphan")
