import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Document } from 'mongoose';

export type RecruiterDocument = Recruiter & Document;

@Schema()
export class Recruiter {
    @Prop()
    twitter_link?: string;

    @Prop()
    web_link?: string;

    @Prop()
    github_link?: string;

    @Prop()
    facebook_link?: string;

    @Prop({ type: String })
    description?: string;

    @Prop()
    companyAddress?: string;

    @Prop()
    specialty?: string;  // Fixed typo: sepeciality -> specialty

    @Prop()
    domaine?: string;

    @Prop()
    employees_number?: number;  // Changed from employeesNumber

    @Prop({ type: [String] })
    service?: string[];
}

export const RecruiterSchema = SchemaFactory.createForClass(Recruiter);