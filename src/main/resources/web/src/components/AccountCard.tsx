import { Button, Card, Center, Grid, Group, Text, Tooltip } from '@mantine/core';
import { Link } from 'react-router-dom';
import { IconUser } from '@tabler/icons-react';
import { Account } from '../pages/Accounts';
import { getUsername } from '../util/api';

interface Props {
  data: Account
}

export default function AccountCard({ data }: Props) {
  const username = getUsername();

  return (
    <Card p="xs" withBorder>
      <Grid align="center">
        <Grid.Col span={6}>
          <Group justify="center">
            <Text size="lg" fw="bold">{data.username}</Text>
            {data.username === username && (
              <Tooltip label="Your account" withArrow>
                <IconUser />
              </Tooltip>
            )}
          </Group>
        </Grid.Col>
        <Grid.Col span={6}>
          <Center>
            <Button component={Link} to={`/accounts/edit/${data.username}`}>Edit</Button>
          </Center>
        </Grid.Col>
      </Grid>
    </Card>
  );
}
