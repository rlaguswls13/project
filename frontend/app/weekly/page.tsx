import { getLatestRetrospective } from '../../lib/api';

export default async function WeeklyPage() {
  const weeklyRetrospective = await getLatestRetrospective('WEEKLY');
  const items = weeklyRetrospective
    ? [weeklyRetrospective.summary, weeklyRetrospective.blockers, weeklyRetrospective.nextActions]
    : ['No weekly retrospective returned from the backend yet.'];

  return (
    <main className="shell narrowShell">
      <section className="panel standalonePanel">
        <p className="eyebrow">Weekly Retrospective</p>
        <h1>{weeklyRetrospective?.dateKey ?? 'Waiting for weekly data.'}</h1>
        <ul className="bulletList">
          {items.map((item) => (
            <li key={item}>{item}</li>
          ))}
        </ul>
      </section>
    </main>
  );
}