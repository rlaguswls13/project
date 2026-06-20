'use client';

import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { generateRetrospective } from '../lib/api';

type GenerateState = 'idle' | 'generating' | 'success' | 'error';

type GenerateRetrospectiveButtonProps = {
  period: 'DAILY' | 'WEEKLY';
};

export function GenerateRetrospectiveButton({ period }: GenerateRetrospectiveButtonProps) {
  const router = useRouter();
  const [state, setState] = useState<GenerateState>('idle');
  const [message, setMessage] = useState('');

  async function handleGenerate() {
    setState('generating');
    setMessage('');

    try {
      const result = await generateRetrospective(period);
      setState('success');
      setMessage(`Generated ${result.dateKey} via ${result.generationProvider ?? 'UNKNOWN'}`);
      router.refresh();
    } catch (error) {
      setState('error');
      setMessage(error instanceof Error ? error.message : 'Retrospective generation failed.');
    }
  }

  const label = period === 'WEEKLY' ? 'Generate weekly' : 'Generate today';

  return (
    <div className="syncControl">
      <button className="syncButton" type="button" onClick={handleGenerate} disabled={state === 'generating'}>
        {state === 'generating' ? 'Generating...' : label}
      </button>
      {message ? <span className={state === 'error' ? 'syncStatus syncError' : 'syncStatus'}>{message}</span> : null}
    </div>
  );
}
