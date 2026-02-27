💬 Messenger Clone

Real-time chat application built with Spring Boot and React.

🚀 Tech Stack

Spring Boot

Spring Security (JWT)

WebSocket

Redis

SQL Server

React

📂 Project Structure
backend/
├── src/
│   └── main/
│       └── resources/
│           └── database/
│               └── init.sql
├── pom.xml
└── ...

frontend/
├── src/
├── package.json
└── ...
🛠 Setup Database (SQL Server Required)

⚠ You must install SQL Server (or SQL Server Express).

1️⃣ Create Database

Open SQL Server Management Studio (SSMS) and run:

CREATE DATABASE MessengerDB;
2️⃣ Run Database Script

Open file:

backend/src/main/resources/database/init.sql

Copy all content → Paste into SSMS → Execute.

▶ Run Project
Backend
mvn spring-boot:run
Frontend
npm install
npm start
