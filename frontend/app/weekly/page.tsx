import { getLatestRetrospective } from '../../lib/api';
import { GenerateRetrospectiveButton } from '../GenerateRetrospectiveButton';

export default async function WeeklyPage() {
  const weeklyRetrospective = await getLatestRetrospective('WEEKLY');
  const generationProvider = weeklyRetrospective?.generationProvider ?? 'UNKNOWN';
  const isCopilotSdk = generationProvider === 'COPILOT_SDK';

  return (
    <main className="shell narrowShell">
      <section className="panel accentPanel standalonePanel">
        <div className="panelHeader">
          <div>
            <p className="eyebrow">Weekly Retrospective</p>
            <h1>{weeklyRetrospective?.dateKey ?? 'No weekly record yet'}</h1>
          </div>
          <div className="panelActions">
            <a href="/">Daily view</a>
          </div>
        </div>

        <div className={isCopilotSdk ? 'statusBadge sdkBadge' : 'statusBadge fallbackBadge'}>
          {isCopilotSdk ? 'Copilot SDK generated' : 'Rule-based fallback'}
        </div>

        <p>
          {weeklyRetrospective?.summary ??
            'No weekly retrospective returned from the backend yet. Generate one to populate this panel.'}
        </p>

        <GenerateRetrospectiveButton period="WEEKLY" />

        <div className="miniGrid">
          <div>
            <span>Blockers</span>
            <strong>{weeklyRetrospective ? weeklyRetrospective.blockers : 'None'}</strong>
          </div>
          <div>
            <span>Next actions</span>
            <strong>{weeklyRetrospective ? weeklyRetrospective.nextActions : 'None'}</strong>
          </div>
          <div>
            <span>Confidence</span>
            <strong>{weeklyRetrospective ? weeklyRetrospective.confidence.toFixed(2) : '0.00'}</strong>
          </div>
        </div>

        <div className="sdkDetailPanel">
          <div>
            <span>Generation path</span>
            <strong>{generationProvider}</strong>
          </div>
          <div>
            <span>Model or engine</span>
            <strong>{weeklyRetrospective?.generationModel ?? 'not recorded'}</strong>
          </div>
          <p>{weeklyRetrospective?.generationDetail ?? 'Generate a new weekly retrospective to record Copilot SDK metadata.'}</p>
        </div>
      </section>
    </main>
  );
}