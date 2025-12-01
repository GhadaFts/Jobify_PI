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
  UseInterceptors,
  UploadedFile,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { UserService } from './user.service';
import { UploadService } from './upload.service';
import { KeycloakAuthGuard } from '../auth/guards/keycloak-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';

@Controller('user')
export class UserController {
  constructor(
    private userService: UserService,
    private uploadService: UploadService,
  ) {}

  @Get('profile')
  @UseGuards(KeycloakAuthGuard)
  async getProfile(@Request() req) {
    const keycloakId = req.user.sub;
    const user = await this.userService.findByKeycloakId(keycloakId);

    if (!user) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }

    return user;
  }

  @Put('profile')
  @UseGuards(KeycloakAuthGuard)
  async updateProfile(@Request() req, @Body() updateData: any) {
    const keycloakId = req.user.sub;

    // Remove photo_profil from updateData if it's present
    // Photos should be uploaded via the upload-photo endpoint
    const { photo_profil, ...cleanData } = updateData;

    const updatedUser = await this.userService.updateProfile(
      keycloakId,
      cleanData,
    );

    if (!updatedUser) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }

    return updatedUser;
  }

  @Post('upload-photo')
  @UseGuards(KeycloakAuthGuard)
  @UseInterceptors(
    FileInterceptor('file', {
      limits: {
        fileSize: 5 * 1024 * 1024, // 5MB limit
      },
      fileFilter: (req, file, cb) => {
        // Check file type
        if (!file.mimetype.match(/\/(jpg|jpeg|png|gif)$/)) {
          return cb(
            new HttpException(
              'Only image files are allowed!',
              HttpStatus.BAD_REQUEST,
            ),
            false,
          );
        }
        cb(null, true);
      },
    }),
  )
  async uploadPhoto(@Request() req, @UploadedFile() file: Express.Multer.File) {
    if (!file) {
      throw new HttpException('No file uploaded', HttpStatus.BAD_REQUEST);
    }

    const keycloakId = req.user.sub;

    try {
      // Get current user to delete old photo
      const user = await this.userService.findByKeycloakId(keycloakId);

      // Delete old photo if exists
      if (user?.['photo_profil']) {
        await this.uploadService.deleteImage(user['photo_profil']);
      }

      // Save new photo
      const imageUrl = await this.uploadService.saveImage(file);

      // Update user profile with new image URL
      const updatedUser = await this.userService.updateProfile(keycloakId, {
        photo_profil: imageUrl,
      });

      return {
        url: imageUrl,
        message: 'Photo uploaded successfully',
      };
    } catch (error) {
      throw new HttpException(
        'Failed to upload photo',
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
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

    if (!user) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }

    return user;
  }

  @Post('initial-profile')
  async initialProfile(@Body() initiateData: any) {
    const { keycloakId, ...data } = initiateData;
    const updatedUser = await this.userService.updateProfile(keycloakId, data);

    if (!updatedUser) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }

    return updatedUser;
  }

  @Get(':keycloakId/public')
  @UseGuards(KeycloakAuthGuard)
  async getPublicProfile(@Param('keycloakId') keycloakId: string) {
    const user = await this.userService.findByKeycloakId(keycloakId);

    if (!user) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND);
    }

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
      github_link: user['github_link'],
    };

    return publicProfile;
  }
}
