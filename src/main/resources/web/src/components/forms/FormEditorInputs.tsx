import { ActionIcon, Button, Card, Group, Paper, Stack, Text } from '@mantine/core';
import { DragDropContext, Draggable, Droppable } from '@hello-pangea/dnd';
import { IconEdit, IconGripVertical, IconTrash } from '@tabler/icons-react';
import { UseListState } from '@mantine/hooks/lib/use-list-state/use-list-state';
import { useViewportSize } from '@mantine/hooks';
import { notifications } from '@mantine/notifications';
import { nprogress } from '@mantine/nprogress';
import { useState } from 'react';
import { modals } from '@mantine/modals';
import { Input } from './FormEditor';
import { type PropertiesProps } from './FormProperties';

interface Props extends PropertiesProps {
  data: UseListState<Input>
  // eslint-disable-next-line no-unused-vars
  remove: (key: string) => void
}

export default function FormEditorInputs({ data, remove, form }: Props) {
  const [state, handlers] = data;
  const { width } = useViewportSize();
  const [submitting, setSubmitting] = useState(false);

  function handleSubmit() {
    if (form.validate().hasErrors) {
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

  const openDeleteModal = (key: string) => modals.openConfirmModal({
    title: 'Delete',
    children: <Text>Are you sure you want to delete this input?</Text>,
    labels: {
      cancel: 'Cancel',
      confirm: 'Delete'
    },
    confirmProps: {
      color: 'red'
    },
    onConfirm: () => remove(key)
  });

  const items = state.map((e, i) => (
    <Draggable key={e.key} index={i} draggableId={e.key}>
      {(provided, snapshot) => (
        <div ref={provided.innerRef} {...provided.draggableProps}>
          <Card withBorder style={{
            borderColor: snapshot.isDragging ? 'var(--mantine-color-blue-9)' : undefined
          }}>
            <Group justify="space-between">
              <Group {...provided.dragHandleProps} style={{
                // Mantine added other styles when setting the grow prop
                flexGrow: '1'
              }}>
                <IconGripVertical />
                <Text size="lg">{e.input}: {e.name}</Text>
              </Group>
              <Group>
                <ActionIcon size="lg">
                  <IconEdit />
                </ActionIcon>
                <ActionIcon color="red" size="lg" onClick={() => openDeleteModal(e.key)}>
                  <IconTrash />
                </ActionIcon>
              </Group>
            </Group>
          </Card>
        </div>
      )}
    </Draggable>
  ));

  return (
    <Paper withBorder p="xs">
      <DragDropContext onDragEnd={({ destination, source }) =>
        // eslint-disable-next-line implicit-arrow-linebreak
        handlers.reorder({ from: source.index, to: destination?.index })}>
        <Droppable droppableId="input-list" direction="vertical">
          {(provided) => (
            <div {...provided.droppableProps} ref={provided.innerRef}>
              <Stack gap="sm">
                {items.length !== 0 ? items : (
                  <Text size="lg">No inputs added. To get started,
                    select an input from the left.</Text>
                )}
                {provided.placeholder}
              </Stack>
            </div>
          )}
        </Droppable>
      </DragDropContext>
      <Group justify="flex-end" mt="sm">
        <Button disabled={state.length === 0} onClick={handleSubmit}
          loading={submitting}>Save</Button>
      </Group>
    </Paper>
  );
}
