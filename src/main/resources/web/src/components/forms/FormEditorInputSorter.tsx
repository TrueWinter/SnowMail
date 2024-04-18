import { ActionIcon, Card, Group, ScrollArea, Stack, Text } from '@mantine/core';
import { DragDropContext, Draggable, Droppable } from '@hello-pangea/dnd';
import { IconEdit, IconGripVertical, IconTrash } from '@tabler/icons-react';
import { UseListState } from '@mantine/hooks/lib/use-list-state/use-list-state';
import { modals } from '@mantine/modals';
import { type Input } from './FormEditor';
import FormEditorInputText from './FormEditorInputText';
import FormEditorInputModal from './FormEditorInputModal';

interface Props {
  data: UseListState<Input>
  // eslint-disable-next-line no-unused-vars
  remove: (key: string) => void
}

export default function FormEditorInputSorter({ data, remove }: Props) {
  const [state, handlers] = data;

  const openEditModal = (input: Input) => modals.open({
    title: 'Edit Input',
    scrollAreaComponent: ScrollArea.Autosize,
    children: (
      <FormEditorInputModal input={input} />
    )
  });

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
            <Group justify="space-between" style={{
              userSelect: 'none'
            }}>
              <Group {...provided.dragHandleProps} style={{
                // Mantine added other styles when setting the grow prop
                flexGrow: '1'
              }}>
                <IconGripVertical />
                <FormEditorInputText input={e} />
              </Group>
              <Group>
                <ActionIcon size="lg" onClick={() => openEditModal(e)}>
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
    <DragDropContext onDragEnd={({ destination, source }) =>
      // eslint-disable-next-line implicit-arrow-linebreak
      handlers.reorder({ from: source.index, to: destination?.index })}>
      <Droppable droppableId="input-list" direction="vertical">
        {(provided) => (
          <div {...provided.droppableProps} ref={provided.innerRef}>
            <Stack gap="sm">
              {items.length !== 0 ? items : <Text size="lg">No inputs added yet.</Text>}
              {provided.placeholder}
            </Stack>
          </div>
        )}
      </Droppable>
    </DragDropContext>
  );
}
