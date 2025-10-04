import { Request, Response, NextFunction } from 'express';
import { body, validationResult } from 'express-validator';

export const validateTransactionCreation = [
  body('amount', 'Amount must be a valid number').isFloat({ gt: 0 }),
  body('type', 'Type must be either INCOME or EXPENSE').isIn(['INCOME', 'EXPENSE']),
  body('category', 'Category is required').not().isEmpty(),
  (req: Request, res: Response, next: NextFunction) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ success: false, errors: errors.array() });
    }
    next();
  },
];

export const validateTransactionUpdate = [
  body('amount', 'Amount must be a valid number').optional().isFloat({ gt: 0 }),
  body('type', 'Type must be either INCOME or EXPENSE').optional().isIn(['INCOME', 'EXPENSE']),
  body('category', 'Category is required').optional().not().isEmpty(),
  (req: Request, res: Response, next: NextFunction) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ success: false, errors: errors.array() });
    }
    next();
  },
];