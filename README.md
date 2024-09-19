# Reservations platform

This is the backend service of reservations platform api

## How to setup and run the project

### Prerequisites

You would need the following tools installed before running the project locally:

- Java 21
- Gradle
- IntelliJ IDEA (or any preferred IDE)
- Docker

### Configure environment variables

1. Create .env file in the root folder with database credentials:

```
DATABASE_NAME=reservations_db
DATABASE_USER=user
DATABASE_PASSWORD=password
```

2. Setup IntelliJ environment variables
- Run -> Edit Configurations, then under Environment Variables, you should add the following:

   ```
   DATABASE_NAME=reservations_db;DATABASE_USER=user;DATABASE_PASSWORD=password
   ```

### Start the project

- run `gradle clean build` in a terminal or in InteliJ Gradle menu to get all the needed dependencies and to build the project
- run `docker-compose up -d --build` in a terminal in the root folder
    - This command will start a Postgres DB and the App in a docker containers with the properties we've entered in the .env file
- The app should be running on localhost:8080


