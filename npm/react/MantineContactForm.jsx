import { useEffect, useRef, useState } from 'react';
import { Alert, Box, Loader, LoadingOverlay, Stack } from '@mantine/core';
import { useForm } from '@mantine/form';
import { getForm } from '../api';
import FormInput from './FormInput';
import createForm from './createForm';
import * as FormRegistry from './FormRegistry';

/** @param {import('./ContactForm').ContactFormProps} props */
export default function MantineContactForm(props) {
  /** @type {[import('../types/public').InputUnion[], import('react').Dispatch<import('react').SetStateAction<import('../types/public').InputUnion[]>]} */
  const [formState, setFormState] = useState(props.inputs || []);
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [success, setSuccess] = useState(false);
  const form = useForm(createForm(formState));
  /** @type {import('react').MutableRefObject<HTMLFormElement>} */
  const formRef = useRef(null);

  useEffect(() => {
    if (!props.inputs) {
      getForm(props.url, props.id).then(setFormState).catch((e) => {
        setError(`An error occurred, please try again later. ${e}`);
      });
    }
  }, []);

  // console.log(formState, form, error);

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
      }).then(async (resp) => {
        if (resp.status === 200) {
          form.reset();
          FormRegistry.resetAll();
          setSuccess(true);
        } else {
          const body = await resp.json();
          setError(body.title || 'An error occurred, please try again later');
        }
      }).catch((e) => {
        setError(`An error occurred, please try again later. ${e}`);
      }).finally(() => {
        setSubmitting(false);
        scrollToFormTop();
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
