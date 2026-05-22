from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import sqlite3
import platform
import socket
import psutil
import os
import random

app = FastAPI(title="NeilOS Backend")

# =========================
# CORS
# =========================

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

# =========================
# DATABASE
# =========================

db = sqlite3.connect("neilos.db", check_same_thread=False)
cur = db.cursor()

cur.execute("""
CREATE TABLE IF NOT EXISTS users(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE,
    balance INTEGER DEFAULT 0
)
""")

cur.execute("""
CREATE TABLE IF NOT EXISTS files(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    filename TEXT
)
""")

cur.execute("""
CREATE TABLE IF NOT EXISTS patients(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    age INTEGER,
    payment INTEGER
)
""")

db.commit()

# =========================
# MODELS
# =========================

class BankRequest(BaseModel):
    user: str
    amount: int

class FileRequest(BaseModel):
    filename: str

class PatientRequest(BaseModel):
    name: str
    age: int
    payment: int

class CommandRequest(BaseModel):
    command: str

class SearchRequest(BaseModel):
    query: str

class CyberRequest(BaseModel):
    ip: str

class AIRequest(BaseModel):
    message: str

# =========================
# ROOT
# =========================

@app.get("/")
def root():
    return {
        "system": "NeilOS",
        "status": "running"
    }

# =====================================================
# BANKING
# =====================================================

@app.get("/bank/{user}")
def balance(user: str):

    cur = db.cursor()

    cur.execute(
        "SELECT balance FROM users WHERE username=?",
        (user,)
    )

    row = cur.fetchone()

    if row:
        return {"user": user, "balance": row[0]}

    cur.execute(
        "INSERT INTO users(username,balance) VALUES(?,0)",
        (user,)
    )

    db.commit()

    return {"user": user, "balance": 0}

@app.post("/deposit")
def deposit(data: BankRequest):

    cur = db.cursor()

    cur.execute(
        "INSERT OR IGNORE INTO users(username,balance) VALUES(?,0)",
        (data.user,)
    )

    cur.execute(
        "UPDATE users SET balance=balance+? WHERE username=?",
        (data.amount, data.user)
    )

    db.commit()

    return {"status": "deposited"}

@app.post("/withdraw")
def withdraw(data: BankRequest):

    cur = db.cursor()

    cur.execute(
        "SELECT balance FROM users WHERE username=?",
        (data.user,)
    )

    row = cur.fetchone()

    if not row:
        return {"error": "user not found"}

    if row[0] < data.amount:
        return {"error": "insufficient balance"}

    cur.execute(
        "UPDATE users SET balance=balance-? WHERE username=?",
        (data.amount, data.user)
    )

    db.commit()

    return {"status": "withdrawn"}

# =====================================================
# FILE MANAGER
# =====================================================

@app.get("/files")
def list_files():

    cur = db.cursor()

    cur.execute("SELECT filename FROM files")

    rows = cur.fetchall()

    return {
        "files": [x[0] for x in rows]
    }

@app.post("/files/create")
def create_file(data: FileRequest):

    cur = db.cursor()

    cur.execute(
        "INSERT INTO files(filename) VALUES(?)",
        (data.filename,)
    )

    db.commit()

    return {
        "status": "created"
    }

# =====================================================
# TERMINAL
# =====================================================

@app.post("/terminal")
def terminal(data: CommandRequest):

    cmd = data.command.lower()

    if cmd == "help":
        return {
            "output":
            "help dir version cls files"
        }

    elif cmd == "version":
        return {
            "output":
            "NeilOS v1.0"
        }

    elif cmd == "dir":

        cur.execute("SELECT filename FROM files")

        files = cur.fetchall()

        return {
            "output":
            "\n".join([f[0] for f in files])
        }

    elif cmd == "files":

        cur.execute("SELECT filename FROM files")

        files = cur.fetchall()

        return {
            "output":
            "\n".join([f[0] for f in files])
        }

    return {
        "output":
        f"Unknown command: {cmd}"
    }

# =====================================================
# SEARCH ENGINE
# =====================================================

@app.post("/search")
def search(data: SearchRequest):

    return {
        "query": data.query,
        "url":
        f"https://duckduckgo.com/?q={data.query}"
    }

# =====================================================
# AI ASSISTANT
# =====================================================

@app.post("/ai")
def ai(data: AIRequest):

    text = data.message.lower()

    if "hello" in text:
        return {"response": "Hello User"}

    if "scan" in text:
        return {"response": "Opening Cyber Security Center"}

    if "bank" in text:
        return {"response": "Bank module online"}

    return {
        "response":
        "AI Ready"
    }

# =====================================================
# CYBER SECURITY
# =====================================================

@app.post("/cyber/scan")
def cyber_scan(data: CyberRequest):

    return {
        "target": data.ip,
        "firewall": "active",
        "ports": [22,80,443],
        "threat_level": "low",
        "status": "secure"
    }

@app.get("/cyber/threats")
def threats():

    return {
        "level": "low",
        "attacks": 0,
        "firewall": "enabled"
    }

# =====================================================
# CLINIC
# =====================================================

@app.post("/clinic/add")
def add_patient(data: PatientRequest):

    cur = db.cursor()

    cur.execute(
        """
        INSERT INTO patients(
        name,
        age,
        payment
        )
        VALUES(?,?,?)
        """,
        (
            data.name,
            data.age,
            data.payment
        )
    )

    db.commit()

    return {
        "status":
        "patient added"
    }

@app.get("/clinic/list")
def clinic_list():

    cur = db.cursor()

    cur.execute(
        """
        SELECT
        id,
        name,
        age,
        payment
        FROM patients
        """
    )

    rows = cur.fetchall()

    result = []

    for row in rows:
        result.append({
            "id": row[0],
            "name": row[1],
            "age": row[2],
            "payment": row[3]
        })

    return result

# =====================================================
# SOCIAL NET
# =====================================================

@app.get("/social/groups")
def groups():

    return {
        "groups":
        [[1],[0,2],[1]]
    }

@app.get("/social/recommend")
def recommend():

    return {
        "user":
        "cyber_user"
    }

# =====================================================
# NETWORK
# =====================================================

@app.get("/network")
def network():

    hostname = socket.gethostname()

    try:
        ip = socket.gethostbyname(hostname)
    except:
        ip = "127.0.0.1"

    return {
        "hostname": hostname,
        "ip": ip,
        "gateway": "active",
        "dns": "connected"
    }

# =====================================================
# DEPLOYMENT
# =====================================================

@app.post("/deploy")
def deploy():

    return {
        "status":
        "ISO generation started",
        "file":
        "NeilOS.iso"
    }

# =====================================================
# KERNEL
# =====================================================

@app.get("/kernel")
def kernel():

    return {
        "kernel": "NeilOS Kernel",
        "architecture": platform.machine(),
        "platform": platform.platform()
    }

# =====================================================
# SYSTEM MONITOR
# =====================================================

@app.get("/monitor")
def monitor():

    return {
        "cpu": psutil.cpu_percent(),
        "ram": psutil.virtual_memory().percent,
        "disk": psutil.disk_usage('/').percent
    }

# =====================================================
# API TEST
# =====================================================

@app.get("/api/test")
def api_test():

    return {
        "status": "online",
        "message": "NeilOS API running"
    }

# =====================================================
# START
# =====================================================

# uvicorn server:app --reload