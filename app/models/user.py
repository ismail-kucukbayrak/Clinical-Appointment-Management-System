import enum

from sqlalchemy import Column, Integer, String, Enum
from sqlalchemy.orm import relationship

from app.database import Base


class UserRole(str, enum.Enum):
    ADMIN = "admin"
    PATIENT = "patient"
    DOCTOR = "doctor"
    BILLING_OFFICER = "billing_officer"


class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    role = Column(Enum(UserRole), nullable=False, index=True)

    # Admin logs in with username/password. Patient/Doctor/Billing Officer log in
    # with national_id/password. Exactly one of (username, national_id) is set
    # depending on the role.
    username = Column(String, unique=True, nullable=True, index=True)
    national_id = Column(String, unique=True, nullable=True, index=True)

    full_name = Column(String, nullable=False)
    password_hash = Column(String, nullable=False)

    doctor_profile = relationship(
        "Doctor",
        back_populates="user",
        uselist=False,
        cascade="all, delete-orphan",
    )
