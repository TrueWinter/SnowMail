import { Button, Space, Text, TextInput, Title } from '@mantine/core';
import { useEffect } from 'react';
import { type ActionFunctionArgs, Form, useActionData, useNavigate } from 'react-router-dom';
import { useForm } from '@mantine/form';
import { notifications } from '@mantine/notifications';
import { HttpResponse, ensureUserIsLoggedIn, post } from '../util/api';
import { onSubmit } from '../util/forms';

export function Component() {
  const navigate = useNavigate();
  const form = useForm({
    initialValues: {
      username: '',
      password: '',
      confirmPassword: ''
    },
    validate: {
      confirmPassword: (value, values) => (value !== values.password ?
        'Passwords do not match' : null)
    }
  });
  const data = useActionData() as HttpResponse;

  useEffect(() => {
    ensureUserIsLoggedIn();
  }, []);

  useEffect(() => {
    if (data && data.status === 200) {
      notifications.show({
        message: `Account with username ${form.values.username} created`
      });
      navigate('/accounts');
    }
  }, [data]);

  return (
    <>
      <Title>Add Account</Title>
      {data && data.status !== 200 && <Text c="red">{data.body.title}</Text>}
      <Form method="POST" onSubmit={(e) => onSubmit(e, form)}>
        <TextInput label="Username" name="username" required {...form.getInputProps('username')} />
        <TextInput label="Password" name="password" type="password" required
          {...form.getInputProps('password')} />
        <TextInput label="Confirm Password" name="confirm-password" type="password" required
          {...form.getInputProps('confirmPassword')} />
        <Space my="sm" />
        <Button type="submit">Create Account</Button>
      </Form>
    </>
  );
}

export async function action({ request }: ActionFunctionArgs) {
  return post('/api/accounts', await request.formData());
}
