import { UseFormReturnType } from '@mantine/form';
import { FormEvent } from 'react';
import { v4 as uuid } from 'uuid';
import { type KV } from '../components/forms/KeyValueInput';
import type { InputUnion } from '#types/java';

export function onSubmit(e: FormEvent<HTMLFormElement>, form: UseFormReturnType<any, any>) {
  if (form.validate().hasErrors) {
    e.preventDefault();
  }
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

export function serializeInputs(inputs: InputUnion[]): InputUnion[] {
  const serializeProperties = ['customAttributes', 'cssStyles'];

  // eslint-disable-next-line no-restricted-syntax
  for (const input of inputs) {
    // eslint-disable-next-line no-restricted-syntax
    for (const property of serializeProperties) {
      if (input[property] && Array.isArray(input[property])) {
        input[property] = serialize(input[property]);
      }
    }

    if (input.inputType === 'MULTIPLE') {
      serializeInputs(input.inputs);
    }
  }

  return inputs;
}
