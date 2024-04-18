import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { notifications } from '@mantine/notifications';
import { LoadingOverlay } from '@mantine/core';
import { get } from '../util/api';
import FormEditor from '../components/forms/FormEditor';
import { type Form } from '#types/java';
import Page from '../components/Page';

export function Component() {
  const params = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState<Form>(null);

  useEffect(() => {
    get(`/api/forms/${params.id}`).then((d) => {
      if (d.status === 404) {
        notifications.show({
          message: `Form ${params.id} does not exist`,
          color: 'red'
        });

        navigate('/');
        return;
      }

      setForm(d.body as Form);
    });
  }, []);

  return (
    <Page title="Edit Form">
      <LoadingOverlay visible={!form} />
      <FormEditor key={form?.id} form={form} />
    </Page>
  );
}
