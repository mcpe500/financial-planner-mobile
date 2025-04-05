import { Request } from "express";
import { User } from "./user.types";

// Augment Express namespace to use our custom User type
declare global {
  namespace Express {
    interface AuthRequest {
      user?: User;
    }
  }
}

// Our custom AuthRequest interface
// export interface AuthRequest extends Request {
//   user?: User;
// }