import request from 'supertest';
import app from '../app';
import database from '../services/database.service';
import jwt from 'jsonwebtoken';
import { config } from '../config/config';
import { UserType } from '../types/user.types';

// Mock the database service
jest.mock('../services/database.service');

describe('Transaction Routes', () => {
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

  describe('POST /api/v1/transactions', () => {
    const validTransaction = { amount: 100, type: 'EXPENSE', category: 'Food' };

    it('should create a transaction with valid data', async () => {
      (database.createTransaction as jest.Mock).mockResolvedValue({ id: 'txn-123', ...validTransaction });
      const res = await request(app)
        .post('/api/v1/transactions')
        .set('Authorization', `Bearer ${token}`)
        .send(validTransaction);
      expect(res.status).toBe(201);
      expect(res.body.data).toMatchObject(validTransaction);
    });

    it('should return 401 if no token is provided', async () => {
      const res = await request(app).post('/api/v1/transactions').send(validTransaction);
      expect(res.status).toBe(401);
    });

    it('should return 400 for missing amount', async () => {
        const { amount, ...payload } = validTransaction;
        const res = await request(app)
            .post('/api/v1/transactions')
            .set('Authorization', `Bearer ${token}`)
            .send(payload);
        expect(res.status).toBe(400);
    });

    it('should return 400 for invalid amount (zero)', async () => {
        const res = await request(app)
            .post('/api/v1/transactions')
            .set('Authorization', `Bearer ${token}`)
            .send({ ...validTransaction, amount: 0 });
        expect(res.status).toBe(400);
    });

    it('should return 400 for missing type', async () => {
        const { type, ...payload } = validTransaction;
        const res = await request(app)
            .post('/api/v1/transactions')
            .set('Authorization', `Bearer ${token}`)
            .send(payload);
        expect(res.status).toBe(400);
    });

    it('should return 400 for invalid type', async () => {
        const res = await request(app)
            .post('/api/v1/transactions')
            .set('Authorization', `Bearer ${token}`)
            .send({ ...validTransaction, type: 'INVALID' });
        expect(res.status).toBe(400);
    });

    it('should return 400 for missing category', async () => {
        const { category, ...payload } = validTransaction;
        const res = await request(app)
            .post('/api/v1/transactions')
            .set('Authorization', `Bearer ${token}`)
            .send(payload);
        expect(res.status).toBe(400);
    });
  });

  describe('PUT /api/v1/transactions/:id', () => {
    const transactionId = 'txn-123';
    const updatePayload = { amount: 150, category: 'Updated Category' };

    it('should update a transaction successfully', async () => {
      (database.getTransactionById as jest.Mock).mockResolvedValue({ id: transactionId, user_id: 'user-123' });
      (database.updateTransaction as jest.Mock).mockResolvedValue({ id: transactionId, ...updatePayload });
      const res = await request(app)
        .put(`/api/v1/transactions/${transactionId}`)
        .set('Authorization', `Bearer ${token}`)
        .send(updatePayload);
      expect(res.status).toBe(200);
      expect(res.body.data).toMatchObject(updatePayload);
    });

    it('should return 400 for invalid amount on update', async () => {
        const res = await request(app)
            .put(`/api/v1/transactions/${transactionId}`)
            .set('Authorization', `Bearer ${token}`)
            .send({ amount: -50 });
        expect(res.status).toBe(400);
    });

    it('should return 400 for invalid type on update', async () => {
        const res = await request(app)
            .put(`/api/v1/transactions/${transactionId}`)
            .set('Authorization', `Bearer ${token}`)
            .send({ type: 'INVALID' });
        expect(res.status).toBe(400);
    });
  });

  describe('DELETE /api/v1/transactions/:id', () => {
    const transactionId = 'txn-123';

    it('should delete a transaction successfully', async () => {
      (database.getTransactionById as jest.Mock).mockResolvedValue({ id: transactionId, user_id: 'user-123' });
      (database.deleteTransaction as jest.Mock).mockResolvedValue({});
      const res = await request(app)
        .delete(`/api/v1/transactions/${transactionId}`)
        .set('Authorization', `Bearer ${token}`);
      expect(res.status).toBe(204);
    });

    it('should return 401 if no token is provided', async () => {
      const res = await request(app).delete(`/api/v1/transactions/${transactionId}`);
      expect(res.status).toBe(401);
    });

    it('should return 404 if transaction does not exist', async () => {
      (database.getTransactionById as jest.Mock).mockResolvedValue(null);
      const res = await request(app)
        .delete(`/api/v1/transactions/${transactionId}`)
        .set('Authorization', `Bearer ${token}`);
      expect(res.status).toBe(404);
    });
  });
});