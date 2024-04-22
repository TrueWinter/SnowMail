import { Card, Text } from '@mantine/core';
import { v4 as uuid } from 'uuid';
import { toTitleCase } from '../../util/text';
import type * as Types from '#types/java';

export const INPUT_TYPES = ['TEXT', 'TEXTAREA', 'BUTTON', 'SCRIPT', 'MULTIPLE', 'CUSTOM'] as const;
export type InputTypes = typeof INPUT_TYPES[number];
export type InputType<T extends InputTypes> =
  T extends 'TEXT' ? Types.TextInput :
  T extends 'TEXTAREA' ? Types.TextAreaInput :
  T extends 'BUTTON' ? Types.ButtonInput :
  T extends 'SCRIPT' ? Types.ScriptInput :
  T extends 'MULTIPLE' ? Types.MultipleInputs :
  T extends 'CUSTOM' ? Types.CustomElementInput :
  never;

interface Props {
  type: InputTypes
  // eslint-disable-next-line no-unused-vars
  onClick: (input: Types.InputUnion) => void;
}

function createInputData(type: InputTypes): Partial<Types.InputUnion> {
  if (type === 'MULTIPLE') {
    return {
      rKey: uuid(),
      inputType: type,
      inputs: []
    };
  }

  return {
    rKey: uuid(),
    inputType: type
  };
}

export default function InputCard({ type, onClick }: Props) {
  return (
    <Card withBorder onClick={() => onClick(createInputData(type) as Types.InputUnion)} style={{
      cursor: 'pointer',
      userSelect: 'none'
    }}>
      <Text>{toTitleCase(type)}</Text>
    </Card>
  );
}
