import { UserRole } from "../../users/schema/userRole.enum";

export class RegisterDto {
    email: string;
    password: string;
    fullName: string;
    role?: UserRole;
}