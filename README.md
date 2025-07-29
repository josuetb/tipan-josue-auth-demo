# Servicio de Autenticación con AWS Cognito

Este proyecto es un microservicio de autenticación desarrollado con Spring Boot y Kotlin que utiliza AWS Cognito como proveedor de identidad. La aplicación actúa como un OAuth2 Resource Server que valida tokens JWT emitidos por AWS Cognito.

## 📋 Tabla de Contenidos

- [Arquitectura](#arquitectura)
- [Prerrequisitos](#prerrequisitos)
- [Configuración de AWS Cognito](#configuración-de-aws-cognito)
- [Configuración de la Aplicación](#configuración-de-la-aplicación)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [Endpoints Disponibles](#endpoints-disponibles)
- [Testing de la API](#testing-de-la-api)
- [Solución de Problemas](#solución-de-problemas)

## 🏗️ Arquitectura

La aplicación utiliza las siguientes tecnologías:

- **Spring Boot 3.5.3** - Framework principal
- **Kotlin 1.9.25** - Lenguaje de programación
- **Spring Security** - Seguridad y autenticación
- **OAuth2 Resource Server** - Validación de tokens JWT
- **AWS Cognito** - Proveedor de identidad
- **Java 21** - Runtime

### Flujo de Autenticación

1. El cliente obtiene un token JWT de AWS Cognito
2. El cliente envía el token en el header `Authorization: Bearer <token>`
3. La aplicación valida el token con AWS Cognito
4. Se extraen los grupos del claim `cognito:groups` y se convierten en roles de Spring Security
5. Se autoriza el acceso basado en los roles

## 📋 Prerrequisitos

- **Java 21** o superior
- **Cuenta de AWS** con acceso a Cognito
- **AWS CLI** configurado (opcional, para automatización)
- **Gradle** (incluido wrapper en el proyecto)

## 🔧 Configuración de AWS Cognito

### Paso 1: Crear un User Pool

1. **Acceder a la Consola de AWS**
   - Inicia sesión en la [Consola de AWS](https://console.aws.amazon.com)
   - Busca "Cognito" en la barra de búsqueda
   - Selecciona "Amazon Cognito"

2. **Crear un User Pool**
   - Haz clic en "Create user pool"
   - En "Authentication providers", selecciona **"Cognito user pool"**
   - Haz clic en "Next"

### Paso 2: Configurar Atributos del Usuario

1. **Configurar sign-in options**
   - Selecciona **"Email"** como método de inicio de sesión
   - Opcionalmente, también puedes habilitar **"Phone number"**
   - Haz clic en "Next"

2. **Configurar políticas de contraseña**
   - Selecciona **"Cognito defaults"** o personaliza según tus necesidades:
     - Longitud mínima: 8 caracteres
     - Requerir números: Sí
     - Requerir símbolos especiales: Sí
     - Requerir mayúsculas y minúsculas: Sí
   - En "Multi-factor authentication", selecciona **"Optional"** o **"Required"** según tus necesidades
   - Haz clic en "Next"

### Paso 3: Configurar App Integration

1. **Configurar el dominio del User Pool**
   - En "Domain", selecciona **"Use a Cognito domain"**
   - Ingresa un subdominio único, por ejemplo: `tu-app-auth-2024`
   - Haz clic en "Check availability"
   - Cuando esté disponible, haz clic en "Next"

2. **Crear App Client**
   - En "App client name", ingresa: `auth-service-client`
   - En "Client secret", selecciona **"Generate a client secret"**
   - En "Authentication flows", habilita:
     - ✅ ALLOW_USER_PASSWORD_AUTH
     - ✅ ALLOW_REFRESH_TOKEN_AUTH
     - ✅ ALLOW_USER_SRP_AUTH
   - Haz clic en "Next"

### Paso 4: Revisar y Crear

1. **Revisar configuración**
   - Revisa todos los ajustes
   - Haz clic en **"Create user pool"**

2. **Obtener información del User Pool**
   - Una vez creado, anota los siguientes datos:
     - **User Pool ID**: `us-east-1_XXXXXXXXX`
     - **App Client ID**: `xxxxxxxxxxxxxxxxxxxxxx`
     - **App Client Secret**: `xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`
     - **Domain**: `https://tu-app-auth-2024.auth.us-east-1.amazoncognito.com`

### Paso 5: Crear Grupos para Autorización

1. **Crear grupo de administradores**
   - En el User Pool creado, ve a la pestaña **"Groups"**
   - Haz clic en **"Create group"**
   - Nombre del grupo: `ADMINS`
   - Descripción: `Administradores del sistema`
   - Haz clic en **"Create group"**

2. **Crear grupo de usuarios**
   - Haz clic en **"Create group"** nuevamente
   - Nombre del grupo: `USERS`
   - Descripción: `Usuarios regulares`
   - Haz clic en **"Create group"**

### Paso 6: Crear Usuarios de Prueba

1. **Crear usuario administrador**
   - Ve a la pestaña **"Users"**
   - Haz clic en **"Create user"**
   - Email: `admin@tuempresa.com`
   - Temporal password: `TempPass123!`
   - Desmarca **"Mark phone number as verified"** si no usas teléfono
   - Marca **"Mark email as verified"**
   - Haz clic en **"Create user"**

2. **Asignar usuario al grupo ADMINS**
   - Selecciona el usuario recién creado
   - Haz clic en **"Add to group"**
   - Selecciona **"ADMINS"**
   - Haz clic en **"Add"**

## ⚙️ Configuración de la Aplicación

1. **Configurar application.yml**

   Edita el archivo `src/main/resources/application.yml` y reemplaza el placeholder:

   ```yaml
   spring:
     application:
       name: auth
     security:
       oauth2:
         resourceserver:
           jwt:
             issuer-uri: https://cognito-idp.REGION.amazonaws.com/USER_POOL_ID
   ```

   **Ejemplo:**
   ```yaml
   spring:
     application:
       name: auth
     security:
       oauth2:
         resourceserver:
           jwt:
             issuer-uri: https://cognito-idp.us-east-1.amazonaws.com/us-east-1_XYZ123ABC
   ```

   > **Nota:** Reemplaza `REGION` con tu región de AWS (ej: `us-east-1`) y `USER_POOL_ID` con el ID de tu User Pool.

## 🚀 Instalación y Ejecución

### Opción 1: Usar Gradle Wrapper (Recomendado)

```bash
# Clonar el repositorio (si aplica)
git clone <tu-repositorio>
cd auth

# Dar permisos de ejecución al wrapper (en macOS/Linux)
chmod +x gradlew

# Compilar el proyecto
./gradlew build

# Ejecutar la aplicación
./gradlew bootRun
```

### Opción 2: Usar Gradle instalado globalmente

```bash
# Compilar el proyecto
gradle build

# Ejecutar la aplicación
gradle bootRun
```

### Opción 3: Ejecutar el JAR generado

```bash
# Compilar
./gradlew build

# Ejecutar el JAR
java -jar build/libs/auth-0.0.1-SNAPSHOT.jar
```

La aplicación se ejecutará en: **http://localhost:8080**

## 🔗 Endpoints Disponibles

| Endpoint | Método | Acceso | Descripción |
|----------|--------|--------|-------------|
| `/api/health` | GET | Público | Health check de la aplicación |
| `/api/hello` | GET | Autenticado | Endpoint protegido que requiere token válido |
| `/api/admin/action` | POST | Solo ADMINS | Endpoint que requiere rol de administrador |

### Descripción de Seguridad

- **Público**: No requiere autenticación
- **Autenticado**: Requiere token JWT válido de AWS Cognito
- **Solo ADMINS**: Requiere token válido + pertenecer al grupo `ADMINS` en Cognito

## 🧪 Testing de la API

### 1. Obtener Token de AWS Cognito

Primero necesitas obtener un token JWT de AWS Cognito. Puedes usar la AWS CLI:

```bash
aws cognito-idp admin-initiate-auth \
  --user-pool-id us-east-1_XXXXXXXXX \
  --client-id xxxxxxxxxxxxxxxxxxxxxx \
  --auth-flow ADMIN_NO_SRP_AUTH \
  --auth-parameters USERNAME=admin@tuempresa.com,PASSWORD=TuNuevaPassword123!
```

O usar el SDK de AWS en tu aplicación frontend preferida.

### 2. Probar Endpoints

#### Endpoint Público
```bash
curl -X GET http://localhost:8080/api/health
# Respuesta esperada: "OK"
```

#### Endpoint Autenticado
```bash
curl -X GET http://localhost:8080/api/hello \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6..."
# Respuesta esperada: "Hello, World!"
```

#### Endpoint de Administrador
```bash
curl -X POST http://localhost:8080/api/admin/action \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6..." \
  -H "Content-Type: application/json"
# Respuesta esperada: "Admin action performed successfully!"
```

### 3. Probar con Postman

1. **Configurar Collection**
   - Crear nueva colección "Auth Service"
   - Configurar variable `baseUrl` = `http://localhost:8080`
   - Configurar variable `token` = `<tu-jwt-token>`

2. **Configurar Authorization**
   - En las requests que lo requieren, usar:
   - Type: `Bearer Token`
   - Token: `{{token}}`

## 🔍 Solución de Problemas

### Error: "Unable to find a jwk with kid"

**Problema:** El token no se puede validar porque la clave pública no se encuentra.

**Solución:**
- Verifica que el `issuer-uri` en `application.yml` sea correcto
- Asegúrate de que el User Pool esté en la misma región configurada
- Verifica que el token JWT sea válido y no haya expirado

### Error: "Access Denied"

**Problema:** El usuario no tiene los permisos necesarios.

**Solución:**
- Para `/api/admin/action`: Verifica que el usuario pertenezca al grupo `ADMINS` en Cognito
- Verifica que el claim `cognito:groups` esté presente en el token JWT
- Asegúrate de que el token no haya expirado

### Error: "Invalid JWT"

**Problema:** El token JWT no es válido.

**Solución:**
- Verifica que estés usando el token correcto de AWS Cognito
- Asegúrate de que el formato del header sea: `Authorization: Bearer <token>`
- Verifica que el token no haya expirado
- Confirma que el token fue emitido por el User Pool correcto

### La aplicación no inicia

**Problema:** Error al iniciar Spring Boot.

**Solución:**
- Verifica que Java 21 esté instalado: `java -version`
- Asegúrate de que el puerto 8080 esté disponible
- Revisa los logs de la aplicación para errores específicos
- Verifica que `application.yml` tenga la configuración correcta

## 📝 Notas Adicionales

- **Seguridad**: En producción, asegúrate de usar HTTPS
- **Logging**: Configura logs apropiados para monitoreo
- **Monitoring**: Considera implementar health checks adicionales
- **Escalabilidad**: La aplicación es stateless y puede escalarse horizontalmente

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.
