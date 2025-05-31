import { Router } from 'express';
import { authenticate } from '../middleware/auth.middleware';
import { syncUserProfile, getUserProfile } from '../controllers/profile.controller';

const router = Router();

// Get user profile
router.get('/', authenticate, getUserProfile);

// Update user profile (sync)
router.put('/update', authenticate, syncUserProfile);

export default router;