import { Burger as B, Button, ButtonProps, Container, Drawer, Grid, Paper,
  PolymorphicComponentProps, ScrollArea, Title, useComputedColorScheme } from '@mantine/core';
import { useDisclosure, useListState, useViewportSize } from '@mantine/hooks';
import { ReactNode, forwardRef, useImperativeHandle, useRef } from 'react';
import { v4 as uuid } from 'uuid';
import { useForm } from '@mantine/form';
import FormProperties from './FormProperties';
import FormEditorInputs from './FormEditorInputs';
import { type InputUnion, type Form } from '#types/java';

function Burger(props: PolymorphicComponentProps<'span', ButtonProps>) {
  const computedColorSchme = useComputedColorScheme('dark');

  const BurgerComponent = (
    <B style={computedColorSchme === 'light' && {
      '--burger-color': 'var(--mantine-color-gray-1)'
    }} />
  );

  // Buttons cannot be nested inside other buttons, and Burger is a button
  return (
    <Button component="span" rightSection={BurgerComponent} {...props}>{props.children}</Button>
  );
}

interface SidebarProps {
  children: ReactNode
  title: string
  position?: 'bottom' | 'left' | 'right' | 'top'
}
interface SidebarRef {
  open: () => void
  close: () => void
}
const Sidebar = forwardRef((props: SidebarProps, ref) => {
  const computedColorSchme = useComputedColorScheme('dark');
  const mobile = useViewportSize().width <= 576;
  const [opened, { open, close }] = useDisclosure(false);

  useImperativeHandle(ref, () => ({
    open, close
  }));

  return mobile ? (
    <Drawer title={props.title} opened={opened && mobile} onClose={close} position={props.position}>
      {props.children}
    </Drawer>
  ) : (
    <Grid.Col span="auto">
      <Paper h="100%" bg={computedColorSchme === 'dark' ? 'dark.8' : 'gray.1'} p="sm" radius="0">
        {props.children}
      </Paper>
    </Grid.Col>
  );
});

export interface Input {
  key: string
  input: InputUnion
}

function toKeyedInputArray(inputs: InputUnion[]): Input[] {
  if (!inputs) return [];

  return inputs.map((e) => ({
    key: uuid(),
    input: e
  }));
}

interface Props {
  form?: Form
}

export default function FormEditor({ form }: Props) {
  const mobile = useViewportSize().width <= 576;
  const propertiesRef = useRef<SidebarRef>(null);
  const [state, handlers] = useListState<Input>(toKeyedInputArray(form?.inputs));

  const propertiesForm = useForm({
    initialValues: {
      name: form?.name || '',
      email: form?.email || ''
    },
    validate: {
      name: (v) => (!v ? 'Name is required' : null),
      email: (v) => (!/^\S+@\S+$/.test(v) ? 'Invalid email' : null)
    }
  });

  function remove(key: string) {
    handlers.filter((item) => item.key !== key);
  }

  return (
    <Grid h="100%" overflow="hidden" gutter="0" styles={{
      inner: {
        height: '100%'
      },
      col: {
        height: '100%'
      }
    }}>
      <Grid.Col span={!mobile ? 9 : 12}>
        <Container h="100%" p="sm" mx={{ md: 'xl' }} fluid>
          <ScrollArea.Autosize h="100%" scrollbars="y">
            {mobile && (
              <Burger onClick={propertiesRef.current?.open} w="100%">Properties</Burger>
            )}
            <Title mb="sm">{form ? 'Edit' : 'Create'} Form</Title>
            <FormEditorInputs data={[state, handlers]} remove={remove}
              propertiesForm={propertiesForm} />
          </ScrollArea.Autosize>
        </Container>
      </Grid.Col>

      <Sidebar title="Properties" position="right" ref={propertiesRef}>
        <FormProperties propertiesForm={propertiesForm} form={form} />
      </Sidebar>
    </Grid>
  );
}
