# resource-tracker
## Description
**resource-tracker** is a spring boot microservices-based platform developed to 
manage Resources (Employees) data for **I-Ray IT Solutions.** The system stores 
and manages data related to Employee Attendance, Timesheet Management, Leave 
Requests, Payrolls, Interview & Candidate tracking.

---

## Tech Stack
- **Java:** 17  
- **Spring Boot:** 3.2.1 
- **Spring Data JPA:** (Hibernate)  
- **Database:** PostgreSQL (Aiven Cloud)
- **Build Tool:** Maven 3.2.3

## Build
To build the project without running tests:
`mvn clean install -DskipTests=true`

---

## Deployment

### Cloud Infrastructure
| Component | Service |
|-----------|---------|
| Backend | Azure App Service (Central India) |
| Database | PostgreSQL on Aiven Cloud |
| Frontend | Vercel |

### Backend URL
`https://candiate-tracker-aea8hqfwbxd4dqhu.centralindia-01.azurewebsites.net`

### Frontend URL
`https://candidate-tracker-application.vercel.app`

---

## CI/CD Pipeline (GitHub Actions → Azure)

The backend is automatically deployed to Azure App Service on every push 
to the `main` branch via GitHub Actions.

**Workflow file:** `.github/workflows/main_candiate-tracker.yml`

```yaml
name: Build and deploy JAR app to Azure Web App - candiate-tracker

on:
  push:
    branches:
      - main
  workflow_dispatch:

jobs:
  build:
    runs-on: windows-latest
    permissions:
      contents: read
    steps:
      - uses: actions/checkout@v4
      - name: Set up Java version
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'microsoft'
      - name: Build with Maven
        run: mvn clean install
      - name: Upload artifact for deployment job
        uses: actions/upload-artifact@v4
        with:
          name: java-app
          path: '${{ github.workspace }}/target/*.jar'

  deploy:
    runs-on: windows-latest
    needs: build
    permissions:
      id-token: write
      contents: read
    steps:
      - name: Download artifact from build job
        uses: actions/download-artifact@v4
        with:
          name: java-app
      - name: Login to Azure
        uses: azure/login@v2
        with:
          client-id: ${{ secrets.__clientidsecretname__ }}
          tenant-id: ${{ secrets.__tenantidsecretname__ }}
          subscription-id: ${{ secrets.__subscriptionidsecretname__ }}
      - name: Deploy to Azure Web App
        uses: azure/webapps-deploy@v3
        with:
          app-name: 'candiate-tracker'
          slot-name: 'Production'
          package: '*.jar'
```

---

## Database Connectivity (Aiven PostgreSQL)
> 💡 Database credentials are configured as Environment Variables 
> in Azure App Service. Do not hardcode credentials in the codebase.

| Property | Value |
|----------|-------|
| Driver | `org.postgresql.Driver` |
| Port | `5432` |
| Database | Aiven PostgreSQL |

**Environment Variables set in Azure App Service:**
- `DB_URL`
- `DB_USERNAME`  
- `DB_PASSWORD`

---

## JPA & Hibernate Settings
- **Dialect:** `org.hibernate.dialect.PostgreSQLDialect`
- **Show SQL:** `true`
- **DDL Auto:** `none` (tables managed manually)

---

## Port Settings
- **server.port:** `8098`
- **Azure Websites Port:** `8098` (set via `WEBSITES_PORT` in App Service)

---

## Login to the Application
1. Go to `https://candidate-tracker-application.vercel.app`
2. Enter credentials:
   - **Username:** `vihansh052025@gmail.com`
   - **Password:** `Password@123`

---

## Swagger Links

### Deployed Environment (Azure)
- [https://candiate-tracker-aea8hqfwbxd4dqhu.centralindia-01.azurewebsites.net/swagger-ui/index.html](https://candiate-tracker-aea8hqfwbxd4dqhu.centralindia-01.azurewebsites.net/swagger-ui/index.html)

### Local Environment
- [http://localhost:8098/swagger-ui/index.html](http://localhost:8098/swagger-ui/index.html)

---

## Key API Endpoints

### Users
- **POST** `/api/v1/user/login` — Login users based on credentials
- **PUT** `/api/v1/user/changePassword` — Change password for logged in user

### Resources
- **GET** `/api/v1/resource/list` — Retrieves all resources
- **POST** `/api/v1/resource/upload` — Creates a new resource

### Projects
- **POST** `/api/v1/projects/createproject` — Creates a new project
- **GET** `/api/v1/projects/list` — Lists all projects

### Openings
- **POST** `/api/v1/openings` — Creates a new Opening
- **GET** `/api/v1/openings` — Retrieves all openings

### Attachments
- **POST** `/api/v1/attachment/upload` — Creates an attachment
- **GET** `/api/v1/attachment/{id}` — Retrieves attachment of resource
