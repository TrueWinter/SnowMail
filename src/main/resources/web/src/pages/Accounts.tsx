import { Button, Group, Stack, Title } from '@mantine/core';
import { IconPlus } from '@tabler/icons-react';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { get } from '../util/api';
import AccountCard from '../components/AccountCard';
import ListCardSkeleton from '../components/ListCardSkeleton';

export interface Account {
  username: string
}

export function Component() {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    get('/api/accounts').then((data) => {
      setLoading(false);
      setAccounts(data.body || [] as any);
    });
  }, []);

  return (
    <>
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
    </>
  );
}
