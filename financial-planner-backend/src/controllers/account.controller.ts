import { Request, Response } from "express";
import crypto from "crypto";
import database from "../services/database.service";
import { sendAccountDeletionEmail } from "../services/email.service";
import { AuthRequest } from "../types/request.types";
import jwt from "jsonwebtoken";
import { config } from "../config/config";

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
export const requestAccountDeletion = async (req: Request, res: Response) => {
  try {
    const { email } = req.body;

    if (!email) {
      return res.render("account/delete-request", {
        title: "Delete Account",
        error: "Email is required",
      });
    }

    // Check if user exists
    const user = await database.findUserByEmail(email);
    if (!user) {
      // For security, don't reveal if email exists or not
      return res.render("account/delete-request", {
        title: "Delete Account",
        message: "If your email is registered, you will receive deletion instructions",
      });
    }

    // Generate unique token for the deletion link
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

    // Send email with deletion link and OTP
    try {
      await sendAccountDeletionEmail(email, token, otp);
    } catch (emailError) {
      console.error('Failed to send email:', emailError);
      // Continue without failing the request for security
    }

    // Show success message
    return res.render("account/delete-request", {
      title: "Delete Account",
      message: "If your email is registered, you will receive deletion instructions",
    });
  } catch (error) {
    console.error("Error requesting account deletion:", error);
    return res.render("account/delete-request", {
      title: "Delete Account",
      error: "An error occurred while processing your request",
    });
  }
};

// Show OTP verification page
export const showOtpVerificationPage = (req: Request, res: Response) => {
  const { token } = req.params;
  
  // Check if token exists and is valid
  const deletionRequest = deletionRequests.get(token);
  if (!deletionRequest) {
    return res.render("account/error", {
      title: "Invalid Request",
      error: "This deletion link is invalid or has expired",
    });
  }
  
  // Check if token is expired
  if (deletionRequest.expiresAt < new Date()) {
    deletionRequests.delete(token);
    return res.render("account/error", {
      title: "Expired Request",
      error: "This deletion link has expired. Please request a new one.",
    });
  }
  
  // Show OTP verification page
  res.render("account/enter-otp", {
    title: "Verify OTP",
    token,
    email: deletionRequest.email,
  });
};

// Verify OTP and show final confirmation
export const verifyOtp = (req: Request, res: Response) => {
  const { token } = req.params;
  const { otp } = req.body;
  
  // Check if token exists and is valid
  const deletionRequest = deletionRequests.get(token);
  if (!deletionRequest) {
    return res.render("account/error", {
      title: "Invalid Request",
      error: "This deletion link is invalid or has expired",
    });
  }
  
  // Check if OTP matches
  if (deletionRequest.otp !== otp) {
    return res.render("account/enter-otp", {
      title: "Verify OTP",
      token,
      email: deletionRequest.email,
      error: "Invalid OTP. Please try again.",
    });
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
    return res.render("account/error", {
      title: "Invalid Request",
      error: "This deletion link is invalid or has expired",
    });
  }
  
  // Verify email confirmation matches
  if (deletionRequest.email !== email) {
    return res.render("account/final-confirmation", {
      title: "Confirm Deletion",
      token,
      email: deletionRequest.email,
      error: "Email confirmation does not match",
    });
  }
  
  try {
    // Delete user from database
    await database.deleteUser(email);
    
    // Remove deletion request
    deletionRequests.delete(token);
    
    // Show completion page
    res.render("account/deletion-complete", {
      title: "Account Deleted",
    });
  } catch (error) {
    console.error("Error deleting account:", error);
    return res.render("account/final-confirmation", {
      title: "Confirm Deletion",
      token,
      email: deletionRequest.email,
      error: "An error occurred while deleting your account",
    });
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
      await sendAccountDeletionEmail(email, token, otp);
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
    
    if (!token || !otp) {
      res.status(400).json({ 
        success: false, 
        message: "Token and OTP are required" 
      });
      return;
    }
    
    // Check if token exists and is valid
    const deletionRequest = deletionRequests.get(token);
    if (!deletionRequest) {
      res.status(400).json({ 
        success: false, 
        message: "Invalid or expired token" 
      });
      return;
    }
    
    // Check if OTP matches
    if (deletionRequest.otp !== otp) {
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
    await database.deleteUser(email);
    
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