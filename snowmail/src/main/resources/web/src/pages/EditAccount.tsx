import { Loader, Text, Title } from '@mantine/core';
import { useEffect, useState } from 'react';
import { ActionFunctionArgs, useActionData, useNavigate, useParams } from 'react-router-dom';
import { notifications } from '@mantine/notifications';
import { HttpResponse, get, put } from '../util/api';
import Page from '../components/Page';
import { AccountForm, prepareRequest } from '../components/AccountForm';
import { Account } from '#types/java';

export function Component() {
  const params = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState<Account>(null);
  const data = useActionData() as HttpResponse;

  useEffect(() => {
    get(`/api/accounts/${params.username}`).then((d) => {
      switch (d.status) {
        case 403:
          notifications.show({
            message: 'You do not have permission to view that account',
            color: 'red'
          });

          navigate('/accounts');
          break;
        case 404:
          notifications.show({
            message: `Account with username ${params.username} does not exist`,
            color: 'red'
          });

          navigate('/accounts');
          break;
        default:
          setUser(d.body);
      }
    });
  }, [params]);

  useEffect(() => {
    if (data && data.status === 200) {
      notifications.show({
        message: 'Account edited'
      });
    }
  }, [data]);

  return (
    <Page title="Edit Account">
      <Title>Edit Account: {params.username}</Title>
      {data && data.status !== 200 && <Text c="red">{data.body.title}</Text>}
      {user ? <AccountForm account={user} key={user.username} /> : <Loader />}
    </Page>
  );
}

export async function action(args: ActionFunctionArgs) {
  return put(`/api/accounts/${args.params.username}`, await prepareRequest(args));
}
