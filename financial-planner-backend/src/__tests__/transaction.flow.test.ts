import request from 'supertest';
import app from '../app';
import database from '../services/database.service';
import jwt from 'jsonwebtoken';
import { config } from '../config/config';
import { UserType } from '../types/user.types';
import { TransactionType as Transaction } from '../types/transaction.types';

// Mock the database service
jest.mock('../services/database.service');

describe('Transaction API Flow Tests', () => {
  let token: string;
  const testUser: UserType = {
    id: 'user-123',
    email: 'test@example.com',
    name: 'Test User',
    role: 'user',
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString(),
  };
  let transactionsDB: Transaction[] = [];

  beforeAll(() => {
    token = jwt.sign({ id: testUser.id }, config.jwt.secret, { expiresIn: '1h' });
  });

  beforeEach(() => {
    transactionsDB = [];
    (database.getUserById as jest.Mock).mockResolvedValue(testUser);

    // Mock implementations
    (database.createTransaction as jest.Mock).mockImplementation((userId: string, payload: any) => {
      const newTx: Transaction = {
        id: `flow-tx-${transactionsDB.length + 1}`,
        user_id: userId,
        created_at: new Date().toISOString(),
        updated_at: new Date().toISOString(),
        ...payload,
        is_from_receipt: false,
        is_synced: false,
        type: payload.type || 'EXPENSE',
        date: payload.date || new Date().toISOString(),
        pocket: payload.pocket || 'Cash',
      };
      transactionsDB.push(newTx);
      return Promise.resolve(newTx);
    });

    (database.getTransactionById as jest.Mock).mockImplementation((id: string) => {
      const tx = transactionsDB.find(t => t.id === id && t.user_id === 'user-123');
      return Promise.resolve(tx || null);
    });

    (database.getUserTransactions as jest.Mock).mockImplementation((userId: string) => {
        const txs = transactionsDB.filter(t => t.user_id === userId);
        return Promise.resolve(txs);
    });

    (database.updateTransaction as jest.Mock).mockImplementation((id: string, payload: any) => {
      const txIndex = transactionsDB.findIndex(t => t.id === id);
      if (txIndex === -1) return Promise.resolve(null);
      transactionsDB[txIndex] = { ...transactionsDB[txIndex], ...payload };
      return Promise.resolve(transactionsDB[txIndex]);
    });

    (database.deleteTransaction as jest.Mock).mockImplementation((id: string) => {
      const txIndex = transactionsDB.findIndex(t => t.id === id);
      if (txIndex > -1) {
        transactionsDB.splice(txIndex, 1);
      }
      return Promise.resolve({});
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should allow a full lifecycle: CREATE -> READ -> UPDATE -> DELETE', async () => {
    const createPayload = { amount: 100, type: 'EXPENSE', category: 'Initial' };
    const createRes = await request(app).post('/api/v1/transactions').set('Authorization', `Bearer ${token}`).send(createPayload);
    expect(createRes.status).toBe(201);
    const transactionId = createRes.body.data.id;

    const readRes1 = await request(app).get(`/api/v1/transactions/${transactionId}`).set('Authorization', `Bearer ${token}`);
    expect(readRes1.status).toBe(200);
    expect(readRes1.body.data.category).toBe('Initial');

    const updatePayload = { category: 'Updated' };
    const updateRes = await request(app).put(`/api/v1/transactions/${transactionId}`).set('Authorization', `Bearer ${token}`).send(updatePayload);
    expect(updateRes.status).toBe(200);
    expect(updateRes.body.data.category).toBe('Updated');

    const deleteRes = await request(app).delete(`/api/v1/transactions/${transactionId}`).set('Authorization', `Bearer ${token}`);
    expect(deleteRes.status).toBe(204);

    const readRes3 = await request(app).get(`/api/v1/transactions/${transactionId}`).set('Authorization', `Bearer ${token}`);
    expect(readRes3.status).toBe(404);
  });

  it('should create multiple transactions and list them correctly', async () => {
    await request(app).post('/api/v1/transactions').set('Authorization', `Bearer ${token}`).send({ amount: 10, type: 'EXPENSE', category: 'A' });
    await request(app).post('/api/v1/transactions').set('Authorization', `Bearer ${token}`).send({ amount: 20, type: 'EXPENSE', category: 'B' });
    const res = await request(app).get('/api/v1/transactions').set('Authorization', `Bearer ${token}`);
    expect(res.status).toBe(200);
    expect(res.body.data).toHaveLength(2);
  });

  it('should fail to read a transaction from another user', async () => {
    const createRes = await request(app).post('/api/v1/transactions').set('Authorization', `Bearer ${token}`).send({ amount: 100, type: 'EXPENSE', category: 'A' });
    const txId = createRes.body.data.id;
    (database.getTransactionById as jest.Mock).mockResolvedValueOnce({ id: txId, user_id: 'other-user' });
    const res = await request(app).get(`/api/v1/transactions/${txId}`).set('Authorization', `Bearer ${token}`);
    expect(res.status).toBe(404);
  });

  it('should fail with an invalid token', async () => {
    const createPayload = { amount: 100, type: 'EXPENSE', category: 'A' };
    const res = await request(app).post('/api/v1/transactions').set('Authorization', 'Bearer invalidtoken').send(createPayload);
    expect(res.status).toBe(401);
  });

  it('should fail without a token', async () => {
    const createPayload = { amount: 100, type: 'EXPENSE', category: 'A' };
    const res = await request(app).post('/api/v1/transactions').send(createPayload);
    expect(res.status).toBe(401);
  });
});