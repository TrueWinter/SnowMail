import { MantineProvider } from '@mantine/core';
import MantineContactForm from './MantineContactForm';

// import '@mantine/core/styles/global.css';
import './mantine-global.css';
import '@mantine/core/styles/UnstyledButton.css';
import '@mantine/core/styles/Button.css';
import '@mantine/core/styles/Stack.css';
import '@mantine/core/styles/Group.css';
import '@mantine/core/styles/Loader.css';
import '@mantine/core/styles/Overlay.css';
import '@mantine/core/styles/LoadingOverlay.css';
import '@mantine/core/styles/Input.css';
import '@mantine/core/styles/Alert.css';

/** @param {import('./ContactForm').ContactFormProps} props */
export default function ContactForm({ url, id, inputs, providerProps = {} }) {
  /** @type {import('@mantine/core').MantineProviderProps} */
  const providerPropsWithDefaults = {
    withStaticClasses: false,
    ...providerProps
  };

  return (
    <MantineProvider {...providerPropsWithDefaults}>
      <MantineContactForm url={url} id={id} inputs={inputs} />
    </MantineProvider>
  );
}
