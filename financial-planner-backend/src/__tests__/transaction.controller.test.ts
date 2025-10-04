import { Request, Response } from 'express';
import { createTransaction, updateTransaction, deleteTransaction } from '../controllers/transaction.controller';
import database from '../services/database.service';
import { AuthRequest } from '../types/request.types';

// Mock the database service
jest.mock('../services/database.service');

describe('Transaction Controller', () => {
  let req: Partial<AuthRequest>;
  let res: Partial<Response>;

  beforeEach(() => {
    res = {};
    res.status = jest.fn().mockReturnThis();
    res.json = jest.fn().mockReturnThis();
    res.send = jest.fn().mockReturnThis();

    req = {
      user: {
        id: 'user-123',
        email: 'test@example.com',
        name: 'Test User',
        created_at: new Date().toISOString(),
        updated_at: new Date().toISOString(),
        role: 'user',
      },
      body: {},
      params: {},
    };
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('createTransaction', () => {
    // Positive Scenarios
    test('should create a transaction with all valid fields', async () => {
      req.body = { amount: 100, type: 'EXPENSE', date: new Date(), category: 'Food' };
      (database.createTransaction as jest.Mock).mockResolvedValue({ id: 'txn-123', ...req.body });

      await createTransaction(req as AuthRequest, res as Response);

      expect(res.status).toHaveBeenCalledWith(201);
      expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-123', ...req.body } });
    });

    test('should create a transaction with minimum required fields', async () => {
      req.body = { amount: 50, type: 'INCOME' };
      (database.createTransaction as jest.Mock).mockResolvedValue({ id: 'txn-124', ...req.body });

      await createTransaction(req as AuthRequest, res as Response);

      expect(res.status).toHaveBeenCalledWith(201);
      expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-124', ...req.body } });
    });

    test('should create a transaction with a zero amount', async () => {
        req.body = { amount: 0, type: 'EXPENSE', date: new Date(), category: 'Freebie' };
        (database.createTransaction as jest.Mock).mockResolvedValue({ id: 'txn-125', ...req.body });

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(201);
        expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-125', ...req.body } });
      });

      test('should create a transaction with a future date', async () => {
        const futureDate = new Date();
        futureDate.setDate(futureDate.getDate() + 7);
        req.body = { amount: 200, type: 'INCOME', date: futureDate, category: 'Salary' };
        (database.createTransaction as jest.Mock).mockResolvedValue({ id: 'txn-126', ...req.body });

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(201);
        expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-126', ...req.body } });
      });

      test('should create a transaction with a long category name', async () => {
        req.body = { amount: 75, type: 'EXPENSE', category: 'This is a very long category name for testing purposes' };
        (database.createTransaction as jest.Mock).mockResolvedValue({ id: 'txn-127', ...req.body });

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(201);
        expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-127', ...req.body } });
      });

      test('should create a transaction with special characters in the note', async () => {
        req.body = { amount: 120, type: 'EXPENSE', note: 'Transaction with @#$%' };
        (database.createTransaction as jest.Mock).mockResolvedValue({ id: 'txn-128', ...req.body });

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(201);
        expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-128', ...req.body } });
      });

      test('should create a transaction with a very large amount', async () => {
        req.body = { amount: 999999999, type: 'INCOME' };
        (database.createTransaction as jest.Mock).mockResolvedValue({ id: 'txn-129', ...req.body });

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(201);
        expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-129', ...req.body } });
      });

      test('should create a transaction with tags', async () => {
        req.body = { amount: 80, type: 'EXPENSE', tags: ['urgent', 'monthly'] };
        (database.createTransaction as jest.Mock).mockResolvedValue({ id: 'txn-130', ...req.body });

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(201);
        expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-130', ...req.body } });
      });

      test('should create a transaction with a null note', async () => {
        req.body = { amount: 60, type: 'INCOME', note: null };
        (database.createTransaction as jest.Mock).mockResolvedValue({ id: 'txn-131', ...req.body });

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(201);
        expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-131', ...req.body } });
      });

      test('should create a transaction with an empty note', async () => {
        req.body = { amount: 40, type: 'EXPENSE', note: '' };
        (database.createTransaction as jest.Mock).mockResolvedValue({ id: 'txn-132', ...req.body });

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(201);
        expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-132', ...req.body } });
      });

    // Negative Scenarios
    test('should return 401 if user is not authenticated', async () => {
      req.user = undefined;

      await createTransaction(req as AuthRequest, res as Response);

      expect(res.status).toHaveBeenCalledWith(401);
      expect(res.json).toHaveBeenCalledWith({ message: 'Authentication required' });
    });

    test('should return 500 if database throws an error', async () => {
        req.body = { amount: 100, type: 'EXPENSE' };
        const errorMessage = 'Database error';
        (database.createTransaction as jest.Mock).mockRejectedValue(new Error(errorMessage));

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(500);
        expect(res.json).toHaveBeenCalledWith({
          success: false,
          message: 'Failed to create transaction',
          error: errorMessage,
        });
      });

      test('should handle transactions with a missing amount', async () => {
        req.body = { type: 'EXPENSE', category: 'Groceries' };
        (database.createTransaction as jest.Mock).mockRejectedValue(new Error('Amount is required'));

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(500);
        expect(res.json).toHaveBeenCalledWith(expect.objectContaining({ message: 'Failed to create transaction' }));
      });

      test('should handle transactions with a missing type', async () => {
        req.body = { amount: 150, category: 'Utilities' };
        (database.createTransaction as jest.Mock).mockRejectedValue(new Error('Type is required'));

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(500);
        expect(res.json).toHaveBeenCalledWith(expect.objectContaining({ message: 'Failed to create transaction' }));
      });

      test('should handle invalid data types in payload', async () => {
        req.body = { amount: 'one hundred', type: 'EXPENSE' };
        (database.createTransaction as jest.Mock).mockRejectedValue(new Error('Invalid data type for amount'));

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(500);
        expect(res.json).toHaveBeenCalledWith(expect.objectContaining({ message: 'Failed to create transaction' }));
      });

      test('should handle empty payload', async () => {
        req.body = {};
        (database.createTransaction as jest.Mock).mockRejectedValue(new Error('Empty payload'));

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(500);
        expect(res.json).toHaveBeenCalledWith(expect.objectContaining({ message: 'Failed to create transaction' }));
      });

      test('should handle database connection failure', async () => {
        req.body = { amount: 200, type: 'INCOME' };
        const errorMessage = 'Unable to connect to the database';
        (database.createTransaction as jest.Mock).mockRejectedValue(new Error(errorMessage));

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(500);
        expect(res.json).toHaveBeenCalledWith({
          success: false,
          message: 'Failed to create transaction',
          error: errorMessage,
        });
      });

      test('should handle unexpected null user object', async () => {
        req.user = undefined;

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(401);
        expect(res.json).toHaveBeenCalledWith({ message: 'Authentication required' });
      });

      test('should handle errors thrown from within the controller logic itself', async () => {
        req.body = { amount: 100, type: 'EXPENSE' };
        const errorMessage = 'Unexpected error';
        (database.createTransaction as jest.Mock).mockImplementation(() => {
          throw new Error(errorMessage);
        });

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(500);
        expect(res.json).toHaveBeenCalledWith({
          success: false,
          message: 'Failed to create transaction',
          error: errorMessage,
        });
      });

      test('should handle transactions with negative amount', async () => {
        req.body = { amount: -100, type: 'EXPENSE' };
        (database.createTransaction as jest.Mock).mockRejectedValue(new Error('Amount cannot be negative'));

        await createTransaction(req as AuthRequest, res as Response);

        expect(res.status).toHaveBeenCalledWith(500);
        expect(res.json).toHaveBeenCalledWith(expect.objectContaining({ message: 'Failed to create transaction' }));
      });
  });

  describe('updateTransaction', () => {
    beforeEach(() => {
      req.params = { id: 'txn-123' };
      (database.getTransactionById as jest.Mock).mockResolvedValue({ id: 'txn-123', user_id: 'user-123' });
    });

    // Positive Scenarios
    test('should update a transaction with valid data', async () => {
      req.body = { amount: 150, category: 'Updated Category' };
      (database.updateTransaction as jest.Mock).mockResolvedValue({ id: 'txn-123', ...req.body });

      await updateTransaction(req as AuthRequest, res as Response);

      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-123', ...req.body } });
    });

    test('should allow updating only the amount', async () => {
      req.body = { amount: 200 };
      (database.updateTransaction as jest.Mock).mockResolvedValue({ id: 'txn-123', amount: 200 });

      await updateTransaction(req as AuthRequest, res as Response);

      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-123', amount: 200 } });
    });

    test('should allow updating only the category', async () => {
      req.body = { category: 'New Category' };
      (database.updateTransaction as jest.Mock).mockResolvedValue({ id: 'txn-123', category: 'New Category' });

      await updateTransaction(req as AuthRequest, res as Response);

      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-123', category: 'New Category' } });
    });

    test('should handle updates with extra fields gracefully', async () => {
      req.body = { amount: 100, extraField: 'should be ignored' };
      (database.updateTransaction as jest.Mock).mockResolvedValue({ id: 'txn-123', amount: 100 });

      await updateTransaction(req as AuthRequest, res as Response);

      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-123', amount: 100 } });
    });

    test('should allow updating with an empty note', async () => {
      req.body = { note: '' };
      (database.updateTransaction as jest.Mock).mockResolvedValue({ id: 'txn-123', note: '' });

      await updateTransaction(req as AuthRequest, res as Response);

      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-123', note: '' } });
    });

    test('should allow updating with a null note', async () => {
      req.body = { note: null };
      (database.updateTransaction as jest.Mock).mockResolvedValue({ id: 'txn-123', note: null });
      await updateTransaction(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-123', note: null } });
    });

    test('should allow updating tags', async () => {
      req.body = { tags: ['updated-tag'] };
      (database.updateTransaction as jest.Mock).mockResolvedValue({ id: 'txn-123', tags: ['updated-tag'] });
      await updateTransaction(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-123', tags: ['updated-tag'] } });
    });

    test('should allow updating to a zero amount', async () => {
      req.body = { amount: 0 };
      (database.updateTransaction as jest.Mock).mockResolvedValue({ id: 'txn-123', amount: 0 });
      await updateTransaction(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-123', amount: 0 } });
    });

    test('should allow updating with a very long note', async () => {
        const longNote = 'a'.repeat(1000);
        req.body = { note: longNote };
        (database.updateTransaction as jest.Mock).mockResolvedValue({ id: 'txn-123', note: longNote });
        await updateTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(200);
        expect(res.json).toHaveBeenCalledWith({ success: true, data: { id: 'txn-123', note: longNote } });
    });

    test('should handle empty update payload gracefully', async () => {
        req.body = {};
        (database.updateTransaction as jest.Mock).mockResolvedValue({ id: 'txn-123' });
        await updateTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(200);
    });

    // Negative Scenarios
    test('should return 401 if user is not authenticated', async () => {
      req.user = undefined;
      await updateTransaction(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(401);
      expect(res.json).toHaveBeenCalledWith({ message: 'Authentication required' });
    });

    test('should return 404 if transaction is not found', async () => {
      (database.getTransactionById as jest.Mock).mockResolvedValue(null);
      await updateTransaction(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(404);
      expect(res.json).toHaveBeenCalledWith({ message: 'Transaction not found' });
    });

    test('should return 404 if transaction belongs to another user', async () => {
      (database.getTransactionById as jest.Mock).mockResolvedValue({ id: 'txn-123', user_id: 'user-456' });
      await updateTransaction(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(404);
      expect(res.json).toHaveBeenCalledWith({ message: 'Transaction not found' });
    });

    test('should return 500 if database fails to update', async () => {
      const errorMessage = 'Database update failed';
      (database.updateTransaction as jest.Mock).mockRejectedValue(new Error(errorMessage));
      req.body = { amount: 150 };
      await updateTransaction(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(500);
      expect(res.json).toHaveBeenCalledWith({
        success: false,
        message: 'Failed to update transaction',
        error: errorMessage,
      });
    });

    test('should return 500 if getTransactionById fails', async () => {
        const errorMessage = 'Database retrieval failed';
        (database.getTransactionById as jest.Mock).mockRejectedValue(new Error(errorMessage));
        req.body = { amount: 150 };
        await updateTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(500);
    });

    test('should handle invalid amount type', async () => {
        req.body = { amount: 'invalid-amount' };
        const errorMessage = 'Invalid amount';
        (database.updateTransaction as jest.Mock).mockRejectedValue(new Error(errorMessage));
        await updateTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(500);
    });

    test('should handle invalid transaction ID format', async () => {
        req.params = { id: 'invalid-id' };
        const errorMessage = 'Invalid ID';
        (database.getTransactionById as jest.Mock).mockRejectedValue(new Error(errorMessage));
        await updateTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(500);
    });

    test('should handle database timeout during update', async () => {
        const errorMessage = 'Database timeout';
        (database.updateTransaction as jest.Mock).mockRejectedValue(new Error(errorMessage));
        req.body = { amount: 150 };
        await updateTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(500);
    });

    test('should handle database timeout during getTransactionById', async () => {
        const errorMessage = 'Database timeout';
        (database.getTransactionById as jest.Mock).mockRejectedValue(new Error(errorMessage));
        req.body = { amount: 150 };
        await updateTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(500);
    });

    test('should handle update with negative amount', async () => {
        req.body = { amount: -100 };
        const errorMessage = 'Amount cannot be negative';
        (database.updateTransaction as jest.Mock).mockRejectedValue(new Error(errorMessage));
        await updateTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(500);
    });
  });

  describe('deleteTransaction', () => {
    beforeEach(() => {
      req.params = { id: 'txn-123' };
      (database.getTransactionById as jest.Mock).mockResolvedValue({ id: 'txn-123', user_id: 'user-123' });
    });

    // Positive Scenarios
    test('should delete a transaction successfully', async () => {
      (database.deleteTransaction as jest.Mock).mockResolvedValue(undefined);
      await deleteTransaction(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(204);
    });

    test('should allow deleting a transaction with a zero amount', async () => {
        (database.getTransactionById as jest.Mock).mockResolvedValue({ id: 'txn-123', user_id: 'user-123', amount: 0 });
        (database.deleteTransaction as jest.Mock).mockResolvedValue(undefined);
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(204);
    });

    test('should allow deleting a transaction with a large amount', async () => {
        (database.getTransactionById as jest.Mock).mockResolvedValue({ id: 'txn-123', user_id: 'user-123', amount: 999999999 });
        (database.deleteTransaction as jest.Mock).mockResolvedValue(undefined);
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(204);
    });

    test('should allow deleting the last remaining transaction', async () => {
        (database.deleteTransaction as jest.Mock).mockResolvedValue(undefined);
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(204);
    });

    test('should allow deleting a transaction with a long note', async () => {
        (database.getTransactionById as jest.Mock).mockResolvedValue({ id: 'txn-123', user_id: 'user-123', note: 'a'.repeat(1000) });
        (database.deleteTransaction as jest.Mock).mockResolvedValue(undefined);
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(204);
    });

    test('should allow deleting a transaction with tags', async () => {
        (database.getTransactionById as jest.Mock).mockResolvedValue({ id: 'txn-123', user_id: 'user-123', tags: ['test'] });
        (database.deleteTransaction as jest.Mock).mockResolvedValue(undefined);
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(204);
    });

    test('should handle deletion idempotency (deleting a deleted transaction)', async () => {
        (database.getTransactionById as jest.Mock).mockResolvedValue(null);
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(404);
    });

    test('should allow deleting a transaction with a future date', async () => {
        const futureDate = new Date();
        futureDate.setDate(futureDate.getDate() + 7);
        (database.getTransactionById as jest.Mock).mockResolvedValue({ id: 'txn-123', user_id: 'user-123', date: futureDate });
        (database.deleteTransaction as jest.Mock).mockResolvedValue(undefined);
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(204);
    });

    test('should allow deleting a transaction with special characters in note', async () => {
        (database.getTransactionById as jest.Mock).mockResolvedValue({ id: 'txn-123', user_id: 'user-123', note: '@#$%' });
        (database.deleteTransaction as jest.Mock).mockResolvedValue(undefined);
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(204);
    });

    test('should allow deleting a transaction with a complex ID if found', async () => {
        req.params = { id: 'complex-id-!@#$-123' };
        (database.getTransactionById as jest.Mock).mockResolvedValue({ id: 'complex-id-!@#$-123', user_id: 'user-123' });
        (database.deleteTransaction as jest.Mock).mockResolvedValue(undefined);
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(204);
    });

    // Negative Scenarios
    test('should return 401 if user is not authenticated', async () => {
      req.user = undefined;
      await deleteTransaction(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(401);
      expect(res.json).toHaveBeenCalledWith({ message: 'Authentication required' });
    });

    test('should return 404 if transaction is not found', async () => {
      (database.getTransactionById as jest.Mock).mockResolvedValue(null);
      await deleteTransaction(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(404);
      expect(res.json).toHaveBeenCalledWith({ message: 'Transaction not found' });
    });

    test('should return 404 if transaction belongs to another user', async () => {
      (database.getTransactionById as jest.Mock).mockResolvedValue({ id: 'txn-123', user_id: 'user-456' });
      await deleteTransaction(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(404);
      expect(res.json).toHaveBeenCalledWith({ message: 'Transaction not found' });
    });

    test('should return 500 if database fails to delete', async () => {
      const errorMessage = 'Database delete failed';
      (database.deleteTransaction as jest.Mock).mockRejectedValue(new Error(errorMessage));
      await deleteTransaction(req as AuthRequest, res as Response);
      expect(res.status).toHaveBeenCalledWith(500);
      expect(res.json).toHaveBeenCalledWith({
        success: false,
        message: 'Failed to delete transaction',
        error: errorMessage,
      });
    });

    test('should return 500 if getTransactionById fails during delete', async () => {
        const errorMessage = 'Database retrieval failed';
        (database.getTransactionById as jest.Mock).mockRejectedValue(new Error(errorMessage));
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(500);
    });

    test('should handle invalid transaction ID format on delete', async () => {
        req.params = { id: 'invalid-id' };
        const errorMessage = 'Invalid ID';
        (database.getTransactionById as jest.Mock).mockRejectedValue(new Error(errorMessage));
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(500);
    });

    test('should handle database timeout on delete', async () => {
        const errorMessage = 'Database timeout';
        (database.deleteTransaction as jest.Mock).mockRejectedValue(new Error(errorMessage));
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(500);
    });

    test('should handle database timeout on getTransactionById during delete', async () => {
        const errorMessage = 'Database timeout';
        (database.getTransactionById as jest.Mock).mockRejectedValue(new Error(errorMessage));
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(500);
    });

    test('should handle case where transaction ID is missing', async () => {
        req.params = { id: undefined } as any;
        // This would likely be caught by routing or validation before the controller
        // but we test the controller's robustness.
        const errorMessage = 'Transaction ID is required';
        (database.getTransactionById as jest.Mock).mockRejectedValue(new Error(errorMessage));
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(500);
    });

    test('should handle very long, non-existent transaction ID', async () => {
        req.params = { id: 'a'.repeat(1000) };
        (database.getTransactionById as jest.Mock).mockResolvedValue(null);
        await deleteTransaction(req as AuthRequest, res as Response);
        expect(res.status).toHaveBeenCalledWith(404);
    });
  });
});