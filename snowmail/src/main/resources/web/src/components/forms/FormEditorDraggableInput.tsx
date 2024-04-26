import type { DraggableProvided, DraggableStateSnapshot } from '@hello-pangea/dnd';
import { ActionIcon, Button, Card, Group, Modal, ScrollArea, Text } from '@mantine/core';
import { IconEdit, IconGripVertical, IconTrash } from '@tabler/icons-react';
import { useDisclosure } from '@mantine/hooks';
import type { InputUnion } from '#types/java';
import FormEditorInputModal from './FormEditorInputModal';
import { toTitleCase } from '../../util/text';
import FormEditorInputText from './FormEditorInputText';

interface ModalProps {
  id: string
  state: InputUnion[]
  onClose: () => void;
  // eslint-disable-next-line no-unused-vars
  remove: (key: string) => void
}

function find(key: string, data: InputUnion[]) {
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

interface EditModalProps extends ModalProps {
  // eslint-disable-next-line no-unused-vars
  set: (key: string, changes: object) => void
}

export function EditModal({ id, state, set, onClose, remove }: EditModalProps) {
  const [opened, { open, close }] = useDisclosure(false);
  const input = find(id, state);
  if (input && !opened) {
    open();
  }

  return (
    <Modal title={`Edit ${toTitleCase(input?.inputType)} Input`} opened={opened} onClose={() => {
      onClose();
      close();
    }} scrollAreaComponent={ScrollArea.Autosize} styles={{
      // The Multiple input's InputSorter causes overflowing on the content element
      content: {
        overflow: 'hidden'
      }
    }}>
      {input && <FormEditorInputModal input={input} set={set} remove={remove} />}
    </Modal>
  );
}

export function DeleteModal({ id, state, onClose, remove }: ModalProps) {
  const [opened, { open, close }] = useDisclosure(false);
  const input = find(id, state);
  if (input && !opened) {
    open();
  }

  return (
    <Modal title="Delete" opened={opened} onClose={() => {
      onClose();
      close();
    }} centered>
      <Text>Are you sure you want to delete this input?</Text>
      <Group>
        <Button onClick={close}>Cancel</Button>
        <Button onClick={() => {
          remove(input.rKey);
          onClose();
          close();
        }} color="red">Remove</Button>
      </Group>
    </Modal>
  );
}

interface Props {
  input: InputUnion
  provided: DraggableProvided
  snapshot: DraggableStateSnapshot
  // eslint-disable-next-line no-unused-vars
  openEditModal: (key: string) => void
  // eslint-disable-next-line no-unused-vars
  openDeleteModal: (key: string) => void
}

export default function FormEditorDraggableInput({ input, provided, snapshot,
  openEditModal, openDeleteModal }: Props) {
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
