from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles

from app.database import Base, SessionLocal, engine
from app.routers import admin, auth, billing_officer, doctor, patient
from app.seed import seed_admin

app = FastAPI(title="Hospital Appointment System")

app.mount("/static", StaticFiles(directory="app/static"), name="static")

app.include_router(auth.router)
app.include_router(patient.router)
app.include_router(doctor.router)
app.include_router(billing_officer.router)
app.include_router(admin.router)


@app.on_event("startup")
def on_startup():
    Base.metadata.create_all(bind=engine)
    db = SessionLocal()
    try:
        seed_admin(db)
    finally:
        db.close()
