import { Injectable, OnModuleInit, OnModuleDestroy, Logger } from '@nestjs/common';
import { Eureka } from 'eureka-js-client';
import { eurekaConfig } from '../config/eureka.config';

@Injectable()
export class EurekaService implements OnModuleInit, OnModuleDestroy {
  private readonly logger = new Logger(EurekaService.name);
  private client: Eureka;

  constructor() {
    this.client = new Eureka(eurekaConfig);
  }

  onModuleInit() {
    this.client.start((error) => {
      if (error) {
        this.logger.error('Eureka registration failed:', error);
      } else {
        this.logger.log('Successfully registered with Eureka server');
      }
    });

    this.client.on('registryUpdated', () => {
      this.logger.debug('Eureka registry updated');
    });
  }

  onModuleDestroy() {
    this.logger.log('Deregistering from Eureka server');
    this.client.stop();
  }

  getClient(): Eureka {
    return this.client;
  }

  getInstancesByAppId(appId: string) {
    return this.client.getInstancesByAppId(appId);
  }
}