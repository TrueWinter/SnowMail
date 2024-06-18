import { Button, Group, Stack, Title } from '@mantine/core';
import { IconPlus } from '@tabler/icons-react';
import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { get, getRole } from '../util/api';
import AccountCard from '../components/AccountCard';
import ListCardSkeleton from '../components/ListCardSkeleton';
import Page from '../components/Page';
import { noPermissionForPage } from '../util/notifications';

export interface Account {
  username: string
}

export function Component() {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    if (getRole() === 'USER') {
      noPermissionForPage();
      navigate('/');
      return;
    }

    get('/api/accounts').then((data) => {
      setLoading(false);
      setAccounts(data.body || [] as any);
    });
  }, []);

  return (
    <Page title="Accounts">
      <Group justify="space-between" my="sm">
        <Title>Accounts</Title>
        <Button component={Link} to="/accounts/add">
          <IconPlus />
        </Button>
      </Group>
      <Stack>
        {loading ? <ListCardSkeleton /> :
          accounts.map((account) => <AccountCard key={account.username} data={account} />)}
      </Stack>
    </Page>
  );
}
