import { ScrollArea, Stack, Text, Title } from '@mantine/core';
import { modals } from '@mantine/modals';
import { notifications } from '@mantine/notifications';
import InputCard, { INPUT_TYPES } from './InputCard';
import { type InputUnion } from '#types/java';
import { toTitleCase } from '../../util/text';

// eslint-disable-next-line no-unused-vars
type AddFunction = (input: InputUnion) => void
interface Props {
  add: AddFunction
}

export default function FormInputs({ add }: Props) {
  function addInput(input: InputUnion) {
    add(input);
    notifications.show({
      message: `Added ${toTitleCase(input.inputType)} input`
    });
  }

  return (
    <>
      <Title size="h2" visibleFrom="md">Form Inputs</Title>
      <Text size="sm" c="dimmed" mb="sm">Click on an input to add it to the form</Text>

      <Stack gap="sm">
        {INPUT_TYPES.map((e) => <InputCard key={e} type={e} onClick={addInput} />)}
      </Stack>
    </>
  );
}

export function openModal(add: AddFunction) {
  modals.open({
    title: 'Inputs',
    scrollAreaComponent: ScrollArea.Autosize,
    children: <FormInputs add={add} />
  });
}
