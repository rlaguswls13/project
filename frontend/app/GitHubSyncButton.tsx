'use client';

import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { syncGithub } from '../lib/api';

type SyncState = 'idle' | 'syncing' | 'success' | 'error';

export function GitHubSyncButton() {
  const router = useRouter();
  const [state, setState] = useState<SyncState>('idle');
  const [message, setMessage] = useState('');

  async function handleSync() {
    setState('syncing');
    setMessage('');

    try {
      const result = await syncGithub();
      setState('success');
      setMessage(result);
      router.refresh();
    } catch (error) {
      setState('error');
      setMessage(error instanceof Error ? error.message : 'GitHub sync failed.');
    }
  }

  return (
    <div className="syncControl">
      <button className="syncButton" type="button" onClick={handleSync} disabled={state === 'syncing'}>
        {state === 'syncing' ? 'Syncing GitHub...' : 'Sync GitHub'}
      </button>
      {message ? <span className={state === 'error' ? 'syncStatus syncError' : 'syncStatus'}>{message}</span> : null}
    </div>
  );
}
