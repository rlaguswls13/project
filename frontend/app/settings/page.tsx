const settings = [
  ['GitHub repo', 'via environment variables'],
  ['GitHub token', 'APP_GITHUB_TOKEN'],
  ['Copilot SDK bridge', 'APP_COPILOT_ENABLED, APP_COPILOT_SDK_URL, APP_COPILOT_MODEL'],
  ['Copilot sidecar', 'COPILOT_GITHUB_TOKEN, COPILOT_CLI_URL (optional)']
];

export default function SettingsPage() {
  return (
    <main className="shell narrowShell">
      <section className="panel standalonePanel">
        <p className="eyebrow">Settings</p>
        <h1>Environment-backed configuration.</h1>
        <div className="settingsList">
          {settings.map(([label, value]) => (
            <div key={label} className="settingRow">
              <span>{label}</span>
              <strong>{value}</strong>
            </div>
          ))}
        </div>
      </section>
    </main>
  );
}