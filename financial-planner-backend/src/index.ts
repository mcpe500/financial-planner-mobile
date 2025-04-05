import app from "./app";
import { config } from "./config/config";

// Start the server
const server = app.listen(config.server.port, () => {
	console.log(`Server is running on port ${config.server.port}`);
});

// Handle unhandled promise rejections
process.on("unhandledRejection", (err: Error) => {
	console.log("UNHANDLED REJECTION! Shutting down...");
	console.error(err.name, err.message);
	server.close(() => {
		process.exit(1);
	});
});

export default server;
