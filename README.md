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
⚙️ Setup Database (SQL Server Required)

⚠️ You must install SQL Server (or SQL Server Express).

Step 1: Create Database

Open SQL Server Management Studio (SSMS)

Run:

CREATE DATABASE MessengerDB;
Step 2: Run Database Script

Open file:

backend/database/init.sql

Copy all content → Paste into SSMS → Execute.

This will create:

Tables

Relationships

Required data structure

🖥 Run Backend

Inside backend folder:

./mvnw spring-boot:run

Backend runs at:

http://localhost:8080
🌐 Run Frontend

Inside frontend folder:

npm install
npm run dev

Frontend runs at:

http://localhost:3000
👨‍💻 Author

Spring Boot | Redis | SQL Server | React
