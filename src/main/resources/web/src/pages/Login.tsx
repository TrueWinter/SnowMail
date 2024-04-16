import { Paper, Stack, TextInput, Button, Center, Title, Group, Text } from '@mantine/core';
import { type ActionFunctionArgs, Form, useActionData, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import DarkModeToggle from '../components/DarkModeToggle';
import { HttpResponse, post, redirectAfterLogin } from '../util/api';

export async function action({ request }: ActionFunctionArgs) {
  return post('/api/login', await request.formData(), {
    noRedirectOn401: true
  });
}

export function Component() {
  const navigate = useNavigate();
  const data = useActionData() as HttpResponse;

  useEffect(() => {
    if (localStorage.getItem('token')) {
      navigate('/');
    }

    if (data && data.status === 200) {
      localStorage.setItem('token', (data.body as any).token);
      navigate(redirectAfterLogin());
    }
  }, [data]);

  return (
    <Center h="100vh">
      <Paper p="xl" miw="25vw" withBorder>
        <Form method="POST">
          <Stack>
            <Title>Login</Title>
            {data && data.status !== 200 && <Text c="red">{data.body.title}</Text>}
            <TextInput label="Username" name="username" autoComplete="username" required />
            <TextInput label="Password" name="password" type="password"
              autoComplete="current-password" required />
            <Group>
              <Button type="submit" style={{
                flexGrow: 1
              }}>Login</Button>
              <DarkModeToggle />
            </Group>
          </Stack>
        </Form>
      </Paper>
    </Center>
  );
}
