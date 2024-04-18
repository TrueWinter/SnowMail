import { ActionIcon, Button, Group, Paper } from '@mantine/core';
import { UseListState } from '@mantine/hooks/lib/use-list-state/use-list-state';
import { useViewportSize } from '@mantine/hooks';
import { notifications } from '@mantine/notifications';
import { nprogress } from '@mantine/nprogress';
import { useState } from 'react';
import { type UseFormReturnType } from '@mantine/form';
import { type Input } from './FormEditor';
import FormEditorInputSorter from './FormEditorInputSorter';
import { IconPlus } from '@tabler/icons-react';
import { openModal } from './FormInputs';

interface Props {
  propertiesForm: UseFormReturnType<{name: string, email: string }>
  data: UseListState<Input>
  // eslint-disable-next-line no-unused-vars
  remove: (key: string) => void
}

export default function FormEditorInputs({ data, remove, propertiesForm }: Props) {
  const [state, handlers] = data;
  const { width } = useViewportSize();
  const [submitting, setSubmitting] = useState(false);

  function handleSubmit() {
    if (propertiesForm.validate().hasErrors) {
      if (width <= 576) {
        notifications.show({
          message: 'Unable to save form. Invalid data detected in form properties.',
          color: 'red'
        });
      }

      return;
    }

    // TODO: Submit
    setSubmitting(true);
    nprogress.start();

    setTimeout(() => {
      nprogress.complete();
      setSubmitting(false);
      notifications.show({
        message: 'Form saved'
      });
    }, 3000);
  }

  return (
    <Paper withBorder p="xs">
      <FormEditorInputSorter data={[state, handlers]} remove={remove} />
      <Group justify="flex-end" mt="sm">
        <ActionIcon size="lg" onClick={() => openModal((input) => handlers.append(input))}>
          <IconPlus />
        </ActionIcon>
        <Button disabled={state.length === 0} onClick={handleSubmit}
          loading={submitting}>Save</Button>
      </Group>
    </Paper>
  );
}
