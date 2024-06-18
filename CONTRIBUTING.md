# Contributing

SnowMail welcomes useful contributions, no matter how small or large.

## Development Environment

You'll need the following installed before you can work on the code:

- Java 17
- Node.js 18+
- Docker Desktop
- IDE and code editor (IntelliJ recommended for Java, VS Code recommended for all other files)

## Developing

Run `npm install` in the root directory and in the `npm` directory to install all the JavaScript dependencies. Your IDE should offer to install the Java dependencies when you open the project.

Create a `.env` file with the following content:
```bash
APP_SECRET=longrandomsecret
# Change this to your email
EMAIL_FROM=web-contact@example.com
# Change this to your domain
MAILNAME=example.com
MONGO_DB=mongodb://127.0.0.1:27125
SMTP_HOST=127.0.0.1
SMTP_PORT=2525
```

To start the SMTP and database servers, run `docker compose -f docker-compose-dev.yml -d`. This will expose the SMTP server on port 2525 and Mongo on 27125.

Start SnowMail in your IDE and then run `npm run dev` to generate TypeScript types and build development JavaScript bundles. Then access SnowMail at http://localhost:8026 (yes, 8026; Webpack will proxy requests to port 8025 automatically).

## Pull Requests

It is recommended to create a local branch for each pull request. Limit pull requests to one feature; if you'd like to add multiple features, create multiple pull requests.
