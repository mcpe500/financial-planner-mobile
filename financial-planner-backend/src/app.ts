import express, { Application, Request, Response, NextFunction } from "express";
import cors from "cors";
import helmet from "helmet";
import morgan from "morgan";
import passport from "passport";
import routes from "./routes";
import "./config/passport";
import path from "path";
import { engine } from 'express-handlebars';

// Initialize express app
const app: Application = express();

app.engine(
	'hbs',
	engine({
		extname: '.hbs',
		defaultLayout: 'main',
		layoutsDir: path.join(__dirname, 'views', 'layouts'),
		partialsDir: path.join(__dirname, 'views', 'partials'),
		helpers: {
			json: function (context: any) {
				return JSON.stringify(context);
			},
		}
	})
);
app.set('view engine', 'hbs');
app.set('views', path.join(__dirname, 'views'));

// Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cors());
app.use(helmet());
app.use(morgan("dev"));
app.use(passport.initialize());

// Routes
app.use(routes);

// 404 handler
app.use((req: Request, res: Response) => {
	res.status(404).json({ message: "Route not found" });
});

// Error handler
app.use((err: Error, req: Request, res: Response, next: NextFunction) => {
	console.error(err.stack);
	res.status(500).json({
		message: "Internal Server Error",
		error: process.env.NODE_ENV === "development" ? err.message : undefined,
	});
});

export default app;
