from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import relationship

from app.database import Base


class Polyclinic(Base):
    __tablename__ = "polyclinics"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, unique=True, nullable=False)

    doctors = relationship("Doctor", back_populates="polyclinic", cascade="all, delete-orphan")
