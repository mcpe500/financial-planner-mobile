import { Router } from "express";
import { 
  showDeletionRequestPage,
  requestAccountDeletion,
  showOtpVerificationPage,
  verifyOtp,
  confirmDeletion 
} from "../controllers/account.controller";

const router = Router();

// Account deletion flow
router.get("/delete", showDeletionRequestPage);
router.post("/delete", requestAccountDeletion);
router.get("/delete/verify/:token", showOtpVerificationPage);
router.post("/delete/verify/:token", verifyOtp);
router.post("/delete/confirm/:token", confirmDeletion);

export default router;