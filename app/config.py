import os
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent

DATABASE_URL = f"sqlite:///{BASE_DIR / 'hospital.db'}"

SECRET_KEY = os.environ.get("HOSPITAL_SECRET_KEY", "dev-secret-key-change-in-production")

SESSION_COOKIE_NAME = "hospital_session"

DEFAULT_ADMIN_USERNAME = "admin"
DEFAULT_ADMIN_PASSWORD = "admin"

CLINIC_OPEN_HOUR = 9
CLINIC_CLOSE_HOUR = 17
SLOT_MINUTES = 30
