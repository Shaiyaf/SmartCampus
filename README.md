# SmartCampus REST API

## Overview

SmartCampus is a Java REST API for managing campus rooms, sensors assigned to rooms, and time-stamped sensor readings. The application is built with **JAX-RS**, uses **Jersey 2.44** as the REST implementation, serializes JSON using **Jackson**, and is packaged as a **WAR** file for deployment to a servlet container such as **Apache Tomcat 9**.

The intended API entry point is versioned under:

```text
/api/v1
```

This versioned base path is defined in `AppConfig.java`.

---

## API Design Overview

### 1. Resource-oriented structure
The API follows a simple REST-style resource model:

- **Rooms** are top-level resources.
- **Sensors** are top-level resources, but each sensor must belong to an existing room.
- **Sensor readings** are nested resources under a specific sensor.

Relationship model:

```text
Room -> Sensor -> Sensor Reading
```

### 2. Main endpoints
The implemented endpoints are:

- `GET /api/v1` - discovery endpoint
- `GET /api/v1/rooms` - list all rooms
- `POST /api/v1/rooms` - create a room
- `GET /api/v1/rooms/{roomId}` - fetch one room by ID
- `DELETE /api/v1/rooms/{roomId}` - delete a room if it has no sensors assigned
- `GET /api/v1/sensors` - list all sensors
- `GET /api/v1/sensors?type=temperature` - filter sensors by type
- `POST /api/v1/sensors` - create a sensor linked to a room
- `GET /api/v1/sensors/{sensorId}/readings` - get readings for a sensor
- `POST /api/v1/sensors/{sensorId}/readings` - add a reading for a sensor

### 3. Discovery-first entry point
The root endpoint returns a JSON discovery document containing:

- API version
- administrative contact information
- links to primary resource collections

This makes the API easier to explore and test.

### 4. Core data models

#### Room
```json
{
  "id": "R101",
  "name": "Embedded Systems Lab",
  "building": "Engineering Block",
  "sensorIds": []
}
```

#### Sensor
```json
{
  "id": "S1001",
  "type": "temperature",
  "roomId": "R101",
  "status": "ACTIVE",
  "currentValue": 24.6
}
```

#### SensorReading
```json
{
  "timestamp": "2026-04-23T10:30:00Z",
  "value": 24.6
}
```

#### ErrorResponse
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Room not found."
}
```

### 5. In-memory storage design
The application uses a shared in-memory `DataStore` rather than a database. It stores:

- rooms in `ConcurrentHashMap<String, Room>`
- sensors in `ConcurrentHashMap<String, Sensor>`
- readings in `ConcurrentHashMap<String, List<SensorReading>>`

This design is suitable for coursework and local testing because it avoids database setup while still supporting concurrent access.

### 6. Validation and business rules
The API enforces these rules:

- a room must have an `id`
- a sensor must have an `id` and a `roomId`
- a sensor can only be created if the referenced room exists
- a room cannot be deleted while it still contains assigned sensors
- a reading cannot be added to a missing sensor
- a reading cannot be added when the sensor status is `MAINTENANCE`
- posting a reading updates the parent sensor's `currentValue`

### 7. Error handling design
The API returns structured JSON errors using custom exception mappers and a shared `ErrorResponse` model.

Current error mapping includes:

- **400 Bad Request** - invalid request body or missing fields
- **403 Forbidden** - sensor is under maintenance
- **404 Not Found** - missing room or sensor
- **409 Conflict** - duplicate resource or room deletion blocked because sensors are attached
- **422 Unprocessable Entity** - linked room does not exist when creating a sensor
- **500 Internal Server Error** - unexpected server-side failure

---

## Technology Stack

Based on `pom.xml`, the project uses:

- **Java 8**
- **Maven**
- **WAR packaging**
- **JAX-RS / Java EE 8 API**
- **Jersey 2.44**
- **Jersey HK2 injection support**
- **Jersey Jackson JSON support**
- **Servlet API 4.0.1**
- **Apache Tomcat 9** for deployment

### Maven coordinates

```xml
<groupId>com.mycompany</groupId>
<artifactId>SmartCampus</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>war</packaging>
```

### Build output
The build is configured with:

```xml
<finalName>SmartCampus</finalName>
```

So the generated deployable artifact is:

```text
target/SmartCampus.war
```

---

## Installation Instructions

### Prerequisites
Install the following software before building the project:

1. **Java JDK 8**
2. **Apache Maven 3.x**
3. **Apache Tomcat 9.x**
4. **Git** (optional, if cloning from GitHub)
5. **Postman** (optional, for API testing)

Tomcat 9 is the safest choice because this project uses the `javax.*` namespace rather than the newer Jakarta 10+ `jakarta.*` namespace.

### 1. Install Java JDK 8
Download and install a JDK 8 distribution on your machine.

After installation, verify it:

```bash
java -version
javac -version
```

If needed, configure:

- `JAVA_HOME` to point to your JDK installation directory
- add `%JAVA_HOME%\bin` on Windows or `$JAVA_HOME/bin` on Linux/macOS to your `PATH`

### 2. Install Apache Maven
Download and install Maven 3.x.

After installation, verify it:

```bash
mvn -version
```

If needed, configure:

- `MAVEN_HOME` to point to the Maven installation directory
- add Maven's `bin` folder to your `PATH`

### 3. Install Apache Tomcat 9
Download and extract or install Apache Tomcat 9.

Typical setup steps:

- extract the Tomcat archive to a folder such as `C:\apache-tomcat-9` on Windows or `/opt/apache-tomcat-9` on Linux
- identify the `webapps` folder inside the Tomcat directory
- identify the startup script inside the `bin` folder

### 4. Optional tools
For easier testing and demonstration, install:

- **Postman** for sending API requests
- **Visual Studio Code / NetBeans / IntelliJ IDEA** for viewing or editing the code

---

## Project Setup

### 1. Clone the repository
```bash
git clone <your-github-repository-url>
cd SmartCampus
```

If you are not using Git, you can simply download the project ZIP and extract it.

### 2. Review the project structure
```text
SmartCampus
├── pom.xml
├── src/
│   └── main/
│       └── java/
│           └── com/mycompany/smartcampus/
│               ├── AppConfig.java
│               ├── DataStore.java
│               ├── JAXRSConfiguration.java
│               ├── exceptions/
│               ├── filters/
│               ├── mappers/
│               ├── models/
│               └── resources/
└── target/
```

### 3. Recommended cleanup before submission
The codebase currently contains **two** JAX-RS configuration classes:

- `AppConfig.java` with `@ApplicationPath("/api/v1")`
- `JAXRSConfiguration.java` with `@ApplicationPath("resources")`

For a clean submission, it is better to keep only **one** active application configuration. The intended one for this project is `AppConfig.java`, because it matches the coursework requirement for a versioned `/api/v1` entry point.

---

## How to Build and Launch the Server

### Step 1. Check Java and Maven
```bash
java -version
mvn -version
```

### Step 2. Build the WAR file
```bash
mvn clean package
```

If the build succeeds, Maven generates:

```text
target/SmartCampus.war
```

### Step 3. Copy the WAR file to Tomcat
Copy the generated WAR file into Tomcat's `webapps` directory.

#### Windows
```bat
copy target\SmartCampus.war C:\path\to\apache-tomcat-9\webapps\
```

#### Linux / macOS
```bash
cp target/SmartCampus.war /path/to/apache-tomcat-9/webapps/
```

### Step 4. Start Tomcat
#### Windows
```bat
C:\path\to\apache-tomcat-9\bin\startup.bat
```

#### Linux / macOS
```bash
/path/to/apache-tomcat-9/bin/startup.sh
```

### Step 5. Confirm deployment
After deployment, the application context is usually:

```text
http://localhost:8080/SmartCampus
```

Because the API base path is `/api/v1`, the full API base URL becomes:

```text
http://localhost:8080/SmartCampus/api/v1
```

### Step 6. Test the root endpoint
```bash
curl -i http://localhost:8080/SmartCampus/api/v1
```

If the server is running correctly, the response should return JSON containing the API version, contact, and resource links.

### Step 7. Redeployment tip
If Tomcat continues serving an old version, stop Tomcat, delete the old exploded app folder and old WAR from `webapps`, copy the new `SmartCampus.war`, and start Tomcat again.

---

## Sample curl Commands

Assume the following base URL:

```bash
BASE_URL=http://localhost:8080/SmartCampus/api/v1
```

### 1. Discovery endpoint
```bash
curl -i "$BASE_URL"
```

### 2. Create a room
```bash
curl -i -X POST "$BASE_URL/rooms" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "R101",
    "name": "Embedded Systems Lab",
    "building": "Engineering Block",
    "sensorIds": []
  }'
```

### 3. List all rooms
```bash
curl -i "$BASE_URL/rooms"
```

### 4. Get a single room by ID
```bash
curl -i "$BASE_URL/rooms/R101"
```

### 5. Create a sensor linked to an existing room
```bash
curl -i -X POST "$BASE_URL/sensors" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "S1001",
    "type": "temperature",
    "roomId": "R101",
    "status": "ACTIVE"
  }'
```

### 6. List all sensors
```bash
curl -i "$BASE_URL/sensors"
```

### 7. Filter sensors by type
```bash
curl -i "$BASE_URL/sensors?type=temperature"
```

### 8. Add a reading to a sensor
```bash
curl -i -X POST "$BASE_URL/sensors/S1001/readings" \
  -H "Content-Type: application/json" \
  -d '{
    "timestamp": "2026-04-23T10:30:00Z",
    "value": 24.6
  }'
```

### 9. List readings for a sensor
```bash
curl -i "$BASE_URL/sensors/S1001/readings"
```

### 10. Create and delete an empty room
Create the room:

```bash
curl -i -X POST "$BASE_URL/rooms" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "R102",
    "name": "Meeting Room A",
    "building": "Admin Block",
    "sensorIds": []
  }'
```

Delete the room:

```bash
curl -i -X DELETE "$BASE_URL/rooms/R102"
```

---

## Example Error Cases

### Create a sensor for a room that does not exist
```bash
curl -i -X POST "$BASE_URL/sensors" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "S2001",
    "type": "humidity",
    "roomId": "R999",
    "status": "ACTIVE"
  }'
```

Expected result: **422 Unprocessable Entity**

### Add a reading to a sensor under maintenance
```bash
curl -i -X POST "$BASE_URL/sensors" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "S3001",
    "type": "co2",
    "roomId": "R101",
    "status": "MAINTENANCE"
  }'

curl -i -X POST "$BASE_URL/sensors/S3001/readings" \
  -H "Content-Type: application/json" \
  -d '{
    "timestamp": "2026-04-23T11:00:00Z",
    "value": 50.2
  }'
```

Expected result: **403 Forbidden**

### Delete a room that still has sensors assigned
```bash
curl -i -X DELETE "$BASE_URL/rooms/R101"
```

Expected result: **409 Conflict**

---

## Coursework Discussion and Answers

### Part 1. Service Architecture and Application Configuration

#### Q1. What is the default lifecycle of a JAX-RS resource class, and how does it affect in-memory data handling?
By default, JAX-RS resource classes are typically instantiated per request rather than being shared as singletons. In this project, `AppConfig` registers resource classes such as `DiscoveryResource`, `RoomResource`, and `SensorResource`, which supports the normal request-scoped lifecycle. This means a new resource object can be created for each incoming request.

However, the main concurrency issue in this project is not the resource instance itself but the shared static in-memory data inside `DataStore`. Rooms, sensors, and readings are stored in shared collections that can be accessed by many requests at the same time. To reduce race conditions and data corruption, the implementation uses `ConcurrentHashMap` for the main maps and synchronized lists for sensor reading history. This is appropriate for coursework and local testing, but some multi-step operations are still not fully atomic. In a larger production system, stronger transactional control or persistent database support would be preferable.

#### Q2. Why is hypermedia considered a hallmark of advanced RESTful design?
Hypermedia is important in REST because it allows the server to guide the client through the API by exposing navigational links and entry points inside responses. In this project, the root discovery endpoint returns the API version, contact information, and links to the main resource collections such as `/api/v1/rooms` and `/api/v1/sensors`.

This reduces client-side coupling to hard-coded URLs and static documentation. Instead of relying only on external documentation, a client can start from the root and discover what resources are available. Although this project implements a simple discovery response rather than full HATEOAS across every resource, it still improves usability and makes the API easier to explore.

### Part 2. Room Management

#### Q3. What are the implications of returning only room IDs versus full room objects?
In this implementation, `GET /api/v1/rooms` returns full `Room` objects rather than only IDs. Returning full room objects gives the client immediate access to useful metadata such as room name, building, and `sensorIds`, which reduces the need for extra requests.

Returning only IDs would make the response smaller and save bandwidth, especially if the dataset were very large. However, it would also increase client-side work because the client would have to send additional requests to retrieve each room's details. For this coursework project, returning full room objects is a practical choice because the data volume is small and the richer response makes testing and integration easier.

#### Q4. Is the DELETE operation idempotent in this implementation?
Yes, the `DELETE /api/v1/rooms/{roomId}` operation is idempotent in terms of final system state. If the room exists and is empty, the first DELETE request removes it. If the same DELETE request is sent again, the room no longer exists, so the API returns `404 Not Found`. Although the second response is different, the server state remains the same after the first successful deletion.

If the room still has sensors assigned, the API throws `RoomNotEmptyException` and returns `409 Conflict`. Repeating the same DELETE request produces the same outcome until the room becomes empty. Therefore, repeated DELETE requests do not create extra side effects, which supports idempotency.

### Part 3. Sensor Operations and Linking

#### Q5. What happens if a client sends data in a format other than JSON when `@Consumes(MediaType.APPLICATION_JSON)` is used?
The POST endpoints are annotated with `@Consumes(MediaType.APPLICATION_JSON)`, which means they are designed to accept only JSON request bodies. If a client sends a different content type such as `text/plain` or `application/xml`, the request no longer matches the declared media type.

In normal JAX-RS behaviour, the framework rejects such a request before the resource method executes, typically with `415 Unsupported Media Type`. This is useful because it enforces a clear contract between the client and the server and prevents invalid payload formats from being deserialized into Java objects.

#### Q6. Why is `@QueryParam` generally better than putting the filter in the URL path?
The filtering logic is implemented as `GET /api/v1/sensors?type=CO2` using `@QueryParam("type")`. This is generally better because the client is still addressing the same collection resource, `/sensors`, while applying an optional filter to the results.

A path-based design such as `/api/v1/sensors/type/CO2` makes the filter look like a fixed sub-resource rather than a search or filtering condition. Query parameters are more flexible and semantically more appropriate for optional filtering, searching, sorting, and pagination. They also scale better when multiple filters are needed.

### Part 4. Deep Nesting with Sub-Resources

#### Q7. What are the architectural benefits of the Sub-Resource Locator pattern?
In this project, `SensorResource` delegates `/{sensorId}/readings` to a dedicated `SensorReadingResource` class. This separates sensor-level operations from reading-level operations and keeps each class focused on a smaller responsibility.

The main advantage is better separation of concerns. If all nested endpoints were placed inside one very large controller class, the code would become harder to read, maintain, and test. By delegating reading-related logic to `SensorReadingResource`, the API structure becomes clearer and easier to extend in future.

### Part 5. Advanced Error Handling and Exception Mapping

#### Q8. Why is HTTP 422 more accurate than 404 when a linked resource inside valid JSON does not exist?
When a client posts a new sensor with a `roomId` that does not exist, the request is still sent to a valid endpoint and the JSON structure itself is valid. The problem is semantic: the linked room reference inside the payload cannot be resolved.

For that reason, `422 Unprocessable Entity` is more accurate than `404 Not Found`. A `404` usually means that the requested URL itself does not exist. In this case, `/api/v1/sensors` does exist; only the referenced room inside the request body is invalid.

#### Q9. Why is exposing Java stack traces a cybersecurity risk?
Exposing raw Java stack traces to external API consumers is risky because stack traces can reveal internal implementation details such as package names, class names, method names, framework usage, exception types, and even file paths or line numbers. An attacker could use this information to understand the technology stack and identify possible weaknesses more easily.

To reduce this risk, the project includes a catch-all `ExceptionMapper<Throwable>` that returns a generic `500 Internal Server Error` response with a safe message rather than exposing internal details to the client. This preserves security while still allowing server-side debugging.

---

## Possible Future Improvements

This project can be extended further by adding:

- persistent database storage instead of in-memory maps
- update endpoints such as `PUT` or `PATCH`
- sensor deletion endpoints
- cleanup of duplicate JAX-RS configuration classes
- explicit registration of optional filters and extra resources
- unit tests and integration tests
- Swagger or OpenAPI documentation
- Docker support for easier deployment

---

## Conclusion

SmartCampus demonstrates a clear REST API design using Java, JAX-RS, Jersey, and Maven. It includes versioning, structured JSON responses, validation rules, filtering, nested resources, and custom exception mapping. Because the project is packaged as a WAR and configured through Maven, it can be built locally and deployed on Tomcat for testing or coursework submission.

