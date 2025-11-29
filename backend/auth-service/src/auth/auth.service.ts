import { Injectable, UnauthorizedException, InternalServerErrorException, Logger } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model, Types } from 'mongoose';
import { JwtService } from '@nestjs/jwt';
import { User, UserDocument } from '../users/schema/user.schema';
import * as bcrypt from 'bcrypt';
import { RegisterDto } from './dto/register.dto';
import { LoginDto } from './dto/login.dto';
import { UserRole } from '../users/schema/userRole.enum';

@Injectable()
export class AuthService {
    private readonly logger = new Logger(AuthService.name);

    constructor(@InjectModel(User.name) private userModel: Model<UserDocument>, private jwtService: JwtService) { }

    async register(registerDto: RegisterDto) {
        const { fullName, email, password, role } = registerDto;
        const hashedPassword = await bcrypt.hash(password, 10);
        this.userModel.create({
            fullName,
            email,
            password: hashedPassword,
            role: role ?? UserRole.JobSeeker,
        });
        return { message: 'User registered successfully' };
    }

    async validateUser(loginDto: LoginDto): Promise<UserDocument | null> {
        const { email, password } = loginDto;
        const user = await this.userModel.findOne({ email });
        if (user && await bcrypt.compare(password, user.password)) {
            return user;
        }
        return null;
    }

    async login(loginDto: LoginDto) {
        try {
            const user = await this.validateUser(loginDto);
            if (!user) {
                throw new UnauthorizedException('Invalid credentials');
            }
            const userId = user._id instanceof Types.ObjectId ? user._id.toString() : user._id;
            const payload = {
                sub: userId,
                username: user.email,
                role: user.role
            };
            const token = this.jwtService.sign(payload);
            return { accessToken: token };
        } catch (err) {
            // Log full stack for debugging and rethrow a controlled 500
            this.logger.error('Error during login', err as any);
            throw new InternalServerErrorException('Login failed: ' + ((err && (err as any).message) || 'unknown'));
        }
    }
}
