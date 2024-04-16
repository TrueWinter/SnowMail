import { useEffect } from 'react';
import { ensureUserIsLoggedIn } from '../util/api';
import FormEditor from '../components/forms/FormEditor';

export function Component() {
  useEffect(() => {
    ensureUserIsLoggedIn();
  }, []);

  return (
    <FormEditor />
  );
}
