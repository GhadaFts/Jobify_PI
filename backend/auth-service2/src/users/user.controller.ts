import {
  Controller,
  Get,
  Post,
  Put,
  Body,
  UseGuards,
  Request,
  Param,
  HttpException,
  HttpStatus,
} from '@nestjs/common';
import { UserService } from './user.service';
import { KeycloakAuthGuard } from '../auth/guards/keycloak-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';

@Controller('user')
export class UserController {
  constructor(private userService: UserService) {}
  @Get('profile')
  @UseGuards(KeycloakAuthGuard)
  async getProfile(@Request() req) {
    const keycloakId = req.user.sub;
    const user = await this.userService.findByKeycloakId(keycloakId);

    // Fixed: Added null check
    if (!user) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }

    // Return user with all discriminator fields
    return user;
  }

  @Put('profile')
  @UseGuards(KeycloakAuthGuard)
  async updateProfile(@Request() req, @Body() updateData: any) {
    const keycloakId = req.user.sub;
    const updatedUser = await this.userService.updateProfile(
      keycloakId,
      updateData,
    );

    // Fixed: Added null check
    if (!updatedUser) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }

    return updatedUser;
  }

  @Get('all')
  @UseGuards(KeycloakAuthGuard, RolesGuard)
  @Roles('ADMIN')
  async getAllUsers() {
    return this.userService.findAll();
  }

  @Get(':keycloakId')
  @UseGuards(KeycloakAuthGuard, RolesGuard)
  @Roles('ADMIN', 'RECRUITER')
  async getUserById(@Param('keycloakId') keycloakId: string) {
    const user = await this.userService.findByKeycloakId(keycloakId);

    // Fixed: Added null check
    if (!user) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }

    return user;
  }

  /**
   * 
   * @param initiateData 
   * intitateData = {
   * keycloakId: string,
   * everything else.....
   * }
   * @returns 
   */
  @Post('initial-profile')
  async initialProfile(@Body() initiateData: any){
    const { keycloakId, ...data } = initiateData;
    const updatedUser = await this.userService.updateProfile(
      keycloakId,
      data,
    );

    // Fixed: Added null check
    if (!updatedUser) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }

    return updatedUser;
  }
  @Get(':keycloakId/public')
  @UseGuards(KeycloakAuthGuard) // Any authenticated user can view
  async getPublicProfile(@Param('keycloakId') keycloakId: string) {
    const user = await this.userService.findByKeycloakId(keycloakId);

    if (!user) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }

    // Return only public fields (remove sensitive data)
    const publicProfile = {
      fullName: user.fullName,
      email: user.email,
      role: user.role,
      photo_profil: user['photo_profil'],
      description: user['description'],
      phone_number: user['phone_number'],
      companyAddress: user['companyAddress'],
      domaine: user['domaine'],
      employees_number: user['employees_number'],
      service: user['service'],
      web_link: user['web_link'],
      facebook_link: user['facebook_link'],
      twitter_link: user['twitter_link'],
      github_link: user['github_link']
    };

    return publicProfile;
  }
}