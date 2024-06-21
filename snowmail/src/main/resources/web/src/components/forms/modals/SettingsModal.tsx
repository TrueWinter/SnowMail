import { Modal, ScrollArea } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { useContext } from 'react';
import { toTitleCase } from '../../../util/text';
import { find } from '../FormEditorDraggableInput';
import type { SettingsModalProps } from './ModalProps';
import MantineContactForm from '../../../../../../../../../npm/react/MantineContactForm';
import { MetadataContext } from '../context/MetadataContext';
import { serialize } from '../../../util/forms';

export default function SettingsModal({ id, state, onClose }: SettingsModalProps) {
  const { metadata, setMetadata } = useContext(MetadataContext);
  const [opened, { open, close }] = useDisclosure(false);
  const input = find(id, state);
  if (input && !opened) {
    open();
  }

  const modalTitle = input?.customDisplayName || toTitleCase(input?.inputType);

  function updateMetadata(values: Record<string, any>) {
    setMetadata((mdata) => {
      const newMetadata = [...mdata];
      newMetadata.forEach((m) => {
        Object.entries(values).forEach(([key, value]) => {
          if (m.key === key) {
            // eslint-disable-next-line no-param-reassign
            m.value = value;
          }
        });
      });

      return newMetadata;
    });
  }

  return (
    <Modal title={`Settings for ${modalTitle}`} opened={opened} onClose={() => {
      onClose();
      close();
    }} scrollAreaComponent={ScrollArea.Autosize}>
      {input && (
        <MantineContactForm url={null} id={null} inputs={input?.settings}
          defaults={serialize(metadata)}
          handler={async () => ({
            success: false
          })} onChange={updateMetadata} />
      )}
    </Modal>
  );
}
