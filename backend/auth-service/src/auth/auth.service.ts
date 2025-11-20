import { Injectable, UnauthorizedException } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { JwtService } from '@nestjs/jwt';
import { User, UserDocument } from '../users/schema/user.schema';
import * as bcrypt from 'bcrypt';
import { RegisterDto } from './dto/register.dto';
import { LoginDto } from './dto/login.dto';
import { UserRole } from '../users/schema/userRole.enum';

@Injectable()
export class AuthService {
    constructor(@InjectModel(User.name) private userModel: Model<UserDocument>, private jwtService: JwtService) {}

    async register(registerDto: RegisterDto){
        const { fullName, email, password, role } = registerDto;
        const hashedPassword = await bcrypt.hash(password, 10);
        this.userModel.create({
            id: Date.now(),
            fullName,
            email,
            password: hashedPassword,
            role: role ?? UserRole.JobSeeker,
        });
        return {message: 'User registered successfully'};
    }

    async validateUser(loginDto: LoginDto): Promise<User | null> {
        const { email, password } = loginDto;
        const user = await this.userModel.findOne({ email });
        if (user && await bcrypt.compare(password, user.password)) {
            return user;
        }
        return null;
    }

    async login(loginDto: LoginDto) {
        const user = await this.validateUser(loginDto);
        if (!user) {
            throw new UnauthorizedException('Invalid credentials');
        }
        const payload = { sub: user.id, username: user.email };
        const token = this.jwtService.sign(payload);
        return { accessToken: token };
    }
}
