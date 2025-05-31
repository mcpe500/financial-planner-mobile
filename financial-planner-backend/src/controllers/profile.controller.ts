import { Request, Response } from 'express';
import database from '../services/database.service';
import { UserProfileUpdatePayload } from '../types/profile.types';
import { UserType } from '../types/user.types';
import { convertDateFormat, convertDateToDisplay, isValidDate, isValidPhoneNumber } from '../helpers/dates.helper';

// Define AuthRequest interface to match the auth middleware
interface AuthRequest extends Request {
  user?: UserType;
}

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

    const profileData: UserProfileUpdatePayload = req.body;

    // Basic validation
    if (!profileData.name || profileData.name.trim().length === 0) {
      res.status(400).json({
        success: false,
        message: 'Name is required'
      });
      return;
    }

    // Validate phone format if provided
    if (profileData.phone && !isValidPhoneNumber(profileData.phone)) {
      res.status(400).json({
        success: false,
        message: 'Invalid phone number format'
      });
      return;
    }

    // Validate date format if provided
    if (profileData.dateOfBirth && !isValidDate(profileData.dateOfBirth)) {
      res.status(400).json({
        success: false,
        message: 'Invalid date format. Use DD/MM/YYYY or YYYY-MM-DD'
      });
      return;
    }    console.log(`Updating profile for user ${userId}:`, profileData);

    // Convert date format from DD/MM/YYYY to YYYY-MM-DD for Supabase
    const convertedProfileData = { ...profileData };
    if (profileData.dateOfBirth) {
      convertedProfileData.dateOfBirth = convertDateFormat(profileData.dateOfBirth);
      console.log(`Date conversion: ${profileData.dateOfBirth} -> ${convertedProfileData.dateOfBirth}`);
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

    console.log(`Profile updated successfully for user ${userId}`);    res.status(200).json({
      success: true,
      message: 'Profile updated successfully',
      data: {
        id: updatedUser.id,
        name: updatedUser.name,
        email: updatedUser.email,
        phone: updatedUser.phone || null,
        dateOfBirth: convertDateToDisplay(updatedUser.date_of_birth) || null,
        occupation: updatedUser.occupation || null,
        monthlyIncome: updatedUser.monthly_income || null,
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
        id: user.id,
        name: user.name,
        email: user.email,
        phone: (user as any).phone || null,
        dateOfBirth: convertDateToDisplay((user as any).date_of_birth) || null,
        occupation: (user as any).occupation || null,
        monthlyIncome: (user as any).monthly_income || null,
        financialGoals: (user as any).financial_goals || null,
        updatedAt: (user as any).updated_at || user.created_at
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
