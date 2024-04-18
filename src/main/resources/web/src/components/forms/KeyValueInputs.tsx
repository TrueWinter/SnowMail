import { ActionIcon, Group, Stack } from '@mantine/core';
import { v4 as uuid } from 'uuid';
import { IconPlus } from '@tabler/icons-react';
import KeyValueInput, { type KV } from './KeyValueInput';

interface Props {
  current: KV[],
  // eslint-disable-next-line no-unused-vars
  onChange: (kv: KV[]) => void
}

export default function KeyValueInputs({ current, onChange }: Props) {
  function addInput() {
    onChange([
      ...current,
      {
        id: uuid(),
        key: '',
        value: ''
      }
    ]);
  }

  function change(kv: KV) {
    // Deep copy the array because React expects state to be immutable
    const newKV = current.map((o) => ({ ...o }));
    const o = newKV.find((e) => e.id === kv.id);
    o.key = kv.key;
    o.value = kv.value;

    onChange(newKV);
  }

  return (
    <Stack>
      {current.map((e) => <KeyValueInput key={e.id} current={e} onChange={change} />)}
      <Group justify="flex-end">
        <ActionIcon onClick={addInput}>
          <IconPlus />
        </ActionIcon>
      </Group>
    </Stack>
  );
}

export function deserialize(obj: Record<string, string>): KV[] {
  if (!obj) return [];

  return Object.entries(obj).map((e) => ({
    id: uuid(),
    key: e[0],
    value: e[1]
  }));
}

export function serialize(kv: KV[]): Record<string, string> {
  if (!kv) return {};
  return kv.reduce((a, c) => ({ ...a, [c.key]: c.value }), {});
}
