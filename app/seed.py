from sqlalchemy.orm import Session

from app.config import DEFAULT_ADMIN_PASSWORD, DEFAULT_ADMIN_USERNAME
from app.models import User, UserRole
from app.security import hash_password


def seed_admin(db: Session) -> None:
    existing = db.query(User).filter(User.role == UserRole.ADMIN).first()
    if existing:
        return
    admin = User(
        role=UserRole.ADMIN,
        username=DEFAULT_ADMIN_USERNAME,
        full_name="System Administrator",
        password_hash=hash_password(DEFAULT_ADMIN_PASSWORD),
    )
    db.add(admin)
    db.commit()
