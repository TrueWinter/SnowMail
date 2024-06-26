import { useEffect, useRef, useState } from 'react';
import { Alert, Box, Loader, LoadingOverlay, Stack } from '@mantine/core';
import { useForm } from '@mantine/form';
import { getForm } from '../api';
import FormInput from './FormInput';
import createForm from './createForm';
import * as FormRegistry from './FormRegistry';

/** @param {import('./MantineContactForm').MantineContactFormProps} props */
export default function MantineContactForm(props) {
  /** @type {[import('../types/public').InputUnion[], import('react').Dispatch<import('react').SetStateAction<import('../types/public').InputUnion[]>]} */
  const [formState, setFormState] = useState(props.inputs || []);
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [success, setSuccess] = useState(false);
  const form = useForm(createForm(formState, props.onChange, props.defaults));
  /** @type {import('react').MutableRefObject<HTMLFormElement>} */
  const formRef = useRef(null);

  useEffect(() => {
    if (!props.inputs) {
      getForm(props.url, props.id).then(setFormState).catch((e) => {
        setError(`An error occurred, please try again later. ${e}`);
      });
    }
  }, []);

  function scrollToFormTop() {
    if (formRef.current) {
      formRef.current.scrollIntoView({
        behavior: 'smooth'
      });
    }
  }

  function checkRegisteredInputs() {
    if (!FormRegistry.validateAll()) {
      scrollToFormTop();
      return false;
    }

    return true;
  }

  function handleSubmit(values) {
    setSuccess(false);
    setError(null);

    if (checkRegisteredInputs()) {
      setSubmitting(true);

      const submitFunction = props.handler ? props.handler(values) :
        fetch(`${props.url}/public-api/forms/${props.id}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json'
          },
          body: JSON.stringify({
            ...values,
            ...FormRegistry.getAll()
          })
        });

      submitFunction.then(async (resp) => {
        if (resp.success || resp.status === 200) {
          form.reset();
          FormRegistry.resetAll();
          setSuccess(true);
        } else {
          const err = props.handler ? resp.error : (await resp.json()).title;
          setError(err || 'An error occurred, please try again later');
        }
      }).catch((e) => {
        setError(`An error occurred, please try again later. ${e}`);
      }).finally(() => {
        setSubmitting(false);
        if (props.afterSubmit) {
          props.afterSubmit(() => {
            scrollToFormTop();
          });
        } else {
          scrollToFormTop();
        }
      });
    }
  }

  // eslint-disable-next-line no-nested-ternary
  return formState.length === 0 ? (error ? <Alert color="red">{error}</Alert> : <Loader />) : (
    <form onSubmit={form.onSubmit(handleSubmit, () => {
      setSuccess(false);
      checkRegisteredInputs();
      scrollToFormTop();
    })} onReset={form.onReset}
      ref={formRef} noValidate>
      <Box pos="relative" p="xs">
        <LoadingOverlay visible={submitting} overlayProps={{
          radius: 'sm',
          blur: 2
        }} />
        <Stack>
          {success && <Alert color="green">Email sent!</Alert>}
          {error && <Alert color="red">{error}</Alert>}
          {formState.map((input) => <FormInput key={input.rKey} input={input} form={form} />)}
        </Stack>
      </Box>
    </form>
  );
}
