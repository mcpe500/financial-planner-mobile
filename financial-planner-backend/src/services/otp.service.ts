import crypto from 'crypto';
import databaseService from './database.service';

const OTP_EXPIRATION_MINUTES = 10;

/**
 * Generates a 6-digit numeric OTP and its expiration date.
 */
export function generateOtp(): { otp: string; expiresAt: Date } {
  const otp = crypto.randomInt(100000, 999999).toString();
  const expiresAt = new Date();
  expiresAt.setMinutes(expiresAt.getMinutes() + OTP_EXPIRATION_MINUTES);
  return { otp, expiresAt };
}

/**
 * Stores a deletion OTP for a user in the database.
 * @param userId - The ID of the user.
 * @param otp - The OTP to store.
 * @param expiresAt - The expiration date of the OTP.
 */
export async function storeOtp(
  userId: string,
  otp: string,
  expiresAt: Date
): Promise<void> {
  await databaseService.updateUser(userId, {
    deletion_otp: otp,
    deletion_otp_expires_at: expiresAt,
  });
}

/**
 * Verifies a deletion OTP for a user.
 * @param userId - The ID of the user.
 * @param otp - The OTP to verify.
 * @returns True if the OTP is valid and not expired, false otherwise.
 */
export async function verifyOtp(
  userId: string,
  otp: string
): Promise<boolean> {
  const user = await databaseService.findUserById(userId);
  if (
    !user ||
    !user.deletion_otp ||
    !user.deletion_otp_expires_at
  ) {
    return false;
  }

  const isOtpValid = user.deletion_otp === otp;
  const isOtpExpired = new Date() > new Date(user.deletion_otp_expires_at);

  if (!isOtpValid || isOtpExpired) {
    return false;
  }

  // OTP is valid, clear it after verification
  await databaseService.updateUser(userId, {
    deletion_otp: null,
    deletion_otp_expires_at: null,
  });

  return true;
} 