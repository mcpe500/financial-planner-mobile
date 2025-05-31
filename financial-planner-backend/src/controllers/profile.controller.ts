import { Request, Response } from 'express';
import database from '../services/database.service';
import { UserProfileUpdatePayload } from '../types/profile.types';
import { UserType } from '../types/user.types';
import { convertDateFormat, convertDateToDisplay, isValidDate, isValidPhoneNumber } from '../helpers/dates.helper';

// Define AuthRequest interface to match the auth middleware
interface AuthRequest extends Request {
  user?: UserType;
}

// Sanitize input to prevent XSS attacks
const sanitizeString = (input: string): string => {
  return input.trim().replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '');
};

// Validate and sanitize profile data
const validateAndSanitizeProfileData = (data: any): { isValid: boolean; errors: string[]; sanitized?: Partial<UserProfileUpdatePayload> } => {
  const errors: string[] = [];
  const sanitized: Partial<UserProfileUpdatePayload> = {};

  // Name validation
  if (data.name) {
    const name = sanitizeString(data.name);
    if (name.length < 2 || name.length > 100) {
      errors.push('Name must be between 2 and 100 characters');
    } else {
      sanitized.name = name;
    }
  }

  // Phone validation
  if (data.phone) {
    const phone = sanitizeString(data.phone);
    if (!isValidPhoneNumber(phone)) {
      errors.push('Invalid phone number format');
    } else {
      sanitized.phone = phone;
    }
  }

  // Date validation
  if (data.dateOfBirth) {
    const dateOfBirth = sanitizeString(data.dateOfBirth);
    if (!isValidDate(dateOfBirth)) {
      errors.push('Invalid date format. Use DD/MM/YYYY or YYYY-MM-DD');
    } else {
      sanitized.dateOfBirth = dateOfBirth;
    }
  }

  // Occupation validation
  if (data.occupation) {
    const occupation = sanitizeString(data.occupation);
    if (occupation.length > 100) {
      errors.push('Occupation must be less than 100 characters');
    } else {
      sanitized.occupation = occupation;
    }
  }

  // Monthly income validation
  if (data.monthlyIncome) {
    const monthlyIncome = parseFloat(data.monthlyIncome);
    if (isNaN(monthlyIncome) || monthlyIncome < 0 || monthlyIncome > 999999999) {
      errors.push('Invalid monthly income amount');
    } else {
      sanitized.monthlyIncome = monthlyIncome.toString();
    }
  }

  // Financial goals validation
  if (data.financialGoals) {
    const financialGoals = sanitizeString(data.financialGoals);
    if (financialGoals.length > 500) {
      errors.push('Financial goals must be less than 500 characters');
    } else {
      sanitized.financialGoals = financialGoals;
    }
  }

  return {
    isValid: errors.length === 0,
    errors,
    sanitized: errors.length === 0 ? sanitized : undefined
  };
};

export const syncUserProfile = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const userId = req.user?.id;
    
    if (!userId) {
      res.status(401).json({
        success: false,
        message: 'User not authenticated'
      });
      return;
    }

    // Use comprehensive validation and sanitization
    const validation = validateAndSanitizeProfileData(req.body);
    
    if (!validation.isValid) {
      res.status(400).json({
        success: false,
        message: 'Validation failed',
        errors: validation.errors
      });
      return;
    }

    const profileData = validation.sanitized!;
    console.log(`Updating profile for user ${userId.substring(0, 8)}...`); // Only log partial ID

    // Convert date format from DD/MM/YYYY to YYYY-MM-DD for Supabase
    const convertedProfileData = { ...profileData };
    if (profileData.dateOfBirth) {
      convertedProfileData.dateOfBirth = convertDateFormat(profileData.dateOfBirth);
    }

    // Update user profile in database
    const updatedUser = await database.updateUserProfile(userId, convertedProfileData);

    if (!updatedUser) {
      res.status(404).json({
        success: false,
        message: 'User not found'
      });
      return;
    }

    console.log(`Profile updated successfully for user ${userId.substring(0, 8)}...`);
    
    res.status(200).json({
      success: true,
      message: 'Profile updated successfully',
      data: {
        name: updatedUser.name,
        email: updatedUser.email,
        phone: updatedUser.phone || null,
        dateOfBirth: updatedUser.date_of_birth ? convertDateToDisplay(updatedUser.date_of_birth) : null,
        occupation: updatedUser.occupation || null,
        monthlyIncome: updatedUser.monthly_income ? updatedUser.monthly_income.toString() : null,
        financialGoals: updatedUser.financial_goals || null,
        updatedAt: updatedUser.updated_at || new Date().toISOString()
      }
    });

  } catch (error) {
    console.error('Error updating user profile:', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error while updating profile'
    });
  }
};

export const getUserProfile = async (req: AuthRequest, res: Response): Promise<void> => {
  try {
    const userId = req.user?.id;
    
    if (!userId) {
      res.status(401).json({
        success: false,
        message: 'User not authenticated'
      });
      return;
    }

    const user = await database.getUserById(userId);

    if (!user) {
      res.status(404).json({
        success: false,
        message: 'User not found'
      });
      return;
    }    res.status(200).json({
      success: true,
      data: {
        name: user.name,
        email: user.email,
        phone: user.phone || null,
        dateOfBirth: user.date_of_birth ? convertDateToDisplay(user.date_of_birth) : null,
        occupation: user.occupation || null,
        monthlyIncome: user.monthly_income ? user.monthly_income.toString() : null,
        financialGoals: user.financial_goals || null,
        updatedAt: user.updated_at || user.created_at
      }
    });

  } catch (error) {
    console.error('Error fetching user profile:', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error while fetching profile'
    });
  }
};
