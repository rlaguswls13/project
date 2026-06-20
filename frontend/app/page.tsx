import { countBySource, formatActivityTime, getActivities, getLatestRetrospective } from '../lib/api';

export default async function HomePage() {
  const [activities, dailyRetrospective] = await Promise.all([
    getActivities(),
    getLatestRetrospective('DAILY')
  ]);
  const generationProvider = dailyRetrospective?.generationProvider ?? 'UNKNOWN';
  const isCopilotSdk = generationProvider === 'COPILOT_SDK';

  return (
    <main className="shell">
      <section className="hero">
        <div>
          <p className="eyebrow">Daily Retrospective</p>
          <h1>Activity intelligence for GitHub work.</h1>
          <p className="heroCopy">
            The dashboard is rendered from the running Spring Boot backend, including collected activity and the latest retrospective record.
          </p>
        </div>
        <div className="statsCard">
          <span>Today</span>
          <strong>{activities.length} activities</strong>
          <small>
            {countBySource(activities, 'PR')} PRs, {countBySource(activities, 'ISSUE')} issues, {countBySource(activities, 'COMMIT')} commits
          </small>
        </div>
      </section>

      <section className="contentGrid">
        <article className="panel">
          <div className="panelHeader">
            <h2>Today&apos;s activity</h2>
            <a href="/weekly">Weekly view</a>
          </div>
          <div className="activityList">
            {activities.length > 0 ? (
              activities.map((activity) => (
                <a key={activity.id} href={activity.url} className="activityRow" target="_blank" rel="noreferrer">
                  <div>
                    <p className="activityTitle">{activity.title}</p>
                    <p className="activityMeta">
                      {activity.source} · {activity.author} · {activity.repoName}
                    </p>
                  </div>
                  <time>{formatActivityTime(activity.createdAt)}</time>
                </a>
              ))
            ) : (
              <div className="activityRow">
                <div>
                  <p className="activityTitle">No backend activity returned yet.</p>
                  <p className="activityMeta">Run GitHub sync or keep sample seeding enabled.</p>
                </div>
              </div>
            )}
          </div>
        </article>

        <aside className="panel accentPanel">
          <p className="eyebrow">Latest daily retrospective</p>
          <div className={isCopilotSdk ? 'statusBadge sdkBadge' : 'statusBadge fallbackBadge'}>
            {isCopilotSdk ? 'Copilot SDK generated' : 'Rule-based fallback'}
          </div>
          <h2>{dailyRetrospective?.dateKey ?? 'No daily record yet'}</h2>
          <p>{dailyRetrospective?.summary ?? 'Generate a daily retrospective from the backend API to populate this panel.'}</p>
          <div className="miniGrid">
            <div>
              <span>Blockers</span>
              <strong>{dailyRetrospective ? dailyRetrospective.blockers : 'None'}</strong>
            </div>
            <div>
              <span>Confidence</span>
              <strong>{dailyRetrospective ? dailyRetrospective.confidence.toFixed(2) : '0.00'}</strong>
            </div>
          </div>
          <div className="sdkDetailPanel">
            <div>
              <span>Generation path</span>
              <strong>{generationProvider}</strong>
            </div>
            <div>
              <span>Model or engine</span>
              <strong>{dailyRetrospective?.generationModel ?? 'not recorded'}</strong>
            </div>
            <p>{dailyRetrospective?.generationDetail ?? 'Generate a new retrospective to record Copilot SDK metadata.'}</p>
          </div>
        </aside>
      </section>
    </main>
  );
}