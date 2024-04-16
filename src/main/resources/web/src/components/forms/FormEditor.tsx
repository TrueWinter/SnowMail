import { Burger as B, Button, ButtonProps, Container, Drawer, Grid, Group, Paper,
  PolymorphicComponentProps, ScrollArea, Title, useComputedColorScheme } from '@mantine/core';
import { useDisclosure, useListState, useMediaQuery } from '@mantine/hooks';
import { ReactNode, forwardRef, useImperativeHandle, useRef } from 'react';
import { v4 as uuid } from 'uuid';
import { useForm } from '@mantine/form';
import FormInputs from './FormInputs';
import FormProperties from './FormProperties';
import FormEditorInputs from './FormEditorInputs';

interface Props {
  form?: any // TODO: Create interfaces
}

interface BurgerProps extends PolymorphicComponentProps<'span', ButtonProps> {
  side: 'left' | 'right'
}
function Burger(props: BurgerProps) {
  const computedColorSchme = useComputedColorScheme('dark');

  const BurgerComponent = (
    <B style={computedColorSchme === 'light' && {
      '--burger-color': 'var(--mantine-color-gray-1)'
    }} />
  );

  // Buttons cannot be nested inside other buttons, and Burger is a button
  return (
    <Button component="span" leftSection={props.side === 'left' && BurgerComponent}
      rightSection={props.side === 'right' && BurgerComponent} {...props}>{props.children}</Button>
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
  const mobile = useMediaQuery('(max-width: 576px)');
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

// TODO: Create proper interfaces
export interface Input {
  input: string
  name: string
  key: any
}

// TODO: Move into parent and fetch from server
const data = [];

export default function FormEditor({ form }: Props) {
  const mobile = useMediaQuery('(max-width: 576px)');
  const inputsRef = useRef<SidebarRef>(null);
  const propertiesRef = useRef<SidebarRef>(null);
  const [state, handlers] = useListState<Input>(data);

  const propertiesForm = useForm({
    initialValues: {
      name: '',
      email: ''
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
      <Sidebar title="Inputs" ref={inputsRef}>
        <FormInputs add={(input, name) => {
          handlers.append({
            input,
            name,
            key: uuid()
          });

          inputsRef.current.close();
        }} />
      </Sidebar>

      <Grid.Col span={!mobile ? 7 : 12}>
        <Container h="100%" p="sm" mx={{ md: 'xl' }} fluid>
          <ScrollArea.Autosize h="100%" scrollbars="y">
            {mobile && (
              <Group grow>
                <Burger side="left" onClick={inputsRef.current?.open}>Inputs</Burger>
                <Burger side="right" onClick={propertiesRef.current?.open}>Properties</Burger>
              </Group>
            )}
            <Title mb="sm">{form ? 'Edit' : 'Create'} Form</Title>
            <FormEditorInputs data={[state, handlers]} remove={remove} form={propertiesForm} />
          </ScrollArea.Autosize>
        </Container>
      </Grid.Col>

      <Sidebar title="Properties" position="right" ref={propertiesRef}>
        <FormProperties form={propertiesForm} />
      </Sidebar>
    </Grid>
  );
}
