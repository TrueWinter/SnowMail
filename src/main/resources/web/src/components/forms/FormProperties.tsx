import { Fieldset, ScrollArea, Text, TextInput, Title } from '@mantine/core';
import { type UseFormReturnType } from '@mantine/form';

export interface PropertiesProps {
  form: UseFormReturnType<{name: string, email: string }>
}

export default function FormProperties({ form }: PropertiesProps) {
  return (
    <ScrollArea.Autosize h="100%" scrollbars="y">
      <Title size="h2" visibleFrom="md">Form Properties</Title>
      <Fieldset legend="Configurable Properties" mb="xs">
        <TextInput label="Name" type="text" required
          description="A name used to identify this form in the dashboard"
          {...form.getInputProps('name')} />
        <TextInput label="Email" type="email" required
          description="Submissions will be sent to this email address"
          {...form.getInputProps('email')} />

        <Text mt="sm" size="xs" c="dimmed">Important: Ensure you have an email handler plugin
          installed. Emails will not be sent otherwise.</Text>
      </Fieldset>
      <Fieldset legend="Metadata">
        <Text size="xs" c="dimmed" mb="xs">Metadata is added and managed by plugins. It is not
          possible to modify or delete metadata yourself.</Text>
        {new Array(25).fill(null).map((e, i) => <Text key={i}>key{i}: value{i}</Text>)}
      </Fieldset>
    </ScrollArea.Autosize>
  );
}
