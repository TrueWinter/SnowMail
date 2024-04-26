import { Fieldset, ScrollArea, Text, TextInput, Title, Tooltip } from '@mantine/core';
import { type UseFormReturnType } from '@mantine/form';
import FormMetadata, { type FormMetadataProps } from './FormMetadata';

export interface PropertiesProps extends FormMetadataProps {
  propertiesForm: UseFormReturnType<{name: string, email: string }>
}

export default function FormProperties({ propertiesForm, metadata, setMetadata }:
  PropertiesProps) {
  return (
    <ScrollArea.Autosize h="100%" scrollbars="y">
      <Title size="h2" visibleFrom="md">Form Properties</Title>
      <Fieldset legend="Required Properties" mb="xs">
        <TextInput label="Name" type="text" required
          description="A name used to identify this form in the dashboard and the email subject"
          {...propertiesForm.getInputProps('name')} />
        <TextInput label="Email" type="email" required
          description="Submissions will be sent to this email address"
          {...propertiesForm.getInputProps('email')} />
        <Text mt="md" size="xs" c="dimmed">Emails will be sent to <Text fw="bold"
          span>{propertiesForm.values.email}</Text> with the subject <Text fw="bold" span>
            Message sent from {propertiesForm.values.name} website (12345678)</Text>.
          The timestamp, encoded as hexadecimal, will be used instead of <Text fw="bold"
            span>12345678</Text>.
        </Text>
      </Fieldset>
      <Fieldset legend="Metadata">
        <Text size="xs" c="dimmed" mb="sm">Metadata is for storing data that plugins can access.
          It is not included in the API response when the client-side script on your website
          requests the form information. Metadata can be exposed to the client by using
          the <Text fw="bold" span>%metadata:
            <Tooltip label="Replace key with the metadata key">
              <Text fw="bold" td="underline" span>key</Text>
            </Tooltip>%</Text> placeholder.</Text>
        <FormMetadata metadata={metadata} setMetadata={setMetadata} />
      </Fieldset>
    </ScrollArea.Autosize>
  );
}
