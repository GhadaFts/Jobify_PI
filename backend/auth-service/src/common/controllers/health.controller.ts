import { Controller, Get } from '@nestjs/common';

@Controller()
export class HealthController {
  @Get('health')
  healthCheck() {
    return {
      status: 'UP',
      timestamp: new Date().toISOString(),
    };
  }

  @Get('info')
  info() {
    return {
      app: 'auth-service',
      description: 'Authentication Service for microservices architecture',
      version: '1.0.0',
    };
  }
}