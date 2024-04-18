/* eslint-disable react/prop-types */
import { Dispatch, ReactNode, SetStateAction, useState } from 'react';
import { Button, Checkbox, Fieldset, Group, Stack, Text } from '@mantine/core';
import { v4 as uuid } from 'uuid';
import { type InputTypes } from './InputCard';
import { type Input } from './FormEditor';
import KeyValueInputs, { deserialize, serialize } from './KeyValueInputs';
import { type KV } from './KeyValueInput';

interface FormFieldProps {
  input: Input
  // set: Dispatch<SetStateAction<Input>>
}
// eslint-disable-next-line no-unused-vars
type FormFields = Record<InputTypes, (props: FormFieldProps) => ReactNode>

function CommonFields({ input }: FormFieldProps) {
  const [attribs, setAttribs] = useState<KV[]>(deserialize(input.input.customAttributes));

  return (
    <>
      <Checkbox label="Ignored On Client" description="If checked, the field will not be
        automatically added by SnowMail, but will be validated by the server. This is useful for
        third-party scripts like captchas." defaultChecked={input.input.ignoredOnClient || false} />
      <Fieldset legend="Custom HTML Attributes">
        <KeyValueInputs current={attribs} onChange={setAttribs} />
      </Fieldset>
    </>
  );
}

const fields: FormFields = {
  TEXT: ({ input }) => (
    <CommonFields input={input} />
  )
};

interface Props {
  input: Input
}

export default function FormEditorInputModal({ input }: Props) {
  return Object.prototype.hasOwnProperty.call(fields, input.input.inputType) ? (
    <Stack>
      {fields[input.input.inputType]({ input })}
      <Group justify="flex-end">
        <Button type="button">Done</Button>
      </Group>
    </Stack>
  ) :
    <Text c="red">Unable to find fields for type {input.input.inputType}</Text>;
}
