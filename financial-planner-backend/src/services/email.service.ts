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
  },
  connectionTimeout: 10000,
  greetingTimeout: 5000,
  socketTimeout: 10000
});

/**
 * Sends an email with only an OTP for in-app account deletion confirmation.
 */
export async function sendDeletionOtpEmail(
  email: string,
  otp: string
): Promise<void> {
  const mailOptions = {
    from: `"SolFin" <${process.env.EMAIL_USER}>`,
    to: email,
    subject: 'Confirm Your Account Deletion',
    html: `
      <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
        <h2 style="color: #c0392b; text-align: center;">Account Deletion Confirmation</h2>
        <p>We have received a request to delete your account. To confirm this action, please use the One-Time Password (OTP) below.</p>
        <div style="background-color: #f9f9f9; padding: 20px; margin: 20px 0; text-align: center; border-radius: 5px;">
          <p style="font-size: 16px; margin: 0;">Your OTP is:</p>
          <p style="font-size: 32px; font-weight: bold; letter-spacing: 5px; margin: 10px 0; color: #333;">${otp}</p>
          <p style="font-size: 14px; color: #777;">This OTP is valid for 10 minutes.</p>
        </div>
        <p>Enter this code in the application to finalize the deletion of your account. If you did not request this, please ignore this email or contact our support immediately.</p>
        <hr style="border: none; border-top: 1px solid #eee; margin-top: 20px;" />
        <p style="font-size: 12px; color: #999; text-align: center;">Thank you for using our service.</p>
      </div>
    `,
  };
  await transporter.sendMail(mailOptions);
}

/**
 * Send account deletion confirmation email with OTP and link for the web flow.
 * @deprecated Use sendDeletionOtpEmail for app flow.
 */
export const sendAccountDeletionEmailForWeb = async (email: string, token: string, otp: string): Promise<void> => {
  try {
    // Get the base URL from config or environment
    const baseUrl = process.env.APP_URL || "http://localhost:3000";
    const confirmationUrl = `${baseUrl}/account/delete/verify/${token}`;

    const mailOptions = {
      from: `"SolFin" <${process.env.EMAIL_USER}>`,
      to: email,
      subject: "Account Deletion Request",
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 5px;">
          <h2 style="color: #d9534f;">Account Deletion Request</h2>
          <p>We received a request to delete your SolFin account. If you did not request this, please ignore this email.</p>
          
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
  } catch (error) {
    console.error('Email sending failed:', error);
    throw new Error('Failed to send deletion email');
  }
};