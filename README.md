# GameLibrary (Java MVC)

A desktop MVC application to manage your game collection. Backed by PostgreSQL with Flyway migrations and JDBC.

## Tech stack
- Java 21
- Gradle (Application + Flyway)
- PostgreSQL (via Docker)
- Flyway for database migrations
- JDBC for database access

## Prerequisites
- Java Development Kit (JDK) 21 installed and on PATH
- Docker Desktop installed and running
- Docker Compose (bundled with Docker Desktop)
- Gradle or the Gradle Wrapper (`./gradlew` on Linux/macOS, `gradlew.bat` on Windows)

## Quick start

1) Start the database (from the project root):
   - Linux/macOS/Windows:
     ```
     docker compose up -d
     ```
   Notes:
   - The database will be available on `localhost:5432`.
   - Default credentials:
     - Database: `gameslibrary`
     - User: `games_user`
     - Password: `games_pass`

2) Run database migrations (creates/updates tables):
   - With Gradle Wrapper:
     ```
     ./gradlew migrate
     ```
   - Or with a local Gradle installation:
     ```
     gradle migrate
     ```
   This executes the Gradle task under `tasks > application > migrate`.

3) Start the application:
   - With Gradle Wrapper:
     ```
     ./gradlew run
     ```
   - Or with a local Gradle installation:
     ```
     gradle run
     ```
   This executes the Gradle task under `tasks > application > run`.

The GUI should open and the application will connect to PostgreSQL using the default settings above.

## Useful Gradle tasks
- `migrate` — Run Flyway migrations (group: application)
- `run` — Launch the application (group: application)

You can also run these from your IDE’s Gradle tool window.

## Environment and configuration
- JDBC URL (default): `jdbc:postgresql://localhost:5432/gameslibrary`
- Username: `games_user`
- Password: `games_pass`

If you change any of these (e.g., in Docker or local PostgreSQL), update your Gradle/Flyway configuration and application configuration accordingly.

## Stopping and cleaning the database
- Stop containers:
  ```
  docker compose down
  ```
- Remove volumes and data (DANGER: deletes all DB data):
  ```
  docker compose down -v
  ```

## Troubleshooting
- Docker not running: Ensure Docker Desktop is started before running any commands.
- Port 5432 already in use: Stop other PostgreSQL instances or change the exposed port in docker compose and the JDBC URL accordingly.
- Migrations fail or app cannot connect:
  - Verify the container is healthy:
    ```
    docker compose ps
    docker compose logs db
    ```
  - Confirm credentials and URL match the values above.
  - Re-run migrations: `./gradlew migrate`.

## Project overview
- Pattern: Model–View–Controller (MVC)
- Capabilities: add, update, get, delete, and filter games in your library
