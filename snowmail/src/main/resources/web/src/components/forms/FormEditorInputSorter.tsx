import { Stack, Text } from '@mantine/core';
import { DragDropContext, Draggable, DraggableChildrenFn, Droppable } from '@hello-pangea/dnd';
import { UseListState } from '@mantine/hooks/lib/use-list-state/use-list-state';
import { useState } from 'react';
import type { InputUnion } from '#types/java';
import FormEditorDraggableInput, { DeleteModal, EditModal } from './FormEditorDraggableInput';

interface Props {
  id: string
  data: UseListState<InputUnion>
  // eslint-disable-next-line no-unused-vars
  set: (key: string, changes: object) => void
  // eslint-disable-next-line no-unused-vars
  remove: (key: string) => void
}

export default function FormEditorInputSorter({ id, data, set, remove }: Props) {
  const [state, handlers] = data;
  const [editModalKey, setEditModalKey] = useState<string>(null);
  const [deleteModalKey, setDeleteModalKey] = useState<string>(null);

  const getRenderItem = (items: InputUnion[]):
  // eslint-disable-next-line react/function-component-definition, react/no-unstable-nested-components
    DraggableChildrenFn => (provided, snapshot, rubric) => {
    const item = items[rubric.source.index];

    return (
      <FormEditorDraggableInput input={item} provided={provided} snapshot={snapshot}
        openEditModal={(key) => setEditModalKey(key)}
        openDeleteModal={(key) => setDeleteModalKey(key)} />
    );
  };

  const items = getRenderItem(state);

  return (
    <>
      <EditModal id={editModalKey} state={state}
        onClose={() => setEditModalKey(null)} set={set} remove={remove} />
      <DeleteModal id={deleteModalKey} state={state}
        onClose={() => setDeleteModalKey(null)} remove={remove} />
      <DragDropContext onDragEnd={({ destination, source }) =>
        // eslint-disable-next-line implicit-arrow-linebreak
        handlers.reorder({ from: source.index, to: destination?.index })}>
        <Droppable droppableId={id} direction="vertical" renderClone={items}>
          {(provided) => (
            <div {...provided.droppableProps} ref={provided.innerRef}>
              <Stack gap="sm">
                {state.length !== 0 ?
                  state.map((e, i) => (
                    <Draggable key={e.rKey} index={i} draggableId={e.rKey}>
                      {items}
                    </Draggable>
                  )) :
                  <Text size="lg">No inputs added yet.</Text>}
                {provided.placeholder}
              </Stack>
            </div>
          )}
        </Droppable>
      </DragDropContext>
    </>
  );
}
