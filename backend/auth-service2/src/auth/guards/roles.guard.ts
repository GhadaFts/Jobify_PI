import { Injectable, CanActivate, ExecutionContext, Logger } from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { ROLES_KEY } from '../decorators/roles.decorator';

@Injectable()
export class RolesGuard implements CanActivate {
  private readonly logger = new Logger(RolesGuard.name);
  
  constructor(private reflector: Reflector) {}

  canActivate(context: ExecutionContext): boolean {
    const requiredRoles = this.reflector.getAllAndOverride<string[]>(ROLES_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    if (!requiredRoles) {
      return true;
    }

    const request = context.switchToHttp().getRequest();
    const user = request.user;

    const userRoles = user.realm_access?.roles || [];
    
    // Log for debugging
    this.logger.debug(`Required roles: ${requiredRoles.join(', ')}`);
    this.logger.debug(`User roles: ${userRoles.join(', ')}`);

    // Case-insensitive role matching
    const hasRole = requiredRoles.some((role) => 
      userRoles.some((userRole: string) => userRole.toLowerCase() === role.toLowerCase())
    );
    
    this.logger.debug(`Access granted: ${hasRole}`);
    return hasRole;
  }
}