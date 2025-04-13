import passport from "passport";
import { Strategy as GoogleStrategy } from "passport-google-oauth20";
import { config } from "./config";
import database from "../services/database.service";

passport.use(
	new GoogleStrategy(
		{
			clientID: config.google.clientId,
			clientSecret: config.google.clientSecret,
			callbackURL: config.google.callbackUrl,
			scope: ["profile", "email"],
		},
		async (accessToken, refreshToken, profile, done) => {
            console.log("Google OAuth callback received");
            console.log("Profile ID:", profile.id);
            
			try {
				// Check if user exists
				const existingUser = await database.findUserByGoogleId(profile.id);

				if (existingUser) {
					return done(null, existingUser);
				}

				// Create new user
				const email =
					profile.emails && profile.emails[0] ? profile.emails[0].value : "";
				const name = profile.displayName || "";
				const avatar_url =
					profile.photos && profile.photos[0] ? profile.photos[0].value : "";

				const newUser = await database.createUser({
					email,
					name,
					avatar_url,
					google_id: profile.id,
					role: "user",
				});

				return done(null, newUser);
			} catch (error) {
				return done(error as Error, undefined);
			}
		},
	),
);

export default passport;
