import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { notifications } from '@mantine/notifications';
import { get } from '../util/api';
import FormEditor from '../components/forms/FormEditor';

export function Component() {
  const params = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState({});

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

      setForm(d.body);
    });
  }, []);

  return (
    <FormEditor form={form} />
  );
}
