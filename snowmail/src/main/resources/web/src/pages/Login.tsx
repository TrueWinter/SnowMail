import { Paper, Stack, TextInput, Button, Center, Title, Group, Text, Image } from '@mantine/core';
import { type ActionFunctionArgs, Form, useActionData, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import DarkModeToggle from '../components/DarkModeToggle';
import { HttpResponse, post, redirectAfterLogin } from '../util/api';
import Page from '../components/Page';
import logo from '../snowmail-icon.png';

export async function action({ request }: ActionFunctionArgs) {
  return post('/api/login', await request.formData(), {
    noRedirectOn401: true
  });
}

export function Component() {
  const navigate = useNavigate();
  const data = useActionData() as HttpResponse;
  const ssoConfig = JSON.parse(document.getElementById('app').dataset.sso) || {};

  useEffect(() => {
    if (localStorage.getItem('token')) {
      navigate('/');
    } else if (ssoConfig.forceRedirect) {
      location.href = '/login/sso';
    }

    if (data && data.status === 200) {
      localStorage.setItem('token', (data.body as any).token);
      navigate(redirectAfterLogin());
    }
  }, [data]);

  return (
    <Page>
      {!ssoConfig.forceRedirect && (
        <Center h="100vh">
          <Paper p="xl" miw="25vw" withBorder>
            <Form method="POST">
              <Stack>
                <Group justify="space-between">
                  <Title>Login</Title>
                  <Image src={logo} w={64} h={64} />
                </Group>
                {data && data.status !== 200 && <Text c="red">{data.body.title}</Text>}
                <TextInput label="Username" name="username" autoComplete="username" required />
                <TextInput label="Password" name="password" type="password"
                  autoComplete="current-password" required />
                <Group>
                  <Group grow flex="1" preventGrowOverflow={false}>
                    <Button type="submit">Login</Button>
                    {ssoConfig.enabled &&
                    <Button color="teal" component="a" href="/login/sso">Login with SSO</Button>}
                  </Group>
                  <DarkModeToggle />
                </Group>
              </Stack>
            </Form>
          </Paper>
          </Center>
      )}
    </Page>
  );
}
