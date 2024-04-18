import { Text } from '@mantine/core';
import { Input } from './FormEditor';
import { toTitleCase } from '../../util/text';

interface Props {
  input: Input
}

export default function FormEditorInputText({ input }: Props) {
  if (!input) return null;
  const type = toTitleCase(input.input.inputType);
  let name: string;

  if (input.input.customDisplayName) {
    name = input.input.customDisplayName;
  } else {
    switch (input.input.inputType) {
      case 'TEXT':
        name = input.input.name;
        break;
      case 'TEXTAREA':
        name = input.input.name;
        break;
      case 'BUTTON':
        name = input.input.text;
        break;
      case 'CUSTOM':
        name = input.input.type;
        break;
      case 'MULTIPLE':
        name = `${input.input.inputs.length} inputs`;
        break;
      case 'SCRIPT':
        name = input.input.src;
        break;
      default:
        name = '<unknown>';
    }
  }

  return <Text>{type}: {name}</Text>;
}
