# Keycloak Setup Instructions

## Creating the Backend Client in Keycloak

If the `backend` client doesn't exist in Keycloak, follow these steps:

### 1. Access Keycloak Admin Console
- URL: http://localhost:8080
- Login: admin / admin

### 2. Select the Realm
- Click the realm dropdown (top-left)
- Select `jobify-realm` (or create it if it doesn't exist)

### 3. Create the Backend Client
1. Click **Clients** in the left menu
2. Click **Create client** button
3. Fill in:
   - **Client ID**: `backend`
   - **Name**: Backend Service
   - **Description**: Backend authentication client
   - Click **Next**

4. **Capability config**:
   - ✅ Client authentication: ON
   - ✅ Authorization: OFF
   - ✅ Standard flow: ON
   - ✅ Direct access grants: ON
   - ✅ Service accounts roles: ON
   - Click **Next**

5. **Login settings**:
   - Valid redirect URIs: `*`
   - Valid post logout redirect URIs: `*`
   - Web origins: `*`
   - Click **Save**

### 4. Get the Client Secret
1. Go to the **Credentials** tab
2. Copy the **Client secret**
3. Update the `.env` file in `backend/auth-service2/`:
   ```
   KEYCLOAK_CLIENT_SECRET=<paste-the-secret-here>
   ```

### 5. Restart auth-service2
After updating the `.env` file, restart the service:
```bash
cd backend/auth-service2
npm run start:dev
```

## Testing
Try logging in again with valid credentials.
