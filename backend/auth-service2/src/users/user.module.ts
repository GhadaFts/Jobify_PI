import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { UserService } from './user.service';
import { UserController } from './user.controller';
import { User, UserSchema } from './schema/user.schema';
import { UserRole } from './schema/userRole.enum';
import { RecruiterSchema } from './schema/recruiter.schema';
import { JobSeekerSchema } from './schema/jobSeeker.schema';
import { UploadService } from './upload.service';

@Module({
  imports: [
    MongooseModule.forFeatureAsync([
      {
        name: User.name, useFactory: () => {
          const schema = UserSchema;
          schema.discriminator(UserRole.Recruiter, RecruiterSchema);
          schema.discriminator(UserRole.JobSeeker, JobSeekerSchema);
          return schema;
        }
      },
    ]),
  ],
  controllers: [UserController],
  providers: [UserService, UploadService],
  exports: [UserService, MongooseModule], // Important for auth
})
export class UserModule { }