import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Document } from 'mongoose';

export type JobSeekerDocument = JobSeeker & Document;

@Schema({ _id: false })
class Experience {
  @Prop()
  position: string;

  @Prop()
  company: string;

  @Prop()
  startDate: string;

  @Prop()
  endDate: string;

  @Prop()
  description: string;
}

@Schema({ _id: false })
class Education {
  @Prop()
  degree: string;

  @Prop()
  field: string;

  @Prop()
  school: string;

  @Prop()
  graduationDate: string;
}

@Schema()
export class JobSeeker {
  @Prop()
  twitter_link?: string;

  @Prop()
  web_link?: string;

  @Prop()
  github_link?: string;

  @Prop()
  facebook_link?: string;

  @Prop()
  description?: string;

  @Prop()
  title?: string;

  @Prop()
  date_of_birth?: string;  // ✅ Changed from dateOfBirth, changed to string to match frontend

  @Prop()
  gender?: string;  // Note: gender is already in User schema, might be duplicate

  @Prop({ type: [String] })
  skills?: string[];

  @Prop({ type: [Experience] })
  experience?: Experience[];

  @Prop({ type: [Education] })
  education?: Education[];
}

export const JobSeekerSchema = SchemaFactory.createForClass(JobSeeker);