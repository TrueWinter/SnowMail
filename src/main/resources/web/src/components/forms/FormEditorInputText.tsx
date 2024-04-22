import { Text } from '@mantine/core';
import { toTitleCase } from '../../util/text';
import { type InputUnion } from '#types/java';

interface Props {
  input: InputUnion
}

export default function FormEditorInputText({ input }: Props) {
  if (!input) return null;
  const type = toTitleCase(input.inputType);
  let name: string;

  if (input.customDisplayName) {
    name = input.customDisplayName;
  } else {
    switch (input.inputType) {
      case 'TEXT':
        name = input.label;
        break;
      case 'TEXTAREA':
        name = input.label;
        break;
      case 'BUTTON':
        name = input.text;
        break;
      case 'CUSTOM':
        name = input.type;
        break;
      case 'MULTIPLE':
        name = `${input.inputs.length} inputs`;
        break;
      case 'SCRIPT':
        name = input.src;
        break;
      default:
        name = '<unknown>';
    }
  }

  if (!name) {
    name = 'New Input';
  }

  return <Text>{type}: {name}</Text>;
}
