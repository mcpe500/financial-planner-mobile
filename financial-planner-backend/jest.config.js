module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  testMatch: ['**/__tests__/**/*.test.ts'],
  moduleNameMapper: {
    '^../services/database.service$': '<rootDir>/src/__tests__/mocks/database.service.ts',
    '^../services/genAI.service$': '<rootDir>/src/__tests__/mocks/genAI.service.ts',
  },
};