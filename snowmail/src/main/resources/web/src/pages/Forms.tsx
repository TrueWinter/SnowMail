import { Button, Group, Stack, Title } from '@mantine/core';
import { useEffect, useState } from 'react';
import { IconPlus } from '@tabler/icons-react';
import { Link } from 'react-router-dom';
import { get, getRole } from '../util/api';
import ListCardSkeleton from '../components/ListCardSkeleton';
import Page from '../components/Page';
import FormList from '../components/FormList';

export interface FormSummary {
  id: string
  name: string
  email: string
}

export function Component() {
  const [forms, setForms] = useState<FormSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const role = getRole();

  useEffect(() => {
    get('/api/forms').then((data) => {
      setLoading(false);
      setForms(data.body || [] as any);
    });
  }, []);

  return (
    <Page title="Forms">
      <Group justify="space-between" my="sm">
        <Title>Forms</Title>
        {role === 'ADMIN' && (
          <Button component={Link} to="/forms/add">
            <IconPlus />
          </Button>
        )}
      </Group>
      <Stack>
        {loading ? <ListCardSkeleton /> : <FormList forms={forms} />}
      </Stack>
    </Page>
  );
}
