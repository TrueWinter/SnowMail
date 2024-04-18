import { Fieldset, ScrollArea, Text, TextInput, Title } from '@mantine/core';
import { type UseFormReturnType } from '@mantine/form';
import FormMetadata from './FormMetadata';
import { Form } from '#types/java';

export interface PropertiesProps {
  propertiesForm: UseFormReturnType<{name: string, email: string }>
  form?: Form
}

export default function FormProperties({ propertiesForm, form }: PropertiesProps) {
  return (
    <ScrollArea.Autosize h="100%" scrollbars="y">
      <Title size="h2" visibleFrom="md">Form Properties</Title>
      <Fieldset legend="Required Properties" mb="xs">
        <TextInput label="Name" type="text" required
          description="A name used to identify this form in the dashboard"
          {...propertiesForm.getInputProps('name')} />
        <TextInput label="Email" type="email" required
          description="Submissions will be sent to this email address"
          {...propertiesForm.getInputProps('email')} />

        <Text mt="sm" size="xs" c="dimmed">Important: Ensure you have an email handler plugin
          installed. Emails will not be sent otherwise.</Text>
      </Fieldset>
      <Fieldset legend="Metadata">
        <Text size="xs" c="dimmed" mb="sm">Metadata is for storing data that plugins can access.
          It is not included in the API response when the client-side script on your website
          requests the form information.</Text>
        <FormMetadata form={form} />
      </Fieldset>
    </ScrollArea.Autosize>
  );
}
