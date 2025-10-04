import request from 'supertest';
import app from '../app';
import database from '../services/database.service';
import jwt from 'jsonwebtoken';
import { config } from '../config/config';
import { UserType } from '../types/user.types';

jest.mock('../services/database.service');

describe('Profile Routes', () => {
  let token: string;
  const testUser: UserType = {
    id: 'user-123',
    email: 'test@example.com',
    name: 'Test User',
    role: 'user',
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString(),
  };

  beforeAll(() => {
    token = jwt.sign({ id: testUser.id }, config.jwt.secret, { expiresIn: '1h' });
  });

  beforeEach(() => {
    (database.getUserById as jest.Mock).mockResolvedValue(testUser);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('GET /api/v1/profile', () => {
    it('should get the user profile successfully', async () => {
      const res = await request(app)
        .get('/api/v1/profile')
        .set('Authorization', `Bearer ${token}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.name).toBe(testUser.name);
    });

    it('should return 401 if no token is provided', async () => {
      const res = await request(app).get('/api/v1/profile');
      expect(res.status).toBe(401);
    });

    it('should return 401 if the user is not found', async () => {
      (database.getUserById as jest.Mock).mockResolvedValue(null);
      const res = await request(app)
        .get('/api/v1/profile')
        .set('Authorization', `Bearer ${token}`);
      expect(res.status).toBe(401);
    });
  });

  describe('PUT /api/v1/profile/update', () => {
    const updatePayload = { name: 'New Name', phone: '081234567890' };

    it('should update the user profile successfully', async () => {
      (database.updateUserProfile as jest.Mock).mockResolvedValue({ ...testUser, ...updatePayload });
      const res = await request(app)
        .put('/api/v1/profile/update')
        .set('Authorization', `Bearer ${token}`)
        .send(updatePayload);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.name).toBe('New Name');
    });

    it('should return 401 if no token is provided', async () => {
      const res = await request(app).put('/api/v1/profile/update').send(updatePayload);
      expect(res.status).toBe(401);
    });

    it('should return 400 for invalid data', async () => {
      const invalidPayload = { phone: '123' };
      const res = await request(app)
        .put('/api/v1/profile/update')
        .set('Authorization', `Bearer ${token}`)
        .send(invalidPayload);
      expect(res.status).toBe(400);
    });
  });
});