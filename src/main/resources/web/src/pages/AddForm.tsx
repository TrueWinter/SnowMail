import { useEffect } from 'react';
import { ensureUserIsLoggedIn } from '../util/api';
import FormEditor from '../components/forms/FormEditor';
import Page from '../components/Page';

export function Component() {
  useEffect(() => {
    ensureUserIsLoggedIn();
  }, []);

  return (
    <Page title="Add Form">
      <FormEditor />
    </Page>
  );
}
