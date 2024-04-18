import { Text } from '@mantine/core';
import { useState } from 'react';
import { v4 as uuid } from 'uuid';
import { type Form } from '#types/java';
import KeyValueInputs from './KeyValueInputs';
import { type KV } from './KeyValueInput';

interface Props {
  form?: Form
}

export default function FormMetadata({ form }: Props) {
  const [metadata, setMetadata] = useState<KV[]>([]);

  if (form && Object.keys(form.metadata).length > 0) {
    const m: KV[] = Object.entries(form.metadata).map((e) => ({
      id: uuid(),
      key: e[0],
      value: e[1]
    }));

    if (Object.entries(metadata).every(([key, value]) => m[key] === value)) {
      setMetadata(m);
    }
  }

  function change(m: KV[]) {
    setMetadata(m);
  }

  return metadata ?
    <KeyValueInputs current={metadata} onChange={change} /> :
    <Text>No metadata set</Text>;
}
