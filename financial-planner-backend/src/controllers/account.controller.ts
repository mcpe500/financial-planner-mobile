import { Request, Response, RequestHandler } from "express";
import crypto from "crypto";
import databaseService from "../services/database.service";
import { sendDeletionOtpEmail, sendAccountDeletionEmailForWeb } from "../services/email.service";
import { AuthRequest } from "../types/request.types";
import jwt from "jsonwebtoken";
import { config } from "../config/config";
import * as otpService from '../services/otp.service';

// Store deletion requests temporarily (in production, use Redis or a database table)
interface DeletionRequest {
  email: string;
  token: string;
  otp: string;
  createdAt: Date;
  expiresAt: Date;
}

// In-memory storage for deletion requests (replace with database in production)
const deletionRequests = new Map<string, DeletionRequest>();

// Render the account deletion request page
export const showDeletionRequestPage = (req: Request, res: Response) => {
  res.render("account/delete-request", {
    title: "Delete Account",
    message: req.query.message || "",
  });
};

// Handle the account deletion request form
export const requestAccountDeletion: RequestHandler = async (req, res) => {
  const { userId } = (req as any).user;

  if (!userId) {
    res.status(401).json({ success: false, message: 'Authentication required.' });
    return;
  }

  try {
    const user = await databaseService.findUserById(userId);
    if (!user) {
      res.status(404).json({ success: false, message: 'User not found.' });
      return;
    }

    const { otp, expiresAt } = otpService.generateOtp();
    await otpService.storeOtp(userId, otp, expiresAt);

    await sendDeletionOtpEmail(user.email, otp);

    res.status(200).json({
      success: true,
      message: 'An OTP has been sent to your email to confirm deletion.',
    });
  } catch (error) {
    console.error('Error requesting account deletion:', error);
    res.status(500).json({ success: false, message: 'Internal server error.' });
  }
};

// Show OTP verification page
export const showOtpVerificationPage = (req: Request, res: Response) => {
  const { token } = req.params;
  
  // Check if token exists and is valid
  const deletionRequest = deletionRequests.get(token);
  if (!deletionRequest) {
    res.render("account/error", {
      title: "Invalid Request",
      error: "This deletion link is invalid or has expired",
    });
    return;
  }
  
  // Check if token is expired
  if (deletionRequest.expiresAt < new Date()) {
    deletionRequests.delete(token);
    res.render("account/error", {
      title: "Expired Request",
      error: "This deletion link has expired. Please request a new one.",
    });
    return;
  }
  
  // Show OTP verification page
  res.render("account/enter-otp", {
    title: "Verify OTP",
    token,
    email: deletionRequest.email,
  });
};

// Verify OTP and show final confirmation
export const verifyWebOtp = (req: Request, res: Response) => {
  const { token } = req.params;
  const { otp } = req.body;
  
  // Check if token exists and is valid
  const deletionRequest = deletionRequests.get(token);
  if (!deletionRequest) {
    res.render("account/error", {
      title: "Invalid Request",
      error: "This deletion link is invalid or has expired",
    });
    return;
  }
  
  // Check if OTP matches
  if (deletionRequest.otp !== otp) {
    res.render("account/enter-otp", {
      title: "Verify OTP",
      token,
      email: deletionRequest.email,
      error: "Invalid OTP. Please try again.",
    });
    return;
  }
  
  // Show final confirmation page
  res.render("account/final-confirmation", {
    title: "Confirm Deletion",
    token,
    email: deletionRequest.email,
  });
};

// Process final deletion confirmation
export const confirmDeletion = async (req: Request, res: Response) => {
  const { token } = req.params;
  const { email } = req.body;
  
  // Check if token exists and is valid
  const deletionRequest = deletionRequests.get(token);
  if (!deletionRequest) {
    res.render("account/error", {
      title: "Invalid Request",
      error: "This deletion link is invalid or has expired",
    });
    return;
  }
  
  // Verify email confirmation matches
  if (deletionRequest.email !== email) {
    res.render("account/final-confirmation", {
      title: "Confirm Deletion",
      token,
      email: deletionRequest.email,
      error: "Email confirmation does not match",
    });
    return;
  }
  
  try {
    // Delete user from database
    await databaseService.deleteUser(email);
    
    // Remove deletion request
    deletionRequests.delete(token);
    
    // Show completion page
    res.render("account/deletion-complete", {
      title: "Account Deleted",
    });
  } catch (error) {
    console.error("Error deleting account:", error);
    res.render("account/final-confirmation", {
      title: "Confirm Deletion",
      token,
      email: deletionRequest.email,
      error: "An error occurred while deleting your account",
    });
    return;
  }
};

// API endpoints for mobile app deletion flow

// Request account deletion from mobile app
export const apiRequestDeletion = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    // User must be authenticated
    if (!req.user) {
      res.status(401).json({ success: false, message: "Authentication required" });
      return;
    }

    const email = req.user.email;
    
    // Generate unique token for the deletion request
    const token = crypto.randomBytes(32).toString("hex");
    
    // Generate 6-digit OTP
    const otp = Math.floor(100000 + Math.random() * 900000).toString();
    
    // Store deletion request with 1-hour expiration
    const expiresAt = new Date();
    expiresAt.setHours(expiresAt.getHours() + 1);
    
    deletionRequests.set(token, {
      email,
      token,
      otp,
      createdAt: new Date(),
      expiresAt,
    });

    // Set expiration cleanup
    setTimeout(() => {
      if (deletionRequests.has(token)) {
        deletionRequests.delete(token);
      }
    }, 3600000); // 1 hour

    // Send email with OTP
    try {
      await sendAccountDeletionEmailForWeb(email, token, otp);
    } catch (emailError) {
      console.error('Failed to send email:', emailError);
      res.status(500).json({ 
        success: false, 
        message: "Failed to send verification email" 
      });
      return;
    }

    res.status(200).json({ 
      success: true, 
      message: "Deletion verification email sent",
      token // Send token to the app
    });
  } catch (error) {
    console.error("API Error requesting account deletion:", error);
    res.status(500).json({ 
      success: false, 
      message: "Failed to process deletion request" 
    });
  }
};

// Verify OTP from mobile app
export const apiVerifyOtp = async (req: Request, res: Response): Promise<void> => {
  try {
    const { token, otp } = req.body;
    
    console.log('API Verify OTP request:', { token: token?.substring(0, 8) + '...', otp });
    
    if (!token || !otp) {
      console.log('Missing token or OTP');
      res.status(400).json({ 
        success: false, 
        message: "Token and OTP are required" 
      });
      return;
    }
    
    // Check if token exists and is valid
    const deletionRequest = deletionRequests.get(token);
    if (!deletionRequest) {
      console.log('Token not found in deletion requests');
      res.status(400).json({ 
        success: false, 
        message: "Invalid or expired token" 
      });
      return;
    }
    
    // Check if token is expired
    if (deletionRequest.expiresAt < new Date()) {
      console.log('Token has expired');
      deletionRequests.delete(token);
      res.status(400).json({ 
        success: false, 
        message: "Token has expired" 
      });
      return;
    }
    
    // Check if OTP matches
    if (deletionRequest.otp !== otp.toString()) {
      console.log('OTP mismatch:', { expected: deletionRequest.otp, received: otp });
      res.status(400).json({ 
        success: false, 
        message: "Invalid OTP" 
      });
      return;
    }
    
    // Generate a temporary verification token for the final step
    const verificationToken = jwt.sign(
      { email: deletionRequest.email, type: "account_deletion" },
      config.jwt.secret,
      { expiresIn: "15m" }
    );
    
    console.log('OTP verified successfully for email:', deletionRequest.email);
    
    res.status(200).json({ 
      success: true, 
      message: "OTP verified successfully",
      verificationToken
    });
  } catch (error) {
    console.error("API Error verifying OTP:", error);
    res.status(500).json({ 
      success: false, 
      message: "Failed to verify OTP" 
    });
  }
};

// Confirm account deletion from mobile app
export const apiConfirmDeletion = async (req: Request, res: Response): Promise<void> => {
  try {
    const { verificationToken, email } = req.body;
    
    if (!verificationToken || !email) {
      res.status(400).json({ 
        success: false, 
        message: "Verification token and email are required" 
      });
      return;
    }
    
    // Verify the token
    let decoded;
    try {
      decoded = jwt.verify(verificationToken, config.jwt.secret) as { email: string, type: string };
    } catch (err) {
      res.status(401).json({ 
        success: false, 
        message: "Invalid or expired verification token" 
      });
      return;
    }
    
    // Check if token is for account deletion and email matches
    if (decoded.type !== "account_deletion" || decoded.email !== email) {
      res.status(401).json({ 
        success: false, 
        message: "Invalid verification token" 
      });
      return;
    }
    
    // Delete user from database
    await databaseService.deleteUser(email);
    
    // Clean up any deletion requests for this email
    for (const [key, value] of deletionRequests.entries()) {
      if (value.email === email) {
        deletionRequests.delete(key);
      }
    }
    
    res.status(200).json({ 
      success: true, 
      message: "Your account has been successfully deleted" 
    });
  } catch (error) {
    console.error("API Error confirming deletion:", error);
    res.status(500).json({ 
      success: false, 
      message: "Failed to delete account" 
    });
  }
};

/**
 * Confirms and finalizes account deletion using an OTP from the request body.
 * This requires authentication.
 */
export const confirmAccountDeletion: RequestHandler = async (req, res) => {
  const { userId } = (req as any).user;
  const { otp } = req.body;

  if (!userId) {
    res.status(401).json({ success: false, message: 'Authentication required.' });
    return;
  }

  if (!otp) {
    res.status(400).json({ success: false, message: 'OTP is required.' });
    return;
  }

  try {
    const isOtpValid = await otpService.verifyOtp(userId, otp);

    if (!isOtpValid) {
      res.status(400).json({ success: false, message: 'Invalid or expired OTP.' });
      return;
    }

    await databaseService.deleteUserById(userId);

    res.status(200).json({
      success: true,
      message: 'Your account has been successfully deleted.',
    });
  } catch (error) {
    console.error('Error confirming account deletion:', error);
    res.status(500).json({ success: false, message: 'Internal server error.' });
  }
};