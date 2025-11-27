import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { User, UserDocument } from './schema/user.schema';

@Injectable()
export class UserService {
  constructor(@InjectModel(User.name) private userModel: Model<UserDocument>) {}

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
    const result = await this.userModel
      .findOneAndUpdate({ keycloakId }, { $set: updateData }, { new: true })
      .exec();
    return result;
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
