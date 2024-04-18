import { ScrollArea, Stack, Text, Title } from '@mantine/core';
import { modals } from '@mantine/modals';
import InputCard, { INPUT_TYPES } from './InputCard';
import { type Input } from './FormEditor';

// eslint-disable-next-line no-unused-vars
type AddFunction = (input: Input) => void
interface Props {
  add: AddFunction
}

export default function FormInputs({ add }: Props) {
  return (
    <>
      <Title size="h2" visibleFrom="md">Form Inputs</Title>
      <Text size="sm" c="dimmed" mb="sm">Click on an input to add it to the form</Text>

      <Stack gap="sm">
        {INPUT_TYPES.map((e) => <InputCard key={e} type={e} onClick={add} />)}
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
