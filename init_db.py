from app import db, app

with app.app_context():
    db.create_all()
    print("Database tables created (if they didn't exist).")
