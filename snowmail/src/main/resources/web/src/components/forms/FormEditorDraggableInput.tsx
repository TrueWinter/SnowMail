import type { DraggableProvided, DraggableStateSnapshot } from '@hello-pangea/dnd';
import { ActionIcon, Card, Group } from '@mantine/core';
import { IconEdit, IconGripVertical, IconSettings, IconTrash } from '@tabler/icons-react';
import type { InputUnion } from '#types/java';
import FormEditorInputText from './FormEditorInputText';

export function find(key: string, data: InputUnion[]): InputUnion | null {
  // eslint-disable-next-line no-restricted-syntax
  for (const input of data) {
    if (input.rKey === key) {
      return input;
    }

    if (input.inputType === 'MULTIPLE') {
      const result = find(key, input.inputs);
      if (result) {
        return result;
      }
    }
  }

  return null;
}

interface Props {
  input: InputUnion
  provided: DraggableProvided
  snapshot: DraggableStateSnapshot
  // eslint-disable-next-line no-unused-vars
  openEditModal: (key: string) => void
  // eslint-disable-next-line no-unused-vars
  openDeleteModal: (key: string) => void
  // eslint-disable-next-line no-unused-vars
  openSettingsModal: (key: string) => void
}

export default function FormEditorDraggableInput({ input, provided, snapshot,
  openEditModal, openDeleteModal, openSettingsModal }: Props) {
  return (
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
            <FormEditorInputText input={input} />
          </Group>
          <Group justify="flex-end" style={{
            flexGrow: '1'
          }}>
            {input.settings && input.settings.length !== 0 && (
              <ActionIcon size="lg" onClick={() => openSettingsModal(input.rKey)}>
                <IconSettings />
              </ActionIcon>
            )}
            <ActionIcon size="lg" onClick={() => openEditModal(input.rKey)}>
              <IconEdit />
            </ActionIcon>
            <ActionIcon color="red" size="lg" onClick={() => openDeleteModal(input.rKey)}>
              <IconTrash />
            </ActionIcon>
          </Group>
        </Group>
      </Card>
    </div>
  );
}
