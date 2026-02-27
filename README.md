<h1 align="center">💬 Messenger Clone</h1>

<p align="center">
  Real-time chat application built with <b>Spring Boot</b> and <b>React</b>
</p>

<hr/>

<h2>🚀 Tech Stack</h2>

<ul>
  <li><b>Backend:</b> Spring Boot, Spring Security (JWT), WebSocket</li>
  <li><b>Database:</b> SQL Server</li>
  <li><b>Cache:</b> Redis</li>
  <li><b>Frontend:</b> React</li>
</ul>

<hr/>

<h2>📁 Project Structure</h2>

<pre>
backend/
 ├── src/
 │    └── main/
 │         └── resources/
 │              └── database/
 │                   └── init.sql
 ├── pom.xml

frontend/
 ├── src/
 ├── package.json
</pre>

<hr/>

<h2>🛠 Database Setup</h2>

<p><b>Requirement:</b> SQL Server (or SQL Server Express)</p>

<h3>Step 1: Create Database</h3>

<p>Open <b>SQL Server Management Studio (SSMS)</b> and run:</p>

<pre>
CREATE DATABASE MessengerDB;
</pre>

<h3>Step 2: Run Database Script</h3>

<p>Open file:</p>

<pre>
backend/src/main/resources/database/init.sql
</pre>

<p>
Copy all content → Paste into SSMS → Click <b>Execute</b>
</p>

<hr/>

<h2>▶ Run Project</h2>

<h3>🔹 Backend</h3>

<pre>
mvn spring-boot:run
</pre>

<h3>🔹 Frontend</h3>

<pre>
npm install
npm run dev
</pre>

<hr/>

<p align="center">
  ✨ Simple Messenger Clone for learning & development purposes
</p>
