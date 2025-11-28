import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { User } from './user.schema';
import { Document } from 'mongoose';

export type JobSeekerDocument = JobSeeker & Document;

@Schema()
export class JobSeeker {
  @Prop()
  twitterLink?: string;

  @Prop()
  webLink?: string;

  @Prop()
  githubLink?: string;

  @Prop()
  facebookLink?: string;

  @Prop()
  description?: string;

  @Prop()
  title?: string;

  @Prop()
  dateOfBirth?: Date;

  @Prop({ type: [String] })
  skills?: string[];

  @Prop({ type: [String] })
  experience?: string[];

  @Prop({ type: [String] })
  education?: string[];
}

export const JobSeekerSchema = SchemaFactory.createForClass(JobSeeker);
