import { Text, Title } from '@mantine/core';
import { useEffect } from 'react';
import { type ActionFunctionArgs, useActionData, useNavigate } from 'react-router-dom';
import { notifications } from '@mantine/notifications';
import { HttpResponse, getRole, post } from '../util/api';
import Page from '../components/Page';
import { AccountForm, prepareRequest } from '../components/AccountForm';
import { noPermissionForPage } from '../util/notifications';

export function Component() {
  const navigate = useNavigate();
  const data = useActionData() as HttpResponse;

  useEffect(() => {
    if (getRole() === 'USER') {
      noPermissionForPage();
      navigate('/');
    }
  }, []);

  useEffect(() => {
    if (data && data.status === 200) {
      notifications.show({
        message: 'Account created'
      });
      navigate('/accounts');
    }
  }, [data]);

  return (
    <Page title="Add Account">
      <Title>Add Account</Title>
      {data && data.status !== 200 && <Text c="red">{data.body.title}</Text>}
      <AccountForm />
    </Page>
  );
}

export async function action(args: ActionFunctionArgs) {
  return post('/api/accounts', await prepareRequest(args));
}
