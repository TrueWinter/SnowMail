/**
 * @param {import('../types/public').InputUnion[]} inputs
 * @param {(values: Record<string, any>) => void} onChange
 * @returns {import('@mantine/form').UseFormInput}
 */
export default function createForm(inputs, onChange = () => {}, defaults = {}) {
  /** @type {import('@mantine/form').UseFormInput} */
  const form = {
    mode: 'uncontrolled',
    initialValues: {},
    validate: {},
    validateInputOnChange: true,
    onValuesChange: onChange
  };

  inputs.forEach((input) => {
    if (['TEXT', 'TEXTAREA'].includes(input.inputType) && !input.ignoredOnClient) {
      form.initialValues[input.name] = defaults[input.name] || '';

      form.validate[input.name] = (/** @type {string} */ v) => {
        if (input.required && !v) {
          return `${input.label} is required`;
        }

        if (!v) return null;

        if (input.maxLength && v.length > input.maxLength) {
          return `Value is over the maximum length of ${input.maxLength}`;
        }

        if (input.inputType === 'TEXT' && input.pattern && !v.match(input.pattern)) {
          return input.patternError || 'Value does not match required pattern';
        }

        return null;
      };
    }
  });

  return form;
}
