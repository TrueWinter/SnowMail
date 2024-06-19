import { ActionIcon, Button, Card, Center, Grid, Group, Stack, Text, Tooltip } from '@mantine/core';
import { Link, useNavigate } from 'react-router-dom';
import { IconTrash } from '@tabler/icons-react';
import { modals } from '@mantine/modals';
import { notifications } from '@mantine/notifications';
import { type FormSummary } from '../pages/Forms';
import { del, getRole } from '../util/api';

interface Props {
  data: FormSummary
}

export default function FormCard({ data }: Props) {
  const navigate = useNavigate();
  const role = getRole();

  function showDeleteModal() {
    modals.openConfirmModal({
      title: 'Delete',
      children: <Text>Are you sure you want to delete this form?</Text>,
      labels: {
        confirm: 'Delete',
        cancel: 'Cancel'
      },
      confirmProps: {
        color: 'red'
      },
      onConfirm: () => {
        del(`/api/forms/${data.id}`).then((d) => {
          if (d.status === 200) {
            notifications.show({
              message: 'Form deleted'
            });
            navigate('/');
          } else {
            notifications.show({
              message: `Failed to delete form. Error code: ${d.status}`,
              color: 'red'
            });
          }
        });
      }
    });
  }

  return (
    <Card p="xs" withBorder>
      <Grid align="center">
        <Grid.Col span={{ sm: 12, md: 4 }}>
          <Center>
            <Text size="lg" fw="bold">{data.id}</Text>
          </Center>
        </Grid.Col>
        <Grid.Col span={{ sm: 12, md: 4 }}>
          <Stack gap="xs">
            <Text>Name: {data.name}</Text>
            <Tooltip label={data.email}>
              <Text truncate="end">Email: {data.email}</Text>
            </Tooltip>
          </Stack>
        </Grid.Col>
        <Grid.Col span={{ sm: 12, md: 4 }}>
          <Group justify="center">
            <Button component={Link} to={`/forms/edit/${data.id}`}>Edit</Button>
            {role === 'ADMIN' && (
              <ActionIcon size="lg" color="red" onClick={showDeleteModal}>
                <IconTrash />
              </ActionIcon>
            )}
          </Group>
        </Grid.Col>
      </Grid>
    </Card>
  );
}
