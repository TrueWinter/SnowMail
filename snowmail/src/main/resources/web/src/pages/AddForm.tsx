import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import FormEditor from '../components/forms/FormEditor';
import Page from '../components/Page';
import { getRole } from '../util/api';
import { noPermissionForPage } from '../util/notifications';

export function Component() {
  const navigate = useNavigate();
  useEffect(() => {
    if (getRole() !== 'ADMIN') {
      noPermissionForPage();
      navigate('/forms');
    }
  }, []);

  return (
    <Page title="Add Form">
      <FormEditor />
    </Page>
  );
}
