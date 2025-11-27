import {
  Injectable,
  CanActivate,
  ExecutionContext,
  UnauthorizedException,
} from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as jwt from 'jsonwebtoken';
import jwksClient from 'jwks-rsa';

@Injectable()
export class KeycloakAuthGuard implements CanActivate {
  private jwksClient: jwksClient.JwksClient;
  private keycloakUrl: string;
  private realm: string;

  constructor(private configService: ConfigService) {
    this.keycloakUrl = this.configService.get(
      'KEYCLOAK_URL',
      'http://localhost:8080',
    );
    this.realm = this.configService.get('KEYCLOAK_REALM', 'jobify-realm');

    this.jwksClient = jwksClient({
      jwksUri: `${this.keycloakUrl}/realms/${this.realm}/protocol/openid-connect/certs`,
      cache: true,
      cacheMaxAge: 86400000, // 24 hours
    });
  }

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const request = context.switchToHttp().getRequest();
    const authHeader = request.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw new UnauthorizedException('No token provided');
    }

    const token = authHeader.substring(7);

    try {
      const decoded = await this.verifyToken(token);
      request.user = decoded;
      return true;
    } catch (error) {
      throw new UnauthorizedException('Invalid token');
    }
  }

  private async verifyToken(token: string): Promise<any> {
    return new Promise((resolve, reject) => {
      const decodedToken: any = jwt.decode(token, { complete: true });

      if (!decodedToken) {
        return reject(new Error('Invalid token'));
      }

      this.jwksClient.getSigningKey(decodedToken.header.kid, (err, key) => {
        if (err || !key) {
          return reject(err || new Error('Key not found'));
        }

        const signingKey = key.getPublicKey();

        jwt.verify(
          token,
          signingKey,
          { algorithms: ['RS256'] },
          (err, decoded) => {
            if (err) {
              return reject(err);
            }
            resolve(decoded);
          },
        );
      });
    });
  }
}
