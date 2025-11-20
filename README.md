# Travel Insurance Feature API (POC)

Spring Boot 3 service that tells the frontend which modules are available to an end user. It uses an in-memory catalog so you can demo feature toggles and navigation counts without touching a database.

## Requirements
- Java 21 (use `sdkman`, `asdf`, or Homebrew `openjdk@21`)
- Maven 3.9+

## Run locally
```bash
env MAVEN_OPTS="-Xmx512m" mvn spring-boot:run
```
Service listens on `http://localhost:9090`.

## Security
- HTTP Basic is required for all `/api/**` and docs endpoints. Default dev credentials are set in `src/main/resources/application.yml` under `app.security.username/password` (change them for any real deployment).
- CORS is restricted to `app.cors.allowed-origins` (defaults to `http://localhost:5173`); update the list to match your Vue hosts.
- Health and info endpoints remain public at `/actuator/health` and `/actuator/info`.
- Swagger UI and OpenAPI docs are public in dev for convenience (`/swagger-ui/**`, `/v3/api-docs/**`). Lock these down before production.
- In Swagger UI, click **Authorize** and enter the Basic credentials to have Try-It add the `Authorization` header to API calls.

### Calling from Vue/axios
```js
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:9090",
  auth: { username: "admin", password: "change-me" } // or read from env
});

async function loadFeatures() {
  const { data } = await api.get("/api/features");
  return data;
}
```

## REST Endpoints
| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/features` | List every feature tile shown in the UI |
| `GET` | `/api/features/overview` | Aggregated totals by status/category |
| `GET` | `/api/features/{id}` | Fetch details for one feature |
| `POST` | `/api/features` | Create a new feature (request body = `FeatureRequest`) |
| `PUT` | `/api/features/{id}` | Update an existing feature |
| `PATCH` | `/api/features/{id}/toggle` | Flip the enabled flag |
| `DELETE` | `/api/features/{id}` | Remove a feature |

Swagger UI automatically available at `http://localhost:9090/swagger-ui.html`.

## Data Model
```json
{
  "id": "generated UUID",
  "name": "Policy CRUD",
  "description": "Create, edit, archive travel policies",
  "category": "POLICY_MANAGEMENT",
  "enabled": true,
  "lastUpdated": "2025-11-15T07:00:00Z"
}
```

`FeatureRequest` body mirrors all fields except `id` and `lastUpdated`.

## Demo Ideas
1. Use `/api/features/overview` to populate dashboard KPIs in the Vue app.
2. Toggle modules on/off live from Swagger UI and refresh the frontend to show/ hide menus.
3. Add brand-new experiments from the frontend by calling `POST /api/features`.
