import bcrypt
from itsdangerous import BadSignature, URLSafeTimedSerializer

from app.config import SECRET_KEY

_serializer = URLSafeTimedSerializer(SECRET_KEY, salt="session")

SESSION_MAX_AGE_SECONDS = 60 * 60 * 12  # 12 hours


def hash_password(plain_password: str) -> str:
    return bcrypt.hashpw(plain_password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")


def verify_password(plain_password: str, password_hash: str) -> bool:
    return bcrypt.checkpw(plain_password.encode("utf-8"), password_hash.encode("utf-8"))


def create_session_token(user_id: int) -> str:
    return _serializer.dumps({"user_id": user_id})


def read_session_token(token: str) -> int | None:
    try:
        data = _serializer.loads(token, max_age=SESSION_MAX_AGE_SECONDS)
    except BadSignature:
        return None
    return data.get("user_id")
