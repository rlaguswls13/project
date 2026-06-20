export type Activity = {
  id: number;
  source: 'PR' | 'ISSUE' | 'COMMIT';
  title: string;
  url: string;
  author: string;
  repoName: string;
  createdAt: string;
  rawJson: string;
};

export type Retrospective = {
  id: number;
  period: 'DAILY' | 'WEEKLY';
  dateKey: string;
  summary: string;
  blockers: string;
  nextActions: string;
  confidence: number;
  generationProvider?: string | null;
  generationModel?: string | null;
  generationDetail?: string | null;
  createdAt: string;
};

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? 'http://localhost:8080';

export async function getActivities(): Promise<Activity[]> {
  const response = await fetch(`${API_BASE_URL}/api/activities`, {
    cache: 'no-store'
  });

  if (!response.ok) {
    return [];
  }

  return response.json();
}

export async function getLatestRetrospective(period: 'DAILY' | 'WEEKLY'): Promise<Retrospective | null> {
  const response = await fetch(`${API_BASE_URL}/api/retrospectives/latest?period=${period}`, {
    cache: 'no-store'
  });

  if (response.status === 204 || !response.ok) {
    return null;
  }

  return response.json();
}

export async function syncGithub(): Promise<string> {
  const response = await fetch(`${API_BASE_URL}/api/sync/github`, {
    method: 'POST'
  });

  if (!response.ok) {
    throw new Error('GitHub sync request failed.');
  }

  return response.text();
}

export function formatActivityTime(value: string) {
  return new Intl.DateTimeFormat('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
    timeZone: 'Asia/Seoul'
  }).format(new Date(value));
}

export function countBySource(activities: Activity[], source: Activity['source']) {
  return activities.filter((activity) => activity.source === source).length;
}