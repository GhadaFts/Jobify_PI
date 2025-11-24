import { UserRole } from "src/users/schema/userRole.enum";

export interface JwtPayload {
    id: string,
    username: string,
    role: UserRole
}