# resource-tracker

## Description

**resource-tracker** is a spring boot microservices-based platform developed to manage Resources (Employees) data for **I-Ray IT Solutions.** The system stores and manages data related to Employee Attendance, Timesheet Management, Leave Requests, Payrolls, Interview & Candidate tracking.

---

## Tech Stack

- **Java:** 17  
- **Spring Boot:** 3.2.1 
- **Spring Data JPA:** (Hibernate)  
- **Database:** Microsoft SQL Server 2012  
- **Build Tool:** Maven 3.2.3

## Build

To build the project without running tests:

`mvn clean install -DskipTests=true`

## AWS Security Groups IP Configuration

> 💡 Update your IP address in the relevant AWS Security Group inbound rules to ensure access to the database and backend services  

Go to https://console.aws.amazon.com/console/home and enter the below details:

  **AWS Account ID=** `058266308585`  
  **AWS IAM username=** `koppireddyprabhakar@gmail.com`  
  **AWS Account password=** `Check with Active Developers/DevOps Engineers`  
  
1. Go to `EC2` > `Instances` and select the `tracrat-dev-ubuntu (i-0d23a79508813a9fd)` instance.  
2. Navigate to the Security Group `sg-0d035f0e31975bd42` and click on `Edit Inbound Rules`.  
3. Add a `New Rule` with the type set to `MSSQL`, specify the updated IP address, and include an appropriate description.  
4. Click `Save Rules` to apply the changes and update the IP address.  

## Database Connectivity

  **spring.datasource.url=**  `jdbc:sqlserver://3.13.89.17:1433;databaseName=RESOURCES;encrypt=true;trustServerCertificate=true;`  
  **spring.datasource.username=** `gotracrat`  
  **spring.datasource.password=** `dev@123`  
  **spring.datasource.driver-class-name=** `com.microsoft.sqlserver.jdbc.SQLServerDriver`

## JPA & Hibernate Settings

  **spring.jpa.properties.hibernate.dialect=** `org.hibernate.dialect.SQLServerDialect`  
  **spring.jpa.hibernate.naming.physical-strategy=** `org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl`  
  **spring.jpa.show-sql=** `true`  

  **#spring.jpa.hibernate.ddl-auto=** `create`  
  > 💡  Use only for development purposes to auto-create tables based on entity definitions.

## Port Settings

  **server.port=** `8098`  
  
## To be able to login to the app following steps to be followed.
1. Go to http://localhost:3000/ or https://resourcetracker.gotracrat.in/ 
2. Enter the below credentials to login: 

   **Username:** `vihansh052025@gmail.com`  
   **Password:** `Password@123`

## Swagger Links  
  
### Deployed Environment (SYS)

- **Resource Tracker:** [https://resourcetracker.gotracrat.in:8098/swagger-ui/index.html](https://resourcetracker.gotracrat.in:8098/swagger-ui/index.html)  

### Local Environment

- **Resource Tracker:** [http://localhost:8098/swagger-ui/index.html](http://localhost:8098/swagger-ui/index.html)  


## Key API Endpoints  

### Users  
- **POST** `/api/v1/user/login`  
  Login users based on user credentials.
- **PUT** `/api/v1/user/changePassword`  
  Changes password for the login user.

### Resources  
- **GET** `/api/v1/resource/list`  
  Retrieves all resources from resource tracker.
- **POST** `/api/v1/resource/upload`  
  Creates a new resource.

### Projects  
- **POST** `/api/v1/projects/createproject`  
  Creates a new project.
- **GET** `/api/v1/projects/list`  
  Lists all the projects.

### Openings  
- **POST** `/api/v1/openings`  
  Creates a new Opening.
- **GET** `/api/v1/openings`  
  Retrieves details of all openings.

### Attachments  
- **POST** `/api/v1/attachment/upload`  
  Creates an attachment.
- **GET** `/api/v1/attachment/{id}`  
  Retrieves attachment of resource.
