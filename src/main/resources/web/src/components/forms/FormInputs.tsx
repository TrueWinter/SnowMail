import { ScrollArea, Stack, Text, Title } from '@mantine/core';
import InputCard from './InputCard';

interface Props {
  // eslint-disable-next-line no-unused-vars
  add: (input: string, name: string) => void;
}

export default function FormInputs({ add }: Props) {
  return (
    <ScrollArea.Autosize h="100%" scrollbars="y">
      <Title size="h2" visibleFrom="md">Form Inputs</Title>
      <Text size="sm" c="dimmed" mb="sm">Click on an input to add it to the form</Text>

      <Stack gap="sm">
        {new Array(15).fill(null).map((e, i) => (
          <InputCard key={i} name={`Input ${i.toString()}`}
            onClick={() => add('Input', i.toString())} />
        ))}
      </Stack>
    </ScrollArea.Autosize>
  );
}
