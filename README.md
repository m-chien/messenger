<h1 align="center">💬 Messenger Clone - Backend</h1>

<p align="center">
  Spring Boot REST API & WebSocket Server for Messenger Clone
</p>

<hr/>

<h2>🚀 Tech Stack</h2>

<ul>
  <li><b>Spring Boot</b></li>
  <li><b>Spring Security (JWT)</b></li>
  <li><b>WebSocket</b></li>
  <li><b>Redis</b></li>
  <li><b>SQL Server</b></li>
</ul>

<hr/>

<h2>📁 Important Structure</h2>

<pre>
src/
 └── main/
      └── resources/
           └── database/
                └── init.sql
</pre>

<hr/>

<h2>🛠 Database Setup</h2>

<p><b>Requirement:</b> Install SQL Server (or SQL Server Express)</p>

<h3>Step 1: Create Database</h3>

<pre>
CREATE DATABASE MessengerDB;
</pre>

<h3>Step 2: Run Database Script</h3>

<p>Open file:</p>

<pre>
src/main/resources/database/init.sql
</pre>

<p>
Copy all content → Paste into SQL Server Management Studio (SSMS) → Click <b>Execute</b>
</p>

<hr/>

<h2>⚙️ Configure Application</h2>

<p>
Update database connection in:
</p>

<pre>
src/main/resources/application.yaml
</pre>

<p>
Make sure your username, password, and database name match your SQL Server configuration.
</p>

<hr/>

<h2>▶ Run Backend</h2>

<h3>Using Maven</h3>

<pre>
mvn spring-boot:run
</pre>

<h3>Or using Maven Wrapper</h3>

<pre>
./mvnw spring-boot:run
</pre>

<hr/>

<p align="center">
  🚀 Backend server runs by default at: <b>http://localhost:8080</b>
</p>
