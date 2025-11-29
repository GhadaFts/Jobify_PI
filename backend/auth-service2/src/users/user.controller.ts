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
}