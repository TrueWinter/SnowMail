import { Button, Card, Center, Grid, Stack, Text } from '@mantine/core';
import { Link } from 'react-router-dom';
import { type FormSummary } from '../pages/Forms';

interface Props {
  data: FormSummary
}

export default function FormCard({ data }: Props) {
  return (
    <Card p="xs" withBorder>
      <Grid align="center">
        <Grid.Col span={4}>
          <Center>
            <Text size="lg" fw="bold">{data.id}</Text>
          </Center>
        </Grid.Col>
        <Grid.Col span={4}>
          <Stack gap="xs">
            <Text>Name: {data.name}</Text>
            <Text>Email: {data.email}</Text>
          </Stack>
        </Grid.Col>
        <Grid.Col span={4}>
          <Center>
            <Button component={Link} to={`/forms/edit/${data.id}`}>Edit</Button>
          </Center>
        </Grid.Col>
      </Grid>
    </Card>
  );
}
