import { Flex, TextInput } from '@mantine/core';
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
}

export default function KeyValueInput({ current, onChange }: Props) {
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

  return (
    <Flex direction="row" gap="sm">
      <TextInput placeholder="Key" defaultValue={current.key} ref={keyRef}
        onKeyUp={onKeyUp} required />
      <TextInput placeholder="Value" defaultValue={current.value} ref={valueRef}
        onKeyUp={onKeyUp} required />
    </Flex>
  );
}
