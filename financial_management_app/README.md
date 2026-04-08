# Financial Management App

API REST para gestión financiera desarrollada con Spring Boot siguiendo arquitectura hexagonal.

## Tech Stack

- Java 21
- Spring Boot 4.0.5
- Spring Data JPA
- PostgreSQL
- Lombok
- SpringDoc OpenAPI (Swagger)

## Arquitectura Hexagonal

El proyecto sigue la arquitectura hexagonal (ports & adapters) inspirada en el scaffold de Bancolombia, organizada en tres capas:

```
com.udea.financial/
│
├── application/                        → Capa de aplicación (arranque y configuración)
│   ├── config/                         → @Configuration, beans de Spring
│   └── FinancialManagementAppApplication.java
│
├── domain/                             → Capa de dominio (lógica de negocio pura)
│   ├── model/                          → Entidades y objetos de valor (POJOs sin dependencias de framework)
│   ├── usecase/                        → Casos de uso
│   ├── gateway/                        → Puertos de salida (interfaces)
│   └── exception/                      → Excepciones de dominio
│
└── infrastructure/                     → Capa de infraestructura (adaptadores)
    ├── driven/                         → Adaptadores de salida
    │   ├── persistence/
    │   │   ├── entity/                 → Entidades JPA (@Entity)
    │   │   ├── repository/             → Repositorios Spring Data
    │   │   ├── mapper/                 → Mappers Entity ↔ Model
    │   │   └── adapter/               → Implementación de los gateways
    │   └── security/                   → Implementaciones de seguridad
    │
    └── entrypoint/                     → Adaptadores de entrada
        └── rest/
            ├── controller/             → @RestController
            ├── dto/                    → Request/Response DTOs
            ├── mapper/                 → Mappers DTO ↔ Model
            ├── handler/               → Manejo global de excepciones
            └── constants/              → Constantes (Swagger, rutas, etc.)
```

### Regla de dependencias

```
infrastructure → domain ← application
```

- **domain** no depende de ninguna otra capa ni de frameworks externos.
- **application** depende de domain (configura y conecta los beans).
- **infrastructure** depende de domain (implementa los puertos/gateways).

## API Endpoints

### Users (`/api/users`)

| Método | Ruta                | Descripción              | Request Body     | Response         |
|--------|---------------------|--------------------------|------------------|------------------|
| POST   | `/api/users`        | Crear usuario            | `UserRequestDTO` | `201 Created`    |
| GET    | `/api/users`        | Listar todos los usuarios| -                | `UserResponseDTO[]` |
| GET    | `/api/users/{id}`   | Obtener usuario por ID   | -                | `UserResponseDTO`|
| GET    | `/api/users/email/{email}` | Obtener usuario por email | -         | `UserResponseDTO`|
| DELETE | `/api/users/{id}`   | Eliminar usuario por ID  | -                | `200 OK`         |

## Documentación Swagger

Con la aplicación corriendo:

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Configuración

### Variables de entorno requeridas

| Variable      | Descripción                     | Ejemplo                                      |
|---------------|---------------------------------|----------------------------------------------|
| `DB_URL`      | URL de conexión a PostgreSQL    | `jdbc:postgresql://localhost:5432/financial`  |
| `DB_USER`     | Usuario de la base de datos     | `postgres`                                   |
| `DB_PASSWORD` | Contraseña de la base de datos  | `password`                                   |

### Ejecutar la aplicación

```bash
# Configurar variables de entorno
export DB_URL=jdbc:postgresql://localhost:5432/financial
export DB_USER=postgres
export DB_PASSWORD=password

# Ejecutar
./mvnw spring-boot:run
```
