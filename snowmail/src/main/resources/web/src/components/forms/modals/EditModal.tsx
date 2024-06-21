import { Modal, ScrollArea } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import FormEditorInputModal from '../FormEditorInputModal';
import { toTitleCase } from '../../../util/text';
import { find } from '../FormEditorDraggableInput';
import type { EditModalProps } from './ModalProps';

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
