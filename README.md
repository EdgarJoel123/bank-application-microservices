# Bank Application — Spring Boot Microservices

Aplicación bancaria desarrollada como prueba técnica de microservicios con **Spring Boot**, separada en dos servicios independientes:

- **CLIENT**: gestión de personas y clientes.
- **ACCOUNT**: gestión de cuentas, transacciones y estados de cuenta.

La solución completa las funcionalidades solicitadas sobre la estructura base entregada, manteniendo la separación por capas, persistencia con JPA, manejo centralizado de errores, validaciones, pruebas automatizadas, documentación OpenAPI/Swagger y comunicación asíncrona entre microservicios.

---

## 1. Arquitectura

```text
                       ┌─────────────────────────────┐
                       │       CLIENT SERVICE        │
                       │         :8001               │
                       │                             │
HTTP / REST ──────────►│ ClientController            │
                       │        ↓                    │
                       │ ClientService               │
                       │        ↓                    │
                       │ ClientRepository            │
                       │        ↓                    │
                       │ H2 / JPA                    │
                       │                             │
                       │ ClientEventPublisher        │
                       └──────────────┬──────────────┘
                                      │
                                      │ @Async + HTTP
                                      ▼
                       ┌─────────────────────────────┐
                       │      ACCOUNT SERVICE        │
                       │         :8000               │
                       │                             │
HTTP / REST ──────────►│ AccountController           │
                       │ TransactionController       │
                       │        ↓                    │
                       │ Services                    │
                       │        ↓                    │
                       │ Repositories                │
                       │        ↓                    │
                       │ H2 / JPA                    │
                       │                             │
                       │ ClientProjectionController  │
                       └─────────────────────────────┘
```

Cada microservicio mantiene su propia responsabilidad y persistencia. `ACCOUNT` no depende directamente de la entidad `Client`; utiliza el identificador del cliente y una proyección local con los datos mínimos necesarios.

---

## 2. Tecnologías utilizadas

- Java 11
- Spring Boot 2.4.2
- Spring Web / REST
- Spring Data JPA
- Hibernate
- H2 Database
- Maven
- Bean Validation
- Lombok
- Spring Async
- RestTemplate para propagación asíncrona
- Springdoc OpenAPI / Swagger UI
- JUnit 5
- Mockito
- MockMvc

---

## 3. Funcionalidades implementadas

### CLIENT

- Entidad `Person`.
- Entidad `Client` heredando de `Person`.
- CRUD completo de clientes.
- Validaciones de datos obligatorios.
- Manejo global de excepciones.
- Propagación asíncrona de creación, actualización y eliminación de clientes hacia `ACCOUNT`.
- Swagger/OpenAPI.
- Pruebas unitarias y de contexto.

### ACCOUNT

- Entidad `Account`.
- Entidad `Transaction`.
- CRUD completo de cuentas.
- CRUD completo de transacciones.
- Número de cuenta único.
- Registro de depósitos y retiros.
- Recalculo automático de saldo.
- Historial de movimientos.
- Validación de saldo disponible.
- Mensaje de negocio requerido: `Saldo no disponible`.
- Reporte de estado de cuenta por cliente y rango de fechas.
- Consulta de movimientos filtrada desde Repository/JPA.
- Manejo transaccional con `@Transactional`.
- Bloqueo pesimista para proteger operaciones concurrentes sobre la misma cuenta.
- Proyección local de clientes recibida desde `CLIENT`.
- Swagger/OpenAPI.
- Pruebas unitarias, de integración y de contexto.

---

## 4. Requisitos funcionales cubiertos

| Requisito | Implementación |
|---|---|
| F1 | CRUD de Cliente, Cuenta y Transacción |
| F2 | Movimientos positivos/negativos y actualización de saldo |
| F3 | Prevención de saldo negativo con `Saldo no disponible` |
| F4 | Estado de cuenta por cliente y rango de fechas |
| F5 | Prueba unitaria de Client en `sampleTest.java` |
| F6 | Prueba de integración con Controller → Service → Repository → DB |
| Adicional | Swagger/OpenAPI |
| Adicional | Manejo global de excepciones |
| Adicional | Validaciones Bean Validation |
| Adicional | Comunicación asíncrona CLIENT → ACCOUNT |
| Adicional | Control de concurrencia con bloqueo pesimista |
| Adicional | Colección Postman |

---

## 5. Estructura del proyecto

```text
BankApplication/
├── account/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/devsu/hackerearth/backend/account/
│       │   │   ├── config/
│       │   │   ├── controller/
│       │   │   ├── exception/
│       │   │   ├── model/
│       │   │   ├── repository/
│       │   │   └── service/
│       │   └── resources/application.properties
│       └── test/
│
├── client/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/devsu/hackerearth/backend/client/
│       │   │   ├── config/
│       │   │   ├── controller/
│       │   │   ├── exception/
│       │   │   ├── integration/
│       │   │   ├── model/
│       │   │   ├── repository/
│       │   │   └── service/
│       │   └── resources/application.properties
│       └── test/
│
├── collection_bank_postman.json
├── Makefile
└── README.md
```

---

## 6. Puertos

Configuración final recomendada:

| Microservicio | Puerto |
|---|---:|
| ACCOUNT | `8000` |
| CLIENT | `8001` |

En `client/src/main/resources/application.properties`:

```properties
server.port=8001
account.service.url=http://localhost:8000
```

En `account/src/main/resources/application.properties`:

```properties
server.port=8000
```

Esta configuración permite levantar los dos microservicios al mismo tiempo y probar la comunicación entre ellos.

---

## 7. Compilación

Desde la raíz del proyecto:

```bash
cd /home/project/Microservices/BankApplication
```

Compilación final:

```bash
mvn -f client/pom.xml clean install
mvn -f account/pom.xml clean install
```

Compilación requerida por la prueba omitiendo ejecución de tests:

```bash
mvn -f account/pom.xml clean install -DskipTests
mvn -f client/pom.xml clean install -DskipTests
```

---

## 8. Ejecución de pruebas

### CLIENT

```bash
mvn -f client/pom.xml clean test
```

### ACCOUNT

```bash
mvn -f account/pom.xml clean test
```

Las pruebas contemplan, entre otros puntos:

- Construcción y propiedades del dominio `Client`.
- Controller de cliente.
- Contexto Spring Boot.
- Controller de cuenta.
- Flujo de integración para registrar una transacción.
- Actualización correcta del saldo.

---

## 9. Ejecución de los dos microservicios

Antes de iniciar, se pueden detener procesos anteriores:

```bash
pkill -f 'AccountApplication'
pkill -f 'ClientApplication'
pkill -f 'spring-boot:run'
```

Verificar que no existan procesos antiguos:

```bash
ps -ef | grep -E 'AccountApplication|ClientApplication|spring-boot:run' | grep -v grep
```

### Iniciar ACCOUNT

```bash
mvn -f account/pom.xml spring-boot:run
```

Salida esperada:

```text
Tomcat started on port(s): 8000
Started AccountApplication
```

### Iniciar CLIENT

En otra terminal:

```bash
mvn -f client/pom.xml spring-boot:run
```

Salida esperada:

```text
Tomcat started on port(s): 8001
Started ClientApplication
```

### Verificar procesos

```bash
ps -ef | grep -E 'AccountApplication|ClientApplication|spring-boot:run' | grep -v grep
```

---

## 10. Swagger / OpenAPI

Se agregó documentación OpenAPI mediante `springdoc-openapi-ui` en los dos microservicios.

### ACCOUNT

Swagger UI local:

```text
http://localhost:8000/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8000/api-docs
```

### CLIENT

Swagger UI local:

```text
http://localhost:8001/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8001/api-docs
```

### HackerEarth / OpenVSCode

El entorno de preview de HackerEarth expone normalmente el backend asociado al puerto `8000`.

Durante el desarrollo se utilizó, por ejemplo:

```text
https://ide-p-aps1.hackerearth.com/live-7f4dfb16203848678ef7a745fb0d0b3d/swagger-ui/index.html
```

> El identificador `live-...` pertenece a la sesión del entorno y puede cambiar.

Para visualizar Swagger de `CLIENT` en el preview externo de HackerEarth se puede detener `ACCOUNT`, cambiar temporalmente `CLIENT` a `server.port=8000`, ejecutar CLIENT y utilizar la misma URL de preview. Después de la revisión debe restaurarse la configuración final:

```text
ACCOUNT = 8000
CLIENT  = 8001
```

Se configuró `Server().url(".")` en OpenAPI para conservar correctamente el prefijo `/live-.../` utilizado por el reverse proxy de HackerEarth al ejecutar operaciones con **Try it out**.

---

## 11. API — CLIENT

El servicio soporta tanto la ruta original en inglés como el alias solicitado en español.

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/clients` | Listar clientes |
| GET | `/api/clients/{id}` | Consultar cliente |
| POST | `/api/clients` | Crear cliente |
| PUT | `/api/clients/{id}` | Actualizar cliente |
| PATCH | `/api/clients/{id}` | Actualización parcial/estado |
| DELETE | `/api/clients/{id}` | Eliminar cliente |
| GET | `/api/clientes` | Alias para listar clientes |
| GET | `/api/clientes/{id}` | Alias para consultar cliente |
| POST | `/api/clientes` | Alias para crear cliente |
| PUT | `/api/clientes/{id}` | Alias para actualizar cliente |
| PATCH | `/api/clientes/{id}` | Alias para actualización parcial |
| DELETE | `/api/clientes/{id}` | Alias para eliminar cliente |

Ejemplo de creación:

```json
{
  "dni": "1800000001",
  "name": "Cliente Prueba",
  "password": "123456",
  "gender": "M",
  "age": 30,
  "address": "Ambato",
  "phone": "0999999999",
  "isActive": true
}
```

---

## 12. API — ACCOUNT

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/accounts` | Listar cuentas |
| GET | `/api/accounts/{id}` | Consultar cuenta |
| POST | `/api/accounts` | Crear cuenta |
| PUT | `/api/accounts/{id}` | Actualizar cuenta |
| PATCH | `/api/accounts/{id}` | Actualización parcial/estado |
| DELETE | `/api/accounts/{id}` | Eliminar cuenta |
| GET | `/api/cuentas` | Alias solicitado en español |
| GET | `/api/cuentas/{id}` | Alias de consulta |
| POST | `/api/cuentas` | Alias de creación |
| PUT | `/api/cuentas/{id}` | Alias de actualización |
| PATCH | `/api/cuentas/{id}` | Alias de actualización parcial |
| DELETE | `/api/cuentas/{id}` | Alias de eliminación |

Ejemplo:

```json
{
  "number": "001-001",
  "type": "SAVINGS",
  "initialAmount": 1000.00,
  "isActive": true,
  "clientId": 1
}
```

---

## 13. API — TRANSACTIONS

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/transactions` | Listar transacciones |
| GET | `/api/transactions/{id}` | Consultar transacción |
| POST | `/api/transactions` | Registrar movimiento |
| PUT | `/api/transactions/{id}` | Actualizar transacción |
| DELETE | `/api/transactions/{id}` | Eliminar transacción |
| GET | `/api/transacciones` | Alias solicitado en español |
| GET | `/api/transacciones/{id}` | Alias de consulta |
| POST | `/api/transacciones` | Alias de creación |
| PUT | `/api/transacciones/{id}` | Alias de actualización |
| DELETE | `/api/transacciones/{id}` | Alias de eliminación |

### Depósito

```json
{
  "type": "DEPOSIT",
  "amount": 500.00,
  "accountId": 1
}
```

### Retiro

```json
{
  "type": "WITHDRAWAL",
  "amount": -300.00,
  "accountId": 1
}
```

La regla de saldo aplicada es:

```text
nuevoSaldo = saldoActual + montoMovimiento
```

Si el resultado es negativo, la operación es rechazada y se devuelve:

```text
Saldo no disponible
```

La transacción inválida no se conserva y el saldo de la cuenta no se modifica.

---

## 14. Estado de cuenta / reporte

Endpoint principal:

```text
GET /api/transactions/clients/{clientId}/report
```

Parámetros:

```text
dateTransactionStart=yyyy-MM-dd
dateTransactionEnd=yyyy-MM-dd
```

Ejemplo:

```text
GET /api/transactions/clients/1/report?dateTransactionStart=2026-10-01&dateTransactionEnd=2026-10-31
```

El reporte incluye información del cliente, cuenta, saldo inicial, estado y movimientos dentro del rango solicitado.

La consulta por fechas se realiza desde Repository/JPA para evitar cargar movimientos innecesarios y filtrarlos posteriormente en memoria.

---

## 15. Comunicación asíncrona entre microservicios

Cuando un cliente es creado, actualizado o eliminado, `CLIENT` propaga el cambio de manera asíncrona hacia `ACCOUNT`.

Flujo:

```text
ClientController
      ↓
ClientServiceImpl
      ↓
ClientEventPublisher
      ↓
@Async("clientEventExecutor")
      ↓
HTTP PUT / DELETE
      ↓
ClientProjectionController (ACCOUNT)
      ↓
ClientProjectionService
      ↓
ClientProjectionRepository
```

Endpoints internos de `ACCOUNT`:

```text
PUT    /internal/clients/{clientId}
DELETE /internal/clients/{clientId}
```

Estos endpoints se consideran internos y se mantienen fuera de la documentación Swagger pública mediante `@Hidden`.

El objetivo de la proyección es conservar en `ACCOUNT` únicamente la información de cliente necesaria para sus operaciones y reportes, manteniendo desacoplados los dominios de ambos microservicios.

---

## 16. Transaccionalidad y concurrencia

La lógica de movimientos bancarios se ejecuta dentro de transacciones de base de datos mediante `@Transactional`.

El flujo protege de forma atómica:

1. Búsqueda de cuenta.
2. Obtención/bloqueo del estado actual.
3. Registro del movimiento.
4. Recalculo de saldo.
5. Validación de saldo disponible.
6. Persistencia del nuevo estado.

Para reducir el riesgo de inconsistencias por movimientos simultáneos sobre la misma cuenta se implementó acceso con **bloqueo pesimista (`PESSIMISTIC_WRITE`)**.

---

## 17. Validaciones

Se implementaron validaciones mínimas sin imponer restricciones arbitrarias que pudieran alterar el contrato esperado por las pruebas automáticas.

Ejemplos:

- Cliente: DNI, nombre y contraseña obligatorios.
- Cuenta: número, tipo y cliente obligatorios.
- Cuenta: monto inicial no negativo.
- Cuenta: número único.
- Transacción: tipo y cuenta obligatorios.
- Reporte: fecha inicial no puede ser posterior a fecha final.

`amount` no utiliza `@Positive`, ya que los retiros se representan con movimientos negativos.

---

## 18. Manejo de errores

Los errores se centralizan mediante `@RestControllerAdvice`.

Casos contemplados:

| Caso | HTTP |
|---|---:|
| Datos inválidos | 400 |
| Saldo no disponible | 400 |
| Recurso no encontrado | 404 |
| Conflicto de integridad / clave única | 409 |
| Error no controlado | 500 |

Se evita exponer directamente stack traces o excepciones Java al consumidor de la API.

---

## 19. Colección Postman

Archivo:

```text
/home/project/Microservices/BankApplication/collection_bank_postman.json
```

La colección contiene solicitudes para:

- Crear, consultar, actualizar y eliminar clientes.
- Crear, consultar, actualizar y eliminar cuentas.
- Registrar depósito.
- Registrar retiro válido.
- Probar retiro sin saldo suficiente.
- Listar transacciones.
- Consultar estado de cuenta por cliente y fechas.

Variables incluidas:

```text
clientBaseUrl  = http://localhost:8001
accountBaseUrl = http://localhost:8000
clientId
accountId
```

Los IDs creados pueden almacenarse automáticamente como variables de colección para encadenar las pruebas.

Validación rápida del archivo:

```bash
python3 -m json.tool collection_bank_postman.json > /dev/null && echo "POSTMAN JSON OK"
```

---

## 20. Comandos rápidos — HackerEarth

### Ruta

```bash
cd /home/project/Microservices/BankApplication
```

### Detener procesos

```bash
pkill -f 'AccountApplication'
pkill -f 'ClientApplication'
pkill -f 'spring-boot:run'
```

### Verificar procesos

```bash
ps -ef | grep -E 'AccountApplication|ClientApplication|spring-boot:run' | grep -v grep
```

### Tests CLIENT

```bash
mvn -f client/pom.xml clean test
```

### Tests ACCOUNT

```bash
mvn -f account/pom.xml clean test
```

### Ejecutar CLIENT

```bash
mvn -f client/pom.xml spring-boot:run
```

Salida final esperada:

```text
Tomcat started on port(s): 8001
Started ClientApplication
```

### Ejecutar ACCOUNT

```bash
mvn -f account/pom.xml spring-boot:run
```

Salida final esperada:

```text
Tomcat started on port(s): 8000
Started AccountApplication
```

### Build final

```bash
mvn -f client/pom.xml clean install
mvn -f account/pom.xml clean install
```

---

## 21. Modo de revisión Swagger en HackerEarth

Debido a que el preview web del IDE normalmente expone un único puerto (`8000`), durante una revisión visual de Swagger se puede ejecutar un microservicio a la vez.

### Revisar ACCOUNT

```text
ACCOUNT server.port=8000
```

```bash
pkill -f 'AccountApplication'
pkill -f 'ClientApplication'
pkill -f 'spring-boot:run'

mvn -f account/pom.xml clean
mvn -f account/pom.xml spring-boot:run
```

Abrir el preview `/swagger-ui/index.html`.

### Revisar CLIENT mediante preview

Solo para esta revisión temporal:

```text
CLIENT server.port=8000
```

Detener ACCOUNT y ejecutar:

```bash
pkill -f 'AccountApplication'
pkill -f 'ClientApplication'
pkill -f 'spring-boot:run'

mvn -f client/pom.xml clean
mvn -f client/pom.xml spring-boot:run
```

Al terminar la revisión, restaurar:

```text
ACCOUNT = 8000
CLIENT  = 8001
```

---

## 22. Decisiones de diseño

- Se mantuvo la arquitectura y estructura base del proyecto en lugar de reconstruirlo.
- La lógica de negocio permanece en Services y no en Controllers.
- Los Controllers se encargan del contrato HTTP y delegan la lógica.
- Se conservaron firmas y tipos existentes para minimizar incompatibilidades con pruebas automáticas.
- Se añadieron alias de endpoints en español sin eliminar las rutas existentes en inglés.
- Se utiliza `double` en los DTO/modelos donde ya formaba parte del contrato original, evitando cambios innecesarios de firma.
- El reporte filtra por rango desde persistencia.
- La proyección de cliente evita acoplar directamente la entidad `Client` dentro del microservicio `ACCOUNT`.
- La comunicación entre servicios se ejecuta en un executor asíncrono dedicado.
- Swagger se configuró para funcionar también detrás del reverse proxy del IDE de HackerEarth.

---

## 23. Checklist antes de entregar

```text
[ ] mvn -f account/pom.xml clean install -DskipTests
[ ] mvn -f client/pom.xml clean install -DskipTests
[ ] mvn -f account/pom.xml clean test
[ ] mvn -f client/pom.xml clean test
[ ] Revisar sección Problems del IDE
[ ] Verificar ACCOUNT en puerto 8000
[ ] Verificar CLIENT en puerto 8001
[ ] Verificar collection_bank_postman.json
[ ] Verificar Swagger/OpenAPI
[ ] Verificar que no existan secretos/tokens en el repositorio
[ ] git status limpio
[ ] Push de la versión final a GitHub
[ ] Submit Code en HackerEarth
```

---

## 24. Git — actualización final

```bash
git status
git add .
git commit -m "Complete banking microservices requirements"
git push -u origin main
```

Verificar:

```bash
git status
git log -1 --oneline
```

El repositorio debe quedar sin cambios pendientes:

```text
nothing to commit, working tree clean
```

---

## 25. Consideraciones de seguridad

- No almacenar tokens de GitHub, contraseñas reales ni secretos dentro del repositorio.
- Los tokens utilizados para autenticación Git deben mantenerse fuera del código fuente.
- La contraseña incluida en `ClientDto` forma parte del dominio de la prueba; en un entorno productivo debería almacenarse de forma segura mediante hashing y nunca devolverse en texto plano.
- La implementación de mensajería asíncrona HTTP utilizada satisface el objetivo del ejercicio sin introducir infraestructura adicional; en producción podría evolucionarse hacia broker/eventos persistentes y patrón Outbox para mayor resiliencia.

---

## 26. Resultado

La solución final entrega una aplicación bancaria dividida en dos microservicios independientes, con persistencia JPA, CRUD REST, movimientos bancarios, validación de saldo, reporte por fechas, pruebas automatizadas, manejo de concurrencia, comunicación asíncrona, colección Postman y documentación Swagger/OpenAPI.

El diseño prioriza **separación de responsabilidades, compatibilidad con la estructura base, claridad del código y cumplimiento funcional de F1–F6**.
