import { Button, Group, Modal, Text } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { find } from '../FormEditorDraggableInput';
import type { DeleteModalProps } from './ModalProps';

export function DeleteModal({ id, state, onClose, remove }: DeleteModalProps) {
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
