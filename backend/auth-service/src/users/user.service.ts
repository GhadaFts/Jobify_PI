import { Injectable } from "@nestjs/common";
import { User, UserDocument } from "./schema/user.schema";
import { Model, Types } from "mongoose";
import { InjectModel } from "@nestjs/mongoose";

@Injectable()
export class UserService {
    constructor(@InjectModel(User.name) private userModel: Model<UserDocument>) { }

    getProfile(id: string) {
        return this.userModel.findById(id).exec()
    }

    async exists(id: string): Promise<boolean> {
        if (!Types.ObjectId.isValid(id)) {
            return false; // invalid ObjectId
        }
        const user = await this.userModel.exists({ _id: id });
        return !!user;
    }

    async getById(id: string): Promise<User | null> {
        if (!Types.ObjectId.isValid(id)) {
            return null; // invalid ObjectId
        }
        const user = await this.userModel.findById(id).exec();
        return user;
    }
}