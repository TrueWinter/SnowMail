import { Card, Text } from '@mantine/core';
import { v4 as uuid } from 'uuid';
import { toTitleCase } from '../../util/text';
import { type InputUnion } from '#types/java';
import { type Input } from './FormEditor';

export const INPUT_TYPES = ['TEXT', 'TEXTAREA', 'BUTTON', 'SCRIPT', 'MULTIPLE', 'CUSTOM'] as const;
export type InputTypes = typeof INPUT_TYPES[number];

interface Props {
  type: InputTypes
  // eslint-disable-next-line no-unused-vars
  onClick: (input: Input) => void;
}

function createInputData(type: InputTypes): Partial<InputUnion> {
  switch (type) {
    case 'TEXT':
      return {
        inputType: type,
        name: 'new-input'
      };
    case 'TEXTAREA':
      return {
        inputType: type,
        name: 'new-input',
      };
    case 'BUTTON':
      return {
        inputType: type,
        text: 'New Button'
      };
    case 'CUSTOM':
      return {
        inputType: type,
        type: 'div'
      };
    case 'MULTIPLE':
      return {
        inputType: type,
        inputs: []
      };
    case 'SCRIPT':
      return {
        inputType: type,
        src: 'https://example.com/script.js'
      };
    default:
      return null;
  }
}

export default function InputCard({ type, onClick }: Props) {
  return (
    <Card withBorder onClick={() => onClick({
      key: uuid(),
      input: createInputData(type) as InputUnion
    })} style={{
      cursor: 'pointer',
      userSelect: 'none'
    }}>
      <Text>{toTitleCase(type)}</Text>
    </Card>
  );
}
