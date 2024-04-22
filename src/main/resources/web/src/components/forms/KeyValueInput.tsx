import { ActionIcon, Flex, Text, TextInput } from '@mantine/core';
import { modals } from '@mantine/modals';
import { IconTrash } from '@tabler/icons-react';
import { useRef } from 'react';

export interface KV {
  id: string
  key: string
  value: string
}

interface Props {
  current: KV
  // eslint-disable-next-line no-unused-vars
  onChange: (kv: KV) => void
  del: () => void
}

export default function KeyValueInput({ current, onChange, del }: Props) {
  const keyRef = useRef<HTMLInputElement>();
  const valueRef = useRef<HTMLInputElement>();

  function onKeyUp() {
    const m = {
      id: current.id,
      key: keyRef.current.value,
      value: valueRef.current.value
    };

    onChange(m);
  }

  function showDeleteModal() {
    modals.openConfirmModal({
      title: 'Delete',
      children: <Text>Are you sure you want to delete this property?</Text>,
      labels: {
        confirm: 'Delete',
        cancel: 'Cancel'
      },
      confirmProps: {
        color: 'red'
      },
      onConfirm: del
    });
  }

  return (
    <Flex direction="row" gap="sm">
      <TextInput placeholder="Key" defaultValue={current.key} ref={keyRef}
        onKeyUp={onKeyUp} required />
      <TextInput placeholder="Value" defaultValue={current.value} ref={valueRef}
        onKeyUp={onKeyUp} required />
      <ActionIcon color="red" size="lg" onClick={showDeleteModal}>
        <IconTrash />
      </ActionIcon>
    </Flex>
  );
}
