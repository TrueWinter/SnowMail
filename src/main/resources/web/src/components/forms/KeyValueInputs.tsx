import { ActionIcon, Group, Stack } from '@mantine/core';
import { v4 as uuid } from 'uuid';
import { IconPlus } from '@tabler/icons-react';
import KeyValueInput, { type KV } from './KeyValueInput';
import { deepCopyObjArr } from '../../util/data';

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
    const newKV = deepCopyObjArr(current);
    const o = newKV.find((e) => e.id === kv.id);
    o.key = kv.key;
    o.value = kv.value;

    onChange(newKV);
  }

  function del(key: string) {
    onChange(current.filter((e) => e.id !== key));
  }

  return (
    <Stack>
      {current.map((e) => (
        <KeyValueInput key={e.id} current={e} onChange={change} del={() => del(e.id)} />
      ))}
      <Group justify="flex-end">
        <ActionIcon onClick={addInput}>
          <IconPlus />
        </ActionIcon>
      </Group>
    </Stack>
  );
}
