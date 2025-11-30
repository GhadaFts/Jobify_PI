import { Injectable } from '@nestjs/common';
import { InjectConnection, InjectModel } from '@nestjs/mongoose';
import { Connection } from 'mongoose';
import { Model } from 'mongoose';
import { User, UserDocument } from './schema/user.schema';
import { UserRole } from './schema/userRole.enum';
import { RecruiterDocument } from './schema/recruiter.schema';
import { JobSeeker, JobSeekerDocument } from './schema/jobSeeker.schema';

@Injectable()
export class UserService {
  constructor(
    @InjectModel(User.name) private userModel: Model<UserDocument>,
    @InjectConnection() private readonly connection: Connection
  ) { }

  async findByKeycloakId(keycloakId: string): Promise<UserDocument | null> {
    const result = await this.userModel.findOne({ keycloakId }).exec();
    return result;
  }

  async findByEmail(email: string): Promise<UserDocument | null> {
    const result = await this.userModel.findOne({ email }).exec();
    return result;
  }

  async updateProfile(
      keycloakId: string,
      updateData: any,
    ): Promise<UserDocument | null> {

      // 1. Find user by keycloak ID
      const user = await this.userModel.findOne({ keycloakId }).exec();
      if (!user) return null;

      const role = user.role;

    // 2. Compile correct discriminator model manually
    let ModelToUse;

    if (this.userModel.discriminators?.[role]) {
      ModelToUse = this.connection.model(
        role,
        this.userModel.discriminators[role].schema,
        'users'
      );
    } else {
      ModelToUse = this.userModel;
    }

      // 3. Clean updateData - remove fields that shouldn't be updated
    const { _id, __v, createdAt, updatedAt, deleted, keycloakId: kId, ...cleanData } = updateData;

    // 4. Update using the correct discriminator model
      const updated = await ModelToUse.findOneAndUpdate(
        { keycloakId },
        { $set: cleanData },
        { new: true, runValidators: true }
      ).exec();

      return updated;
    }

  async getRecruiters(): Promise<RecruiterDocument[]> {
    return await this.userModel.find({
      deleted: false,
      role: UserRole.Recruiter,
    }).exec();
  }

  async getJobSeekers(): Promise<JobSeekerDocument[]> {
    return await this.userModel.find({
      deleted: false,
      role: UserRole.JobSeeker,
    }).exec();
  }

  async findAll(): Promise<UserDocument[]> {
    return this.userModel.find({ deleted: false }).exec();
  }

  async softDelete(keycloakId: string): Promise<UserDocument | null> {
    const result = await this.userModel
      .findOneAndUpdate(
        { keycloakId },
        { $set: { deleted: true } },
        { new: true },
      )
      .exec();
    return result;
  }
}
