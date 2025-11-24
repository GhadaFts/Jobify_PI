import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Document } from 'mongoose';
import { UserRole } from './userRole.enum';

export type UserDocument = User & Document;

@Schema({ discriminatorKey: 'role', collection: 'users', timestamps: true })
export class User {
    @Prop({ required: true, unique: true })
    id: number;

    @Prop({ required: true, unique: true })
    email: string;

    @Prop({ required: true })
    password: string;

    @Prop({ required: true })
    fullName: string;

    @Prop({ enum: UserRole, default: UserRole.JobSeeker })
    role: UserRole;

    @Prop()
    profilePicture?: string;

    @Prop()
    phoneNumber?: string;

    @Prop()
    nationality?: string;

    @Prop()
    gender?: string;

    @Prop({ default: false })
    deleted?: boolean;
}

export const UserSchema = SchemaFactory.createForClass(User);