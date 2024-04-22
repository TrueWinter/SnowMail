import { Button, Group, Modal, Skeleton, Space, Text, TextInput,
  Title, Tooltip } from '@mantine/core';
import { useEffect, useState } from 'react';
import { ActionFunctionArgs, Form, useActionData, useNavigate, useParams } from 'react-router-dom';
import { notifications } from '@mantine/notifications';
import { useForm } from '@mantine/form';
import { useDisclosure } from '@mantine/hooks';
import { nprogress } from '@mantine/nprogress';
import { HttpResponse, del, get, getUsername, patch } from '../util/api';
import { onSubmit } from '../util/forms';
import Page from '../components/Page';

async function deleteAccount(username: string) {
  nprogress.start();
  const resp = await del(`/api/accounts/${username}`);
  nprogress.complete();
  return resp;
}

export function Component() {
  const params = useParams();
  const navigate = useNavigate();
  const form = useForm({
    initialValues: {
      password: '',
      confirmPassword: ''
    },
    validate: {
      confirmPassword: (value, values) => (value !== values.password ?
        'Passwords do not match' : null)
    }
  });
  const data = useActionData() as HttpResponse;
  const [deleteModalOpened, { open: openDeleteModal, close: closeDeleteModal }] = useDisclosure();
  const [deleteModalBusy, setDeleteModalBusy] = useState(false);

  useEffect(() => {
    get(`/api/accounts/${params.username}`).then((d) => {
      if (d.status === 404) {
        notifications.show({
          message: `Account with username ${params.username} does not exist`,
          color: 'red'
        });

        navigate('/accounts');
      }
    });
  }, []);

  useEffect(() => {
    if (data && data.status === 200) {
      notifications.show({
        message: 'Password changed'
      });
      navigate('/accounts');
    }
  }, [data]);

  const username = getUsername();

  return (
    <Page title="Edit Account">
      <Title>Edit Account: {params.username}</Title>
      {data && data.status !== 200 && <Text c="red">{data.body.title}</Text>}
      <Form method="POST" onSubmit={(e) => onSubmit(e, form)}>
        <TextInput label="Username" value={params.username} required disabled />
        <TextInput label="Password" name="password" type="password" required
          {...form.getInputProps('password')} />
        <TextInput label="Confirm Password" name="confirm-password" type="password" required
          {...form.getInputProps('confirmPassword')} />
        <Space my="sm" />
        <Group gap="sm">
          <Button type="submit">Change Password</Button>
          {params.username === username ? (
            <Tooltip label="You cannot delete your own account" withArrow>
              <Button type="button" disabled>Delete Account</Button>
            </Tooltip>
          ) : (
            <Button type="button" bg="red"
              onClick={openDeleteModal}>Delete Account</Button>
          )}
        </Group>
      </Form>

      <Modal opened={deleteModalOpened} onClose={closeDeleteModal} title="Delete Account"
        withCloseButton={!deleteModalBusy} closeOnClickOutside={!deleteModalBusy}
        closeOnEscape={!deleteModalBusy}>
        <Skeleton visible={deleteModalBusy}>
          <Text>Are you sure you want to delete this account?</Text>
          <Space my="sm" />
          <Group grow>
            <Button type="button" bg="gray" onClick={closeDeleteModal}>Cancel</Button>
            <Button type="button" bg="red" onClick={() => {
              setDeleteModalBusy(true);
              deleteAccount(params.username).then((d) => {
                notifications.show({
                  color: d.status !== 200 ? 'red' : undefined,
                  message: d.status === 200 ? 'Account deleted' : d.body.title
                });
                navigate('/accounts');
              });
            }}>Delete</Button>
          </Group>
        </Skeleton>
      </Modal>
    </Page>
  );
}

export async function action({ request, params }: ActionFunctionArgs) {
  return patch(`/api/accounts/${params.username}`, await request.formData());
}
