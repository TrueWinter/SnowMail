import { TextInput as MantineTextInput, Text, Textarea, Button as MantineButton,
  Group } from '@mantine/core';
import { useEffect } from 'react';

function reactifyStyleName(str) {
  return str.replace(/-./g, (x) => x[1].toUpperCase());
}

function reactifyStyles(/** @type {Record<string, string>} */ obj) {
  const styles = {};

  // eslint-disable-next-line no-restricted-syntax
  for (const key of Object.keys(obj)) {
    styles[reactifyStyleName(key)] = obj[key];
  }

  return styles;
}

function reactifyAttribs(/** @type {Record<string, string} */ obj) {
  const output = {
    ...obj
  };

  if (Object.prototype.hasOwnProperty.call(output, 'class')) {
    output.className = output.class;
    delete output.class;
  }

  if (Object.prototype.hasOwnProperty.call(output, 'style')) {
    const tmpStyle = {};
    output.style.split(';').filter((e) => e).forEach((e) => {
      const parts = e.split(':');
      tmpStyle[reactifyStyleName(parts[0].trim())] = parts.slice(1).join(':').trim();
    });
    output.style = tmpStyle;
  }

  return output;
}

/** @param {import('../types/public').InputUnion} input */
function createCommonProps(input) {
  return {
    ...(reactifyAttribs(input.customAttributes || {}))
  };
}

/** @param {import('../types/public').StylableInput} input */
function createStylableInputProps(input) {
  return {
    unstyled: input.unstyled || false,
    className: input.cssClasses ? input.cssClasses.join(' ') : undefined,
    style: input.cssStyles ? reactifyStyles(input.cssStyles) : undefined,
    ...createCommonProps(input)
  };
}

/** @param {import('../types/public').AbstractTextInput} input */
function createAbstractTextInputProps(input) {
  return {
    label: input.label || undefined,
    description: input.description,
    required: input.required || undefined,
    name: input.name || undefined,
    maxLength: input.maxLength || undefined,
    placeholder: input.placeholder || undefined,
    autoComplete: 'off',
    ...createStylableInputProps(input)
  };
}

/** @type {import('../types/inputs').Component<'TEXT'>} */
function TextInput({ input, form }) {
  return (
    <MantineTextInput {...createAbstractTextInputProps(input)} type={input.type}
      pattern={input.pattern || null} {...form.getInputProps(input.name)}
      key={form.key(input.name)} />
  );
}

/** @type {import('../types/inputs').Component<'TEXTAREA'>} */
function TextAreaInput({ input, form }) {
  return (
    // Null is used here to use the browser's default if the value is 0
    <Textarea {...createAbstractTextInputProps(input)} rows={input.rows}
      {...form.getInputProps(input.name)} key={form.key(input.name)} />
  );
}

/** @type {import('../types/inputs').Component<'BUTTON'>} */
function Button({ input }) {
  return (
    /*
      Group is used here to prevent the button from stretching but still allow
      the user to make it stretch if they choose to.
    */
    <Group>
      <MantineButton {...createStylableInputProps(input)}
        type={input.type}>{input.text}</MantineButton>
    </Group>
  );
}

/** @type {import('../types/inputs').Component<'SCRIPT'>} */
function Script({ input, children }) {
  useEffect(() => {
    const script = document.createElement('script');

    if (input.src) {
      script.src = input.src;
      script.async = input.async;
      script.defer = input.defer;
    } else if (children) {
      script.innerHTML = children;
    }

    if (input.module) {
      script.type = 'module';
    }

    const attribs = createCommonProps(input);
    // eslint-disable-next-line no-restricted-syntax
    for (const key in attribs) {
      if (Object.prototype.hasOwnProperty.call(attribs, key)) {
        script.setAttribute(key, attribs[key]);
      }
    }

    document.body.appendChild(script);
  }, []);

  return null;
}

/** @type {import('../types/inputs').Component<'MULTIPLE'>} */
function Multiple({ input, form }) {
  return (
    <div {...createCommonProps(input)}>
      {/* eslint-disable-next-line no-use-before-define */}
      {input.inputs.map((i) => <FormInput key={i.rKey} input={i} form={form} />)}
    </div>
  );
}

/** @type {import('../types/inputs').Component<'CUSTOM'>} */
function Custom({ input }) {
  const CustomTag = input.type;

  // eslint-disable-next-line react/no-danger
  return input.type === 'script' ? <Script input={{}}>{input.innerHtml}</Script> : (
    <CustomTag dangerouslySetInnerHTML={{ __html: input.innerHtml || '' }}
      {...createCommonProps(input)} />
  );
}

/**
 * @param {Object} param0
 * @param {import('../types/public').InputUnion} param0.input
 * @param {import('@mantine/form').UseFormReturnType} param0.form
 * @returns {import('react').ReactNode}
 */
export default function FormInput({ input, form }) {
  if (input.ignoredOnClient) return null;

  switch (input.inputType) {
    case 'TEXT':
      return <TextInput input={input} form={form} />;
    case 'TEXTAREA':
      return <TextAreaInput input={input} form={form} />;
    case 'BUTTON':
      return <Button input={input} />;
    case 'MULTIPLE':
      return <Multiple input={input} form={form} />;
    case 'CUSTOM':
      return <Custom input={input} />;
    case 'SCRIPT':
      return <Script input={input} />;
    default:
      return <Text c="red">Unknown input type {input.inputType}</Text>;
  }
}
