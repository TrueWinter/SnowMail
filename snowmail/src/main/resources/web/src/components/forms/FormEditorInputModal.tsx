/* eslint-disable react/prop-types */
import { useContext, type ReactNode } from 'react';
import { ActionIcon, Alert, Checkbox, Code, Fieldset, Group, List, NativeSelect, NumberInput,
  Paper, Stack, TagsInput, Text, TextInput, Textarea } from '@mantine/core';
import { IconAlertTriangle, IconPlus } from '@tabler/icons-react';
import { useListState } from '@mantine/hooks';
import { useForm } from '@mantine/form';
import { InputType, type InputTypes } from './InputCard';
import KeyValueInputs from './KeyValueInputs';
import { deserialize } from '../../util/forms';
import { type KV } from './KeyValueInput';
import type { ButtonInputTypes, TextInputTypes, AbstractTextInput, InputUnion,
  StylableInput, Input, TextInput as TextInputType, TextAreaInput, ButtonInput,
  ScriptInput, CustomElementInput } from '#types/java';
import FormEditorInputSorter from './FormEditorInputSorter';
import { openModal } from './FormInputs';
import { CustomInputContext } from './context/CustomInputContext';

interface FormFieldProps<T = InputUnion> {
  input: T
  // eslint-disable-next-line no-unused-vars
  set: (key: string, changes: object) => void
  // eslint-disable-next-line no-unused-vars, react/no-unused-prop-types
  remove?: (key: string) => void
  // set: Dispatch<SetStateAction<Input>>
}
type FormFields = {
  // eslint-disable-next-line no-unused-vars
  [K in InputTypes]: (props: FormFieldProps<InputType<K>>) => ReactNode
}

function StylableInputFields({ input, set }: FormFieldProps<StylableInput>) {
  const form = useForm({
    mode: 'uncontrolled',
    initialValues: {
      unstyled: input.unstyled,
      cssClasses: input.cssClasses
    },
    onValuesChange: (values: Omit<StylableInput, keyof Input | 'cssStyles'>) => {
      set(input.rKey, values);
    }
  });

  function setStyles(kv: KV[]) {
    set(input.rKey, {
      cssStyles: kv
    });
  }

  const styles = Array.isArray(input.cssStyles) ? input.cssStyles : deserialize(input.cssStyles);

  return (
    <Fieldset legend="Styling">
      <Stack>
        <Checkbox label="Unstyled" description="If checked, the default styles will not be applied"
          {...form.getInputProps('unstyled', { type: 'checkbox' })} />
        <Fieldset legend="CSS Styles">
          <KeyValueInputs current={styles} onChange={setStyles} />
        </Fieldset>
        <TagsInput label="CSS Classes" splitChars={[',', ' ']}
          {...form.getInputProps('cssClasses')} />
      </Stack>
    </Fieldset>
  );
}

interface TextInputFieldsProps extends FormFieldProps<AbstractTextInput> {
  children?: ReactNode
}
function TextInputFields({ input, set, children }: TextInputFieldsProps) {
  const form = useForm({
    mode: 'uncontrolled',
    initialValues: {
      required: input.required,
      includedInEmail: input.includedInEmail,
      label: input.label,
      name: input.name,
      placeholder: input.placeholder,
      maxLength: input.maxLength || null,
      ignoredOnClient: input.ignoredOnClient
    },
    validateInputOnChange: true,
    validate: {
      label: (v) => (!v ? 'Label is required' : null),
      name: (v) => (!v ? 'Name is required' : null)
    },
    onValuesChange: (values: Omit<AbstractTextInput, keyof (StylableInput)>) => {
      set(input.rKey, values);
    }
  });

  return (
    <>
      <Group justify="space-evenly">
        <Checkbox label="Required" {...form.getInputProps('required', { type: 'checkbox' })} />
        <Checkbox label="Included In Email"
          {...form.getInputProps('includedInEmail', { type: 'checkbox' })} />
      </Group>
      <TextInput label="Label" description="The label shown in the form, in errors, and in emails"
        required {...form.getInputProps('label')} />
      <TextInput label="Name"
        /*
          Using span here as a paragraph element is used by default which results
          in an error about invalid HTML.
        */
        description={(
          <Text c="dimmed" size="xs" component="span">The name that uniquely identifies this field
            when submitting the form. <Text fw="bold">Special names:</Text>
            <List>
              <List.Item><Code>name</Code>: Used as the sender name</List.Item>
              <List.Item><Code>email</Code>: Used as the Reply-To address</List.Item>
            </List>
          </Text>
        )}
        descriptionProps={{
          component: 'span'
        }}
        required {...form.getInputProps('name')} />
      <TextInput label="Placeholder" {...form.getInputProps('placeholder')} />
      <NumberInput label="Max Length" {...form.getInputProps('maxLength')} />
      {children}
      <Checkbox label="Ignored On Client" description="If checked, the field will not be
        automatically added by SnowMail, but will be validated by the server. This is useful for
        third-party scripts like captchas."
        {...form.getInputProps('ignoredOnClient', { type: 'checkbox' })} />
      <StylableInputFields input={input} set={set} />
    </>
  );
}

function CommonFields({ input, set }: FormFieldProps) {
  function setAttribs(kv: KV[]) {
    set(input.rKey, {
      customAttributes: kv
    });
  }

  const attribs = Array.isArray(input.customAttributes) ? input.customAttributes :
    deserialize(input.customAttributes);

  return (
    <Fieldset legend="Custom HTML Attributes">
      <KeyValueInputs current={attribs} onChange={setAttribs} />
    </Fieldset>
  );
}

const textTypes: TextInputTypes[] = ['text', 'email', 'number', 'password', 'tel', 'url', 'hidden'];
const buttonTypes: ButtonInputTypes[] = ['button', 'submit', 'reset'];
const fields: FormFields = {
  TEXT: ({ input, set }) => {
    const form = useForm({
      mode: 'uncontrolled',
      initialValues: {
        pattern: input.pattern,
        patternError: input.patternError,
        type: input.type
      },
      onValuesChange: (values: Omit<TextInputType, keyof (StylableInput & AbstractTextInput)>) => {
        set(input.rKey, values);
      }
    });

    return (
      <>
        <TextInputFields set={set} input={input}>
          <NativeSelect label="Type" data={textTypes} {...form.getInputProps('type')} />
          <TextInput label="Pattern"
            description="A regular expression that the input value will be validated against"
            placeholder="^.+$" {...form.getInputProps('pattern')} />
          <TextInput label="Pattern Error"
            description="The error message that is shown if the user enters an invalid value"
            placeholder="Value does not match required pattern"
            {...form.getInputProps('patternError')} />
        </TextInputFields>
        <CommonFields input={input} set={set} />
      </>
    );
  },
  TEXTAREA: ({ input, set }) => {
    const form = useForm({
      mode: 'uncontrolled',
      initialValues: {
        // Null is used here so if the value is 0, the field will be blank
        rows: input.rows || null
      },
      onValuesChange: (values: Omit<TextAreaInput, keyof (StylableInput & AbstractTextInput)>) => {
        set(input.rKey, {
          rows: values.rows || null
        });
      }
    });

    return (
      <>
        <TextInputFields input={input} set={set}>
          <NumberInput label="Rows" {...form.getInputProps('rows')} />
        </TextInputFields>
        <CommonFields input={input} set={set} />
      </>
    );
  },
  BUTTON: ({ input, set }) => {
    const form = useForm({
      mode: 'controlled',
      initialValues: {
        text: input.text,
        type: input.type
      },
      validate: {
        text: (v) => (!v ? 'Text is required' : null)
      },
      validateInputOnChange: true,
      onValuesChange: (values: Omit<ButtonInput, keyof StylableInput>) => {
        set(input.rKey, values);
      }
    });

    return (
      <>
        <NativeSelect label="Type" data={buttonTypes} {...form.getInputProps('type')} />
        <TextInput label="Text" {...form.getInputProps('text')} required />
        <StylableInputFields input={input} set={set} />
        <CommonFields input={input} set={set} />
      </>
    );
  },
  SCRIPT: ({ input, set }) => {
    const form = useForm({
      mode: 'uncontrolled',
      initialValues: {
        src: input.src,
        defer: input.defer,
        async: input.async,
        module: input.module
      },
      validate: {
        src: (v) => (!v ? 'URL is required' : null)
      },
      validateInputOnChange: true,
      onValuesChange: (values: Omit<ScriptInput, keyof Input>) => {
        set(input.rKey, values);
      }
    });

    return (
      <>
        <TextInput label="URL" required {...form.getInputProps('src')} />
        <Group justify="space-evenly">
          <Checkbox label="Defer" {...form.getInputProps('defer', { type: 'checkbox' })} />
          <Checkbox label="Async" {...form.getInputProps('async', { type: 'checkbox' })} />
          <Checkbox label="Module" {...form.getInputProps('module', { type: 'checkbox' })} />
        </Group>
        <CommonFields input={input} set={set} />
      </>
    );
  },
  MULTIPLE: ({ input, set, remove }) => {
    const customInputs = useContext(CustomInputContext);
    const [state, h] = useListState<InputUnion>(input.inputs);
    const handlers = {
      ...h,
      reorder: ({ from, to }) => {
        // Copied from Mantine's useListState reorder method
        const cloned = [...state];
        const item = state[from];
        cloned.splice(from, 1);
        cloned.splice(to, 0, item);

        set(input.rKey, {
          inputs: cloned
        });
      }
    };

    if (state !== input.inputs) {
      handlers.setState(input.inputs);
    }

    function add(i: InputUnion) {
      set(input.rKey, {
        inputs: [
          ...input.inputs,
          i
        ]
      });
    }

    function setName(name: string) {
      set(input.rKey, {
        customDisplayName: name
      });
    }

    return (
      <>
        <Alert color="orange" title="You probably shouldn&apos;t use this input."
          icon={<IconAlertTriangle />}>
          Using it to group multiple inputs together in the form editor may make managing forms
          more difficult as it will not be easy to see what inputs are in the form. The Multiple
          input is intended for grouping closely-related fields, for example: a response
          field and a script for a captcha.</Alert>
        <TextInput label="Display Name"
          description="A custom name displayed in the dashboard instead of &quot;n inputs&quot;"
          defaultValue={input.customDisplayName} onChange={(e) => setName(e.target.value)} />
        <Paper withBorder p="xs">
          <FormEditorInputSorter data={[state, handlers]} set={set} remove={remove}
            id={input.rKey} />
          <Group mt="sm" justify="flex-end">
            <ActionIcon onClick={() => openModal((i) => add(i), customInputs)}>
              <IconPlus />
            </ActionIcon>
          </Group>
        </Paper>
        <CommonFields input={input} set={set} />
      </>
    );
  },
  CUSTOM: ({ input, set }) => {
    const form = useForm({
      mode: 'uncontrolled',
      initialValues: {
        type: input.type,
        innerHtml: input.innerHtml
      },
      validate: {
        type: (v) => (!v ? 'Element is required' : null)
      },
      validateInputOnChange: true,
      onValuesChange: (values: Omit<CustomElementInput, keyof Input>) => {
        set(input.rKey, values);
      }
    });

    return (
      <>
        <TextInput label="Element" {...form.getInputProps('type')} required />
        <Textarea label="Inner HTML" {...form.getInputProps('innerHtml')} resize="vertical" />
        <CommonFields input={input} set={set} />
      </>
    );
  }
};

export default function FormEditorInputModal({ input, set, remove }: FormFieldProps<any>) {
  return Object.prototype.hasOwnProperty.call(fields, input.inputType) ? (
    <Stack>
      {fields[input.inputType]({ input, set, remove })}
    </Stack>
  ) :
    <Text c="red">Unable to find fields for type {input.inputType}</Text>;
}
