import { Controller, Get, UseGuards, Request, Logger } from '@nestjs/common';
import { UserService } from '../../users/user.service';
import { KeycloakAuthGuard } from '../../auth/guards/keycloak-auth.guard';
import { Roles } from '../../auth/decorators/roles.decorator';
import { RolesGuard } from '../../auth/guards/roles.guard';

@Controller('analytics')
@UseGuards(KeycloakAuthGuard, RolesGuard)
export class AnalyticsController {
  private readonly logger = new Logger(AnalyticsController.name);
  
  constructor(private userService: UserService) {}

  @Get('users')
  @Roles('admin')
  async getUserStats(@Request() req) {
    this.logger.log('User roles from token:', req.user?.realm_access?.roles);
    this.logger.log('User info:', { 
      preferred_username: req.user?.preferred_username,
      email: req.user?.email 
    });
    
    const totalUsers = await this.userService.getTotalUsers();
    const newUsersLast7Days = await this.userService.getNewUsersLast7Days();

    return {
      totalUsers,
      newUsersLast7Days,
    };
  }

  @Get('users-alert')
  @Roles('admin')
  async getUsersAlert(@Request() req) {
    this.logger.log('Getting users alert data');
    
    const days = 7; // Default to 7 days
    const alert = await this.userService.getUsersAlert(days);

    return alert;
  }
}
