import { Button, Group, Stack, Title } from '@mantine/core';
import { useEffect, useState } from 'react';
import { IconPlus } from '@tabler/icons-react';
import { Link } from 'react-router-dom';
import { get } from '../util/api';
import FormCard from '../components/FormCard';
import ListCardSkeleton from '../components/ListCardSkeleton';

export interface FormSummary {
  id: string
  name: string
  email: string
}

export function Component() {
  const [forms, setForms] = useState<FormSummary[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    get('/api/forms').then((data) => {
      setLoading(false);
      setForms(data.body || [] as any);
    });
  }, []);

  return (
    <>
      <Group justify="space-between" my="sm">
        <Title>Forms</Title>
        <Button component={Link} to="/forms/add">
          <IconPlus />
        </Button>
      </Group>
      <Stack>
        {loading ? <ListCardSkeleton /> :
          forms.map((form) => <FormCard key={form.id} data={form} />)}
      </Stack>
    </>
  );
}
