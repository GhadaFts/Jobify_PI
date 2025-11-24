import { Controller, Get, UseGuards, Req, Post, Body, Param } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { JwtPayload } from './jwt.payload';
import { UserService } from './user.service';

@Controller('user')
export class UserController {
    constructor(private readonly userService: UserService) {}

    @Get('profile')
    @UseGuards(AuthGuard('jwt'))
    getProfile(@Req() req) {
        let payload: JwtPayload = req.user;
        return this.userService.getProfile(payload.id)
    }

    @Post('exists')
    @UseGuards(AuthGuard('jwt'))
    exists(@Body() req: Record<string, string>){
        return this.userService.exists(req.id);
    }

    @Get(":id")
    @UseGuards(AuthGuard('jwt'))
    getById(@Param('id') id: string){
        return this.userService.getById(id)
    }
}
