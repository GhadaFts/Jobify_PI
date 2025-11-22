import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { User } from './user.schema';
import { Document } from 'mongoose';

export type RecruiterDocument = Recruiter & Document;

@Schema()
export class Recruiter {
    @Prop()
    twitterLink?: string;

    @Prop()
    webLink?: string;

    @Prop()
    githubLink?: string;

    @Prop()
    facebookLink?: string;

    @Prop({ type: String })
    description?: string;

    @Prop()
    companyAddress?: string;

    @Prop()
    domaine?: string;

    @Prop()
    employeesNumber?: number;

    @Prop({ type: [String] })
    service?: string[];
}

export const RecruiterSchema = SchemaFactory.createForClass(Recruiter);