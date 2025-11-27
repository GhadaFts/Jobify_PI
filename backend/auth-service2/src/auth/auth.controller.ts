import { Body, Controller, Post, Get, Param, UseGuards, Request } from '@nestjs/common';
import { AuthService } from './auth.service';
import { LoginDto } from './dto/login.dto';
import { RegisterDto } from './dto/register.dto';
import { KeycloakAuthGuard } from './guards/keycloak-auth.guard';
import { Roles } from './decorators/roles.decorator';
import { RolesGuard } from './guards/roles.guard';

@Controller('auth')
export class AuthController {
  constructor(private authService: AuthService) {}

  @Post('register')
  async register(@Body() registerDto: RegisterDto) {
    return this.authService.register(registerDto);
  }

  @Post('login')
  async login(@Body() loginDto: LoginDto) {
    return this.authService.login(loginDto);
  }

  @Post('refresh')
  async refreshToken(@Body('refreshToken') refreshToken: string) {
    return this.authService.refreshToken(refreshToken);
  }

  @Post('logout')
  @UseGuards(KeycloakAuthGuard)
  async logout(@Body('refreshToken') refreshToken: string) {
    return this.authService.logout(refreshToken);
  }

  @Get('profile')
  @UseGuards(KeycloakAuthGuard)
  async getProfile(@Request() req) {
    const keycloakId = req.user.sub;
    return this.authService.getUserProfile(keycloakId);
  }

  @Get('user/exists/:email')
  async checkUserExists(@Param('email') email: string) {
    const exists = await this.authService.userExists(email);
    return { exists };
  }
  // Get user by Keycloak ID (for Feign Client)
  @Get('users/:keycloakId')
  async getUserByKeycloakId(@Param('keycloakId') keycloakId: string) {
    return this.authService.getUserProfile(keycloakId);
  }

  // Check if user exists by Keycloak ID (for Feign Client)
  @Get('users/:keycloakId/exists')
  async checkUserExistsById(@Param('keycloakId') keycloakId: string) {
    try {
      const user = await this.authService.getUserProfile(keycloakId);
      return { exists: !!user };
    } catch (error) {
      return { exists: false };
    }
  }
}
