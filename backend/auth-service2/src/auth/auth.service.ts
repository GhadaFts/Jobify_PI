import {
  Injectable,
  UnauthorizedException,
  ConflictException,
  HttpException,
  Logger,
} from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { HttpService } from '@nestjs/axios';
import { ConfigService } from '@nestjs/config';
import { firstValueFrom } from 'rxjs';
import { User, UserDocument } from '../users/schema/user.schema';
import { RegisterDto } from './dto/register.dto';
import { LoginDto } from './dto/login.dto';
import { UserRole } from '../users/schema/userRole.enum';
@Injectable()
export class AuthService {
  private readonly logger = new Logger(AuthService.name);
  private readonly keycloakUrl: string;
  private readonly realm: string;
  private readonly clientId: string;
  private readonly clientSecret: string;
  private readonly adminUsername: string;
  private readonly adminPassword: string;

  constructor(
    @InjectModel(User.name) private userModel: Model<UserDocument>,
    private httpService: HttpService,
    private configService: ConfigService,
  ) {
    this.keycloakUrl = this.configService.get(
      'KEYCLOAK_URL',
      'http://localhost:8080',
    );
    this.realm = this.configService.get(
      'KEYCLOAK_REALM',
      'jobify-realm',
    );
    this.clientId = this.configService.get(
      'KEYCLOAK_CLIENT_ID',
      'backend',
    );
    this.clientSecret = this.configService.get('KEYCLOAK_CLIENT_SECRET', '');

    this.adminUsername = this.configService.get(
      'KEYCLOAK_ADMIN_USERNAME',
      'admin',
    );
    this.adminPassword = this.configService.get(
      'KEYCLOAK_ADMIN_PASSWORD',
      'admin',
    );

    // Log configuration (without sensitive data)
    this.logger.log(`Keycloak URL: ${this.keycloakUrl}`);
    this.logger.log(`Realm: ${this.realm}`);
    this.logger.log(`Client ID: ${this.clientId}`);
    this.logger.log(`Client Secret configured: ${this.clientSecret ? 'Yes' : 'No'}`);
  }

  // Get admin access token - Try client_credentials first, fallback to password grant
  private async getAdminToken(): Promise<string> {
    // Method 1: Try client credentials (service account)
    try {
      this.logger.log('Method 1: Trying client_credentials grant...');
      const tokenUrl = `${this.keycloakUrl}/realms/${this.realm}/protocol/openid-connect/token`;
      this.logger.log(`Token URL: ${tokenUrl}`);

      const response = await firstValueFrom(
        this.httpService.post(
          tokenUrl,
          new URLSearchParams({
            grant_type: 'client_credentials',
            client_id: this.clientId,
            client_secret: this.clientSecret,
          }),
          {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          },
        ),
      );
      this.logger.log('âœ… Admin token obtained via client_credentials');
      return response.data.access_token;
    } catch (error) {
      this.logger.warn('âŒ client_credentials failed, trying password grant...');
      if (error.response) {
        this.logger.warn('Error details:', JSON.stringify(error.response.data));
      }
    }

    // Method 2: Try password grant with admin user
    try {
      this.logger.log('Method 2: Trying password grant with admin user...');
      const response = await firstValueFrom(
        this.httpService.post(
          `${this.keycloakUrl}/realms/${this.realm}/protocol/openid-connect/token`,
          new URLSearchParams({
            grant_type: 'password',
            client_id: this.clientId,
            client_secret: this.clientSecret,
            username: this.adminUsername,
            password: this.adminPassword,
          }),
          {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          },
        ),
      );
      this.logger.log('âœ… Admin token obtained via password grant');
      return response.data.access_token;
    } catch (error) {
      this.logger.error('âŒ password grant also failed');
      if (error.response) {
        this.logger.error('Error response:', JSON.stringify(error.response.data));
        this.logger.error('Status:', error.response.status);
      }
    }

    // Method 3: Try master realm admin-cli (traditional approach)
    try {
      this.logger.log('Method 3: Trying master realm admin-cli...');
      const response = await firstValueFrom(
        this.httpService.post(
          `${this.keycloakUrl}/realms/master/protocol/openid-connect/token`,
          new URLSearchParams({
            grant_type: 'password',
            client_id: 'admin-cli',
            username: this.adminUsername,
            password: this.adminPassword,
          }),
          {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          },
        ),
      );
      this.logger.log('âœ… Admin token obtained via master realm');
      return response.data.access_token;
    } catch (error) {
      this.logger.error('âŒ All authentication methods failed');
      if (error.response) {
        this.logger.error('Final error:', JSON.stringify(error.response.data));
      }
      throw new HttpException(
        'Failed to authenticate with Keycloak. Please check: 1) Keycloak is running, 2) Client secret is correct, 3) Service account is enabled, 4) Admin credentials are correct',
        500,
      );
    }
  }

  // Register user in both Keycloak and local database
  async register(registerDto: RegisterDto) {
    const { fullName, email, password, role } = registerDto;

    this.logger.log(`Registration attempt for email: ${email}`);

    // Check if user already exists
    const exists = await this.userExists(email);
    if (exists) {
      throw new ConflictException('User with this email already exists');
    }

    try {
      // 1. Get admin token
      this.logger.log('Getting admin token...');
      const adminToken = await this.getAdminToken();

      this.logger.log(`Creating user in Keycloak: ${email}`);

      // 2. Create user in Keycloak with proper configuration
      const createUserResponse = await firstValueFrom(
        this.httpService.post(
          `${this.keycloakUrl}/admin/realms/${this.realm}/users`,
          {
            username: email,
            email: email,
            firstName: fullName.split(' ')[0] || fullName,
            lastName: fullName.split(' ').slice(1).join(' ') || '',
            enabled: true,
            emailVerified: true, // Mark email as verified
            credentials: [
              {
                type: 'password',
                value: password,
                temporary: false, // Not temporary, so no reset required
              },
            ],
            requiredActions: [], // No required actions
          },
          {
            headers: {
              Authorization: `Bearer ${adminToken}`,
              'Content-Type': 'application/json',
            },
          },
        ),
      );

      this.logger.log('User created in Keycloak successfully');

      // 3. Extract user ID from Location header
      const locationHeader = createUserResponse.headers['location'];
      if (!locationHeader) {
        throw new Error('Location header not found in response');
      }

      const keycloakId = locationHeader.split('/').pop();
      this.logger.log(`Keycloak user ID: ${keycloakId}`);

      // 4. Verify user was created and is enabled
      const userCheck = await firstValueFrom(
        this.httpService.get(
          `${this.keycloakUrl}/admin/realms/${this.realm}/users/${keycloakId}`,
          {
            headers: { Authorization: `Bearer ${adminToken}` },
          },
        ),
      );

      if (!userCheck.data.enabled) {
        this.logger.error('User was created but is not enabled');
        throw new HttpException('User creation failed - user not enabled', 500);
      }

      this.logger.log('✓ User verified as enabled in Keycloak');

      // 5. Clear any required actions that might have been set by default
      await this.clearRequiredActions(keycloakId, adminToken);

      // 6. Assign role to user in Keycloak
      const userRole = role || UserRole.JobSeeker;
      this.logger.log(`Assigning role: ${userRole}`);
      await this.assignRoleToUser(keycloakId, userRole, adminToken);

      // 7. Create user in local database
      this.logger.log('Creating user in local database...');
      await this.createLocalUser(keycloakId, fullName, email, userRole, null);

      this.logger.log('✓ User registered successfully');
      return {
        message: 'User registered successfully',
        keycloakId,
        email,
        role: userRole,
      };
    } catch (error) {
      this.logger.error('Registration failed:', error.message);

      if (error.response) {
        this.logger.error('Error response status:', error.response.status);
        this.logger.error('Error response data:', JSON.stringify(error.response.data));
      }

      if (error.response?.status === 409) {
        throw new ConflictException('User already exists in Keycloak');
      }

      throw new HttpException(
        error.response?.data?.errorMessage || error.message || 'Failed to register user',
        error.response?.status || 500,
      );
    }
  }
  // Add this method to your AuthService
  private async clearRequiredActions(keycloakId: string, adminToken: string) {
    try {
      this.logger.log(`Clearing required actions for user: ${keycloakId}`);

      await firstValueFrom(
        this.httpService.put(
          `${this.keycloakUrl}/admin/realms/${this.realm}/users/${keycloakId}`,
          {
            requiredActions: [],
          },
          {
            headers: {
              Authorization: `Bearer ${adminToken}`,
              'Content-Type': 'application/json',
            },
          },
        ),
      );

      this.logger.log('✓ Required actions cleared successfully');
    } catch (error) {
      this.logger.error('Failed to clear required actions:', error.message);
      // Don't throw here, as user creation might still be successful
    }
  }

  // Assign role to user in Keycloak
  private async assignRoleToUser(
    keycloakId: string,
    role: UserRole,
    adminToken: string,
  ) {
    try {
      // Map your enum to Keycloak role names
      const keycloakRoleName = this.mapRoleToKeycloak(role);
      this.logger.log(`Mapping role ${role} to Keycloak role: ${keycloakRoleName}`);

      // Get role from Keycloak
      const roleResponse = await firstValueFrom(
        this.httpService.get(
          `${this.keycloakUrl}/admin/realms/${this.realm}/roles/${keycloakRoleName}`,
          {
            headers: { Authorization: `Bearer ${adminToken}` },
          },
        ),
      );

      const roleData = roleResponse.data;
      this.logger.log('Role data retrieved from Keycloak');

      // Assign role to user
      await firstValueFrom(
        this.httpService.post(
          `${this.keycloakUrl}/admin/realms/${this.realm}/users/${keycloakId}/role-mappings/realm`,
          [roleData],
          {
            headers: {
              Authorization: `Bearer ${adminToken}`,
              'Content-Type': 'application/json',
            },
          },
        ),
      );

      this.logger.log('Role assigned successfully');
    } catch (error) {
      this.logger.error('Failed to assign role:', error.message);
      if (error.response) {
        this.logger.error('Role assignment error data:', error.response.data);
      }
      throw new HttpException('Failed to assign role', 500);
    }
  }

  // Map your role enum to Keycloak role names
  private mapRoleToKeycloak(role: UserRole): string {
    const roleMap = {
      [UserRole.Admin]: 'ADMIN',
      [UserRole.JobSeeker]: 'JOB_SEEKER',
      [UserRole.Recruiter]: 'RECRUITER',
    };
    return roleMap[role];
  }

  // Create local user with discriminator pattern (only essential fields)
  private async createLocalUser(
    keycloakId: string,
    fullName: string,
    email: string,
    role: UserRole,
    roleSpecificFields: any,
  ) {
    try {
      // Only save essential fields during registration
      const userData: any = {
        id: Date.now(),
        keycloakId,
        fullName,
        email,
        password: 'MANAGED_BY_KEYCLOAK',
        role,
        deleted: false,
      };

      this.logger.log('Creating user in MongoDB...');
      const user = await this.userModel.create(userData);
      this.logger.log('User created in MongoDB successfully');
      return user;
    } catch (error) {
      this.logger.error('Failed to create user in MongoDB:', error.message);
      throw error;
    }
  }

  // Login user through Keycloak
  async login(loginDto: LoginDto) {
    const { email, password } = loginDto;

    try {
      this.logger.log(`Login attempt for: ${email}`);
      const tokenUrl = `${this.keycloakUrl}/realms/${this.realm}/protocol/openid-connect/token`;
      this.logger.log(`Token URL: ${tokenUrl}`);

      const response = await firstValueFrom(
        this.httpService.post(
          tokenUrl,
          new URLSearchParams({
            grant_type: 'password',
            client_id: this.clientId,
            client_secret: this.clientSecret,
            username: email,
            password: password,
            scope: 'openid profile email',
          }),
          {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          },
        ),
      );

      this.logger.log('âœ… Login successful');
      return {
        accessToken: response.data.access_token,
        refreshToken: response.data.refresh_token,
        expiresIn: response.data.expires_in,
        tokenType: response.data.token_type,
      };
    } catch (error) {
      this.logger.error('âŒ Login failed:', error.message);
      if (error.response) {
        this.logger.error('Error status:', error.response.status);
        this.logger.error('Error data:', JSON.stringify(error.response.data));
      }
      throw new UnauthorizedException('Invalid credentials or user not enabled');
    }
  }

  // Refresh access token
  async refreshToken(refreshToken: string) {
    try {
      const response = await firstValueFrom(
        this.httpService.post(
          `${this.keycloakUrl}/realms/${this.realm}/protocol/openid-connect/token`,
          new URLSearchParams({
            grant_type: 'refresh_token',
            client_id: this.clientId,
            client_secret: this.clientSecret,
            refresh_token: refreshToken,
          }),
          {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          },
        ),
      );

      return {
        accessToken: response.data.access_token,
        refreshToken: response.data.refresh_token,
        expiresIn: response.data.expires_in,
      };
    } catch (error) {
      throw new UnauthorizedException('Invalid refresh token');
    }
  }

  // Logout user
  async logout(refreshToken: string) {
    try {
      await firstValueFrom(
        this.httpService.post(
          `${this.keycloakUrl}/realms/${this.realm}/protocol/openid-connect/logout`,
          new URLSearchParams({
            client_id: this.clientId,
            client_secret: this.clientSecret,
            refresh_token: refreshToken,
          }),
          {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          },
        ),
      );

      return { message: 'Logged out successfully' };
    } catch (error) {
      throw new HttpException('Logout failed', 500);
    }
  }

  // Check if user exists in Keycloak
  async userExists(email: string): Promise<boolean> {
    try {
      const adminToken = await this.getAdminToken();

      const response = await firstValueFrom(
        this.httpService.get(
          `${this.keycloakUrl}/admin/realms/${this.realm}/users?email=${email}&exact=true`,
          {
            headers: { Authorization: `Bearer ${adminToken}` },
          },
        ),
      );

      return response.data.length > 0;
    } catch (error) {
      this.logger.error('Error checking if user exists:', error.message);
      return false;
    }
  }

  // Get user profile from local database
  async getUserProfile(keycloakId: string) {
    const user = await this.userModel.findOne({ keycloakId }).exec();

    if (!user) {
      throw new HttpException('User not found', 404);
    }

    return user.toObject();
  }

  // Get user by Keycloak ID from Keycloak
  async getKeycloakUser(keycloakId: string) {
    try {
      const adminToken = await this.getAdminToken();

      const response = await firstValueFrom(
        this.httpService.get(
          `${this.keycloakUrl}/admin/realms/${this.realm}/users/${keycloakId}`,
          {
            headers: { Authorization: `Bearer ${adminToken}` },
          },
        ),
      );

      return response.data;
    } catch (error) {
      throw new HttpException('User not found in Keycloak', 404);
    }
  }
}