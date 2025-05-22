import nodemailer from "nodemailer";
import { config } from "../config/config";

// Create NodeMailer transporter 
const transporter = nodemailer.createTransport({
  host: process.env.EMAIL_HOST || "smtp.gmail.com",
  port: parseInt(process.env.EMAIL_PORT || "587", 10),
  secure: process.env.EMAIL_SECURE === "true",
  auth: {
    user: process.env.EMAIL_USER || "",
    pass: process.env.EMAIL_PASSWORD || ""
  }
});

/**
 * Send account deletion confirmation email with OTP and link
 */
export const sendAccountDeletionEmail = async (email: string, token: string, otp: string): Promise<void> => {
  // Get the base URL from config or environment
  const baseUrl = process.env.APP_URL || "http://localhost:3000";
  const confirmationUrl = `${baseUrl}/account/delete/verify/${token}`;

  const mailOptions = {
    from: `"Financial Planner" <${process.env.EMAIL_USER}>`,
    to: email,
    subject: "Account Deletion Request",
    html: `
      <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 5px;">
        <h2 style="color: #d9534f;">Account Deletion Request</h2>
        <p>We received a request to delete your Financial Planner account. If you did not request this, please ignore this email.</p>
        
        <div style="background-color: #f8f8f8; padding: 15px; border-radius: 5px; margin: 20px 0; text-align: center;">
          <p style="font-weight: bold; margin-bottom: 10px;">Your One-Time Password (OTP):</p>
          <p style="font-size: 24px; letter-spacing: 5px; font-weight: bold;">${otp}</p>
          <p>This OTP will expire in 1 hour</p>
        </div>
        
        <p>To proceed with account deletion:</p>
        <ol>
          <li>Click the button below</li>
          <li>Enter the OTP shown above</li>
          <li>Confirm your email address</li>
        </ol>
        
        <div style="text-align: center; margin: 30px 0;">
          <a href="${confirmationUrl}" style="background-color: #d9534f; color: white; padding: 12px 20px; text-decoration: none; border-radius: 4px; font-weight: bold;">I Want To Delete My Account</a>
        </div>
        
        <p>Or copy and paste this link: <a href="${confirmationUrl}">${confirmationUrl}</a></p>
        
        <p style="color: #777; margin-top: 30px; font-size: 12px;">
          If you did not request to delete your account, you can safely ignore this email.
        </p>
      </div>
    `
  };

  await transporter.sendMail(mailOptions);
};