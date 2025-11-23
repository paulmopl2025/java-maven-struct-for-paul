# Vet Clinic CLI

This is a Terminal User Interface (TUI) client for the Vet Clinic Management System.

## Features
*   **Authentication**: Login with username/password (JWT based).
*   **Dashboard**: Overview of system modules.
*   **Owners Management**: List, Create, Delete owners.
*   **Pets Management**: List, Create, Delete pets.
*   **Appointments Management**: List, Create, Complete, Cancel appointments.
*   **Medical History**: View pet medical records (Planned).
*   **Clinic Stats**: View real-time clinic statistics.

## Requirements
*   Java 17+
*   Backend API running on `http://localhost:8081`

## How to Run

### Using the convenience script (Root directory)
```bash
./run_cli.sh
```

### Manually using Maven
```bash
cd cli
mvn clean package
java -jar target/vetclinic-cli-1.0-SNAPSHOT-shaded.jar
```

## Navigation
*   Use **Mouse** to click buttons and select table rows.
*   Use **Tab** / **Shift+Tab** to navigate focus.
*   Use **Enter** to activate buttons.
