import { ActionIcon, Button, Group, Paper } from '@mantine/core';
import { UseListState } from '@mantine/hooks/lib/use-list-state/use-list-state';
import { type UseFormReturnType } from '@mantine/form';
import { IconPlus } from '@tabler/icons-react';
import FormEditorInputSorter from './FormEditorInputSorter';
import { openModal } from './FormInputs';
import { type InputUnion } from '#types/java';

interface Props {
  propertiesForm: UseFormReturnType<{name: string, email: string }>
  data: UseListState<InputUnion>
  // eslint-disable-next-line no-unused-vars
  set: (key: string, changes: object) => void
  // eslint-disable-next-line no-unused-vars
  remove: (key: string) => void
  submit: () => void
}

export default function FormEditorInputs({ data, remove, set, propertiesForm, submit }: Props) {
  const [state, handlers] = data;

  return (
    <Paper withBorder p="xs">
      <FormEditorInputSorter data={[state, handlers]} set={set} remove={remove} id="input-list" />
      <Group justify="flex-end" mt="sm">
        <ActionIcon size="lg" onClick={() => openModal((input) => handlers.append(input))}>
          <IconPlus />
        </ActionIcon>
        <Button disabled={state.length === 0 || !propertiesForm.isValid()}
          onClick={submit}>Save</Button>
      </Group>
    </Paper>
  );
}
