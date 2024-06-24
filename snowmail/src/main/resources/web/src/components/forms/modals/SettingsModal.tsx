import { Modal, ScrollArea } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { useContext } from 'react';
import { toTitleCase } from '../../../util/text';
import { find } from '../FormEditorDraggableInput';
import type { SettingsModalProps } from './ModalProps';
import MantineContactForm from '../../../../../../../../../npm/react/MantineContactForm';
import { MetadataContext } from '../context/MetadataContext';
import { serialize } from '../../../util/forms';
import { getNew } from '../KeyValueInputs';

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
      Object.entries(values).forEach(([key, value]) => {
        let found = false;
        newMetadata.forEach((m) => {
          if (m.key === key) {
            // eslint-disable-next-line no-param-reassign
            m.value = value;
            found = true;
          }
        });

        if (!found) {
          const newSetting = getNew();
          newSetting.key = key;
          newSetting.value = value;
          newMetadata.push(newSetting);
        }
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
