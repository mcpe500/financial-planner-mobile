import { Router } from "express";
import { 
  apiRequestDeletion,
  apiVerifyOtp,
  apiConfirmDeletion 
} from "../controllers/account.controller";
import { authenticate } from "../middleware/auth.middleware";

const router = Router();

// Mobile app API endpoints for account deletion
router.post("/request-deletion", authenticate, apiRequestDeletion);
router.post("/verify-otp", apiVerifyOtp);
router.post("/confirm-deletion", apiConfirmDeletion);

export default router;