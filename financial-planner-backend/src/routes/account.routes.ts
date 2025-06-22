import { Router } from "express";
import { 
  showDeletionRequestPage,
  requestAccountDeletion,
  showOtpVerificationPage,
  verifyOtp,
  confirmDeletion,
  apiRequestDeletion,
  apiVerifyOtp,
  apiConfirmDeletion
} from "../controllers/account.controller";
import { authenticateToken } from "../middleware/auth.middleware";

const router = Router();

// Web account deletion flow
router.get("/delete", showDeletionRequestPage);
router.post("/delete", requestAccountDeletion);
router.get("/delete/verify/:token", showOtpVerificationPage);
router.post("/delete/verify/:token", verifyOtp);
router.post("/delete/confirm/:token", confirmDeletion);

// API account deletion flow for mobile app
router.post("/api/delete/request", authenticateToken, apiRequestDeletion);
router.post("/api/delete/verify", apiVerifyOtp);
router.post("/api/delete/confirm", apiConfirmDeletion);

export default router;