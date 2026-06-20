const settings = [
  ['GitHub repo', 'via environment variables'],
  ['GitHub token', 'APP_GITHUB_TOKEN'],
  ['Copilot SDK', 'APP_COPILOT_* variables']
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