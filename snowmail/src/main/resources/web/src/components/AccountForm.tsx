import { Box, Button, Group, Modal, MultiSelect, NativeSelect, Skeleton,
  Space, Stack, Text, TextInput, Tooltip } from '@mantine/core';
import { useEffect, useState } from 'react';
import { type ActionFunctionArgs, Form, useNavigate } from 'react-router-dom';
import { useForm } from '@mantine/form';
import { useDisclosure } from '@mantine/hooks';
import { nprogress } from '@mantine/nprogress';
import { notifications } from '@mantine/notifications';
import { del, get, getRole, getUsername } from '../util/api';
import { onSubmit } from '../util/forms';
import { type FormSummary } from '../pages/Forms';
import { Account } from '#types/java';

interface Props {
  account?: Account
}

async function deleteAccount(username: string) {
  nprogress.start();
  const resp = await del(`/api/accounts/${username}`);
  nprogress.complete();
  return resp;
}

export function AccountForm({ account }: Props) {
  const [forms, setForms] = useState<FormSummary[]>([]);
  const navigate = useNavigate();
  const [deleteModalOpened, { open: openDeleteModal, close: closeDeleteModal }] = useDisclosure();
  const [deleteModalBusy, setDeleteModalBusy] = useState(false);
  const form = useForm({
    initialValues: {
      username: account?.username || '',
      password: '',
      confirmPassword: '',
      role: account?.role || 'ADMIN',
      forms: account?.forms || []
    },
    validate: {
      confirmPassword: (value, values) => (value !== values.password ?
        'Passwords do not match' : null)
    }
  });

  const formsSelectData = forms.map((e) => ({
    label: `${e.name} - ${e.email}`,
    value: e.id
  }));

  const username = getUsername();
  const role = getRole();

  useEffect(() => {
    get('/api/forms').then((d) => setForms(d.body));
  }, []);

  return (
    <>
      <Form method="POST" onSubmit={(e) => onSubmit(e, form)}>
        <Stack gap="sm">
          <TextInput label="Username" name={account ? undefined : 'username'} required
            disabled={!!account} {...form.getInputProps('username')} />
          <TextInput label="Password" name="password" type="password" required={!account}
            description={account && 'Leave blank to leave password unchanged'}
            {...form.getInputProps('password')} />
          <TextInput label="Confirm Password" name="confirm-password" type="password"
            required={!account} {...form.getInputProps('confirmPassword')} />

          {role === 'ADMIN' && (
            <>
              <NativeSelect label="Role" name="role"
                description="The admin role provides access to all forms, and the ability to
                  create and delete forms. The user role provides users the ability to edit only the
                  selected forms."
                data={[{
                  label: 'Admin',
                  value: 'ADMIN'
                }, {
                  label: 'User',
                  value: 'USER'
                }]} required {...form.getInputProps('role')} />
              {form.values.role !== 'ADMIN' && (
              <MultiSelect label="Forms" name="forms" data={formsSelectData} required
                clearable searchable {...form.getInputProps('forms')} />
              )}
            </>
          )}

          <Box my="sm">
            {account ? (
              <Group gap="sm">
                <Button type="submit">Edit Account</Button>
                {role === 'ADMIN' && (account.username === username ? (
                  <Tooltip label="You cannot delete your own account">
                    <Button type="button" disabled>Delete Account</Button>
                  </Tooltip>
                ) : (
                  <Button type="button" color="red"
                    onClick={openDeleteModal}>Delete Account</Button>
                ))}
              </Group>
            ) : <Button type="submit">Create Account</Button>}
          </Box>
        </Stack>
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
              deleteAccount(account.username).then((d) => {
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
    </>
  );
}

export async function prepareRequest({ request }: ActionFunctionArgs) {
  const formData: Record<string, any> = Object.fromEntries((await request.formData()).entries());
  if (formData.forms && formData.forms.toString().length > 0) {
    formData.forms = formData.forms.toString().split(',');
  } else {
    formData.forms = [];
  }

  return formData;
}
