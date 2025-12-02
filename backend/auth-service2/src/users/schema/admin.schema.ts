import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Document } from 'mongoose';

export type AdminDocument = Admin & Document;

@Schema()
export class Admin {
    @Prop()
    department?: string;

    @Prop()
    permissions?: string[];

    @Prop({ default: true })
    canManageUsers?: boolean;

    @Prop({ default: true })
    canManageJobs?: boolean;

    @Prop({ default: true })
    canViewAnalytics?: boolean;
}

export const AdminSchema = SchemaFactory.createForClass(Admin);
