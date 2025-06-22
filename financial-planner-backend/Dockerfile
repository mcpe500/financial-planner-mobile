# --- Stage 1: Build ---
# This stage uses the Bun image to install all dependencies and build the source code.
FROM oven/bun:1-alpine AS builder

WORKDIR /usr/src/app

# Copy package files and install all dependencies
COPY package*.json ./
COPY bun.lockb ./
RUN bun install

# Copy the rest of the source code
COPY . .

# Build the project
RUN bun run build

# --- Stage 2: Production ---
# This stage creates the final, lean production image.
FROM oven/bun:1-alpine

WORKDIR /usr/src/app

# Copy only the necessary package files and install production dependencies
COPY package*.json ./
COPY bun.lockb ./
RUN bun install --production

# Copy the built application from the 'builder' stage
COPY --from=builder /usr/src/app/dist ./dist

# Bun serves on port 3000 by default, expose it
EXPOSE 3000

# The command to run the application
CMD [ "bun", "start" ]
