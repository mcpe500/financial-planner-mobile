import { Request } from "express";
import { UserType } from "./user.types";

declare global {
  namespace Express {
    interface User extends UserType {}
  }
}

export interface AuthRequest extends Request {
  user?: UserType;
}