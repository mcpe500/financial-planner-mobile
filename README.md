# Financial Planner

This repository contains the full-stack Financial Planner application, including a Node.js backend and Android frontends.

## Overview

The project is divided into three main parts:
-   **Backend**: A robust and scalable server built with Node.js, Express, and TypeScript.
-   **Frontend (Android)**: A native Android application.
-   **Frontend (Jetpack Compose)**: A modern Android application built with Jetpack Compose.

## Backend Service

The backend service handles all the core business logic, from user authentication to transaction management. The application uses Supabase for its database and provides a comprehensive RESTful API for the frontend clients.

This project is built with a focus on code quality, maintainability, and thorough testing, featuring a full suite of unit, integration, and API flow tests.

### ✨ Backend Features

- **User Authentication**: Secure user sign-up and login using JWT and Google OAuth.
- **Transaction Management**: Full CRUD (Create, Read, Update, Delete) functionality for user transactions.
- **Wallet Management**: APIs to manage user wallets and financial pockets.
- **Profile Management**: Endpoints for users to manage their profile information.
- **Receipt Processing**: AI-powered OCR for scanning receipts and automatically creating transactions.
- **Comprehensive Testing**: Includes unit, integration, and API flow tests to ensure reliability and code quality.

### 🛠️ Backend Tech Stack

- **Backend**: Node.js, Express.js
- **Language**: TypeScript
- **Database**: Supabase (PostgreSQL)
- **Authentication**: JWT, Passport.js (for Google OAuth)
- **Testing**: Jest, Supertest
- **Linting & Formatting**: Biome

---

### 🚀 Getting Started (Backend)

Follow these instructions to get the backend service up and running on your local machine for development and testing purposes.

#### Prerequisites

- [Node.js](https://nodejs.org/) (v18 or later recommended)
- [npm](https://www.npmjs.com/)

#### Installation

1.  **Navigate to the backend directory:**
    ```bash
    cd financial-planner-backend
    ```

2.  **Install dependencies:**
    ```bash
    npm install
    ```

3.  **Set up environment variables:**
    Create a `.env` file in the `financial-planner-backend` directory. You can copy the example below and replace the placeholder values with your actual credentials.

    ```env
    # Supabase Credentials
    SUPABASE_URL=your-supabase-url
    SUPABASE_ANON_KEY=your-supabase-anon-key

    # JWT Configuration
    JWT_SECRET=your-strong-jwt-secret
    JWT_EXPIRES_IN=7d

    # Google OAuth Credentials
    GOOGLE_CLIENT_ID=your-google-client-id
    GOOGLE_CLIENT_SECRET=your-google-client-secret
    GOOGLE_CALLBACK_URL=http://localhost:3000/api/v1/auth/google/callback

    # Application Configuration
    PORT=3000
    APP_URL=http://localhost:3000
    ```

### Running the Backend

-   **Development Mode**: To run the server with hot-reloading using `nodemon`:
    ```bash
    cd financial-planner-backend
    npm run dev
    ```
    The server will be available at `http://localhost:3000`.

-   **Production Mode**: To build and run the production version of the app:
    ```bash
    cd financial-planner-backend
    npm run build
    npm start
    ```

### Running Backend Tests

This project has a comprehensive test suite. To run all unit, integration, and flow tests for the backend, navigate to the backend directory and run:

```bash
cd financial-planner-backend
npm test
```

---

## 📁 Project Structure

```
.
├── financial-planner-backend/
│   ├── src/
│   │   ├── __tests__/
│   │   ├── config/
│   │   ├── controllers/
│   │   ├── middleware/
│   │   ├── routes/
│   │   ├── services/
│   │   └── ...
│   ├── .env
│   ├── package.json
│   └── ...
├── financial-planner-frontend/
├── financial-planner-frontend-jetpack-compose/
└── README.md
```