import express from "express";
import { CopilotClient, RuntimeConnection, approveAll } from "@github/copilot-sdk";
import path from "node:path";

const port = Number(process.env.PORT || 3001);
const defaultModel = process.env.COPILOT_DEFAULT_MODEL || "auto";

const clientOptions = {};
if (process.env.COPILOT_CLI_URL) {
  clientOptions.connection = RuntimeConnection.forUri(process.env.COPILOT_CLI_URL);
} else {
  const cliPath = process.platform === "win32"
    ? path.resolve("node_modules", "@github", "copilot-win32-x64", "copilot.exe")
    : path.resolve("node_modules", ".bin", "copilot");
  clientOptions.connection = RuntimeConnection.forStdio({ path: cliPath });
}
if (process.env.COPILOT_MODE) {
  clientOptions.mode = process.env.COPILOT_MODE;
}

const client = new CopilotClient(clientOptions);
const app = express();
app.use(express.json());

app.get("/health", async (_req, res) => {
  try {
    await client.getStatus();
    res.json({ status: "ok" });
  } catch (error) {
    res.status(503).json({ status: "unavailable", message: String(error) });
  }
});

app.post("/api/summarize", async (req, res) => {
  const prompt = typeof req.body?.prompt === "string" ? req.body.prompt.trim() : "";
  const requestedModel = typeof req.body?.model === "string" ? req.body.model.trim() : "";

  if (!prompt) {
    res.status(400).json({ message: "prompt is required" });
    return;
  }

  const sessionConfig = {
    model: requestedModel || defaultModel,
    onPermissionRequest: approveAll
  };

  if (process.env.COPILOT_GITHUB_TOKEN) {
    sessionConfig.gitHubToken = process.env.COPILOT_GITHUB_TOKEN;
  }

  let session;
  try {
    session = await client.createSession(sessionConfig);
    const response = await session.sendAndWait({ prompt });
    const content = response?.data?.content;

    if (!content || typeof content !== "string") {
      throw new Error("Copilot SDK returned empty content");
    }

    res.json({ content });
  } catch (error) {
    res.status(502).json({ message: "Copilot SDK request failed", detail: String(error) });
  } finally {
    if (session) {
      await session.disconnect().catch(() => undefined);
    }
  }
});

async function bootstrap() {
  await client.start();
  app.listen(port, () => {
    console.log(`Copilot SDK service listening on :${port}`);
  });
}

bootstrap().catch((error) => {
  console.error("Failed to start Copilot SDK service", error);
  process.exit(1);
});

async function shutdown(signal) {
  console.log(`Received ${signal}, stopping Copilot SDK client...`);
  await client.stop().catch(() => undefined);
  process.exit(0);
}

process.on("SIGINT", () => {
  void shutdown("SIGINT");
});
process.on("SIGTERM", () => {
  void shutdown("SIGTERM");
});

process.on("unhandledRejection", (reason) => {
  console.error("Unhandled rejection in Copilot SDK service", reason);
});
