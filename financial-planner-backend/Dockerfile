# --- Stage 1: Build ---
# This stage uses a full Node.js image to install all dependencies and build the TypeScript code.
FROM node:18-alpine AS builder

WORKDIR /usr/src/app

# Set ownership to the node user for better security
RUN chown -R node:node /usr/src/app
USER node

# Copy package files and install all dependencies
COPY --chown=node:node package*.json ./
RUN npm install

# Copy the rest of the source code
COPY --chown=node:node . .

# Build the TypeScript project
RUN npm run build

# --- Stage 2: Production ---
# This stage creates the final, lean production image.
FROM node:18-alpine

WORKDIR /usr/src/app

# Set ownership for the production environment
RUN chown -R node:node /usr/src/app
USER node

# Copy only the necessary package files and install production dependencies
COPY --chown=node:node package*.json ./
RUN npm install --omit=dev

# Copy the built application from the 'builder' stage
COPY --chown=node:node --from=builder /usr/src/app/dist ./dist

# Copy the views templates needed for production
COPY --chown=node:node --from=builder /usr/src/app/src/views ./dist/views

# Expose the port the app runs on
EXPOSE 8999

# The command to run the application
CMD [ "npm", "start" ]
