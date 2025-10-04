import { Request, Response } from 'express';
import { getUserProfile, syncUserProfile } from '../controllers/profile.controller';
import database from '../services/database.service';
import { AuthRequest } from '../types/request.types';
import { UserType } from '../types/user.types';

jest.mock('../services/database.service');

describe('Profile Controller', () => {
  let req: Partial<AuthRequest>;
  let res: Partial<Response>;
  const testUser: UserType = {
    id: 'user-123',
    email: 'test@example.com',
    name: 'Test User',
    role: 'user',
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString(),
  };

  beforeEach(() => {
    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis(),
    };
    req = {
      user: testUser,
      body: {},
    };
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('getUserProfile', () => {
    it('should return the user profile successfully', async () => {
      (database.getUserById as jest.Mock).mockResolvedValue(testUser);
      await getUserProfile(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith(expect.objectContaining({
        success: true,
        data: expect.objectContaining({ name: 'Test User' }),
      }));
    });

    it('should return 401 if user is not authenticated', async () => {
      req.user = undefined;
      await getUserProfile(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(401);
    });

    it('should return 404 if user is not found in database', async () => {
      (database.getUserById as jest.Mock).mockResolvedValue(null);
      await getUserProfile(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(404);
    });

    it('should return 500 on database error', async () => {
      (database.getUserById as jest.Mock).mockRejectedValue(new Error('DB Error'));
      await getUserProfile(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(500);
    });
  });

  describe('syncUserProfile', () => {
    const validProfileData = { name: 'Updated Name', phone: '081234567890' };

    it('should update the user profile successfully', async () => {
      req.body = validProfileData;
      (database.updateUserProfile as jest.Mock).mockResolvedValue({ ...testUser, ...validProfileData });
      await syncUserProfile(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith(expect.objectContaining({
        success: true,
        data: expect.objectContaining({ name: 'Updated Name' }),
      }));
    });

    it('should return 401 if user is not authenticated', async () => {
      req.user = undefined;
      await syncUserProfile(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(401);
    });

    it('should return 400 for invalid data', async () => {
      req.body = { name: 'A' }; // Invalid name
      await syncUserProfile(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(400);
    });

    it('should return 404 if user to update is not found', async () => {
        req.body = validProfileData;
        (database.updateUserProfile as jest.Mock).mockResolvedValue(null);
        await syncUserProfile(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(404);
    });

    it('should return 500 on database error', async () => {
        req.body = validProfileData;
        (database.updateUserProfile as jest.Mock).mockRejectedValue(new Error('DB Error'));
        await syncUserProfile(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(500);
    });
  });
});