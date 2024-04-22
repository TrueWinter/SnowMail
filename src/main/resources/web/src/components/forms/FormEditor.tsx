import { Burger as B, Button, ButtonProps, Container, Drawer, Grid, LoadingOverlay, Paper,
  PolymorphicComponentProps, ScrollArea, Title, useComputedColorScheme } from '@mantine/core';
import { useDisclosure, useListState } from '@mantine/hooks';
import { type ReactNode, forwardRef, useImperativeHandle, useRef, useState,
  useEffect } from 'react';
import { useForm } from '@mantine/form';
import { v4 as uuid } from 'uuid';
import { notifications } from '@mantine/notifications';
import { nprogress } from '@mantine/nprogress';
import { useNavigate } from 'react-router-dom';
import FormProperties from './FormProperties';
import FormEditorInputs from './FormEditorInputs';
import { type InputUnion, type Form } from '#types/java';
import { deepCopyObjArr } from '../../util/data';
import { type KV } from './KeyValueInput';
import { serialize, serializeInputs } from '../../util/forms';
import { post, put } from '../../util/api';

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
  const [opened, { open, close }] = useDisclosure(false);

  useImperativeHandle(ref, () => ({
    open, close
  }));

  return (
    <>
      <Drawer hiddenFrom="md" title={props.title} opened={opened} onClose={close}
        position={props.position}>
        {props.children}
      </Drawer>
      <Grid.Col visibleFrom="md" span="auto">
        <Paper h="100%" bg={computedColorSchme === 'dark' ? 'dark.8' : 'gray.1'} p="sm" radius="0">
          {props.children}
        </Paper>
      </Grid.Col>
    </>
  );
});

interface Props {
  form?: Form
}

export default function FormEditor({ form }: Props) {
  const propertiesRef = useRef<SidebarRef>(null);
  const [state, handlers] = useListState<InputUnion>(form?.inputs);
  const [metadata, setMetadata] = useState<KV[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (form && Object.keys(form?.metadata).length > 0) {
      const m: KV[] = Object.entries(form?.metadata).map((e) => ({
        id: uuid(),
        key: e[0],
        value: e[1]
      }));

      if (Object.entries(metadata).every(([key, value]) => m[key] === value)) {
        setMetadata(m);
      }
    }
  }, []);

  const propertiesForm = useForm({
    initialValues: {
      name: form?.name || '',
      email: form?.email || ''
    },
    validateInputOnChange: true,
    validate: {
      name: (v) => (!v ? 'Name is required' : null),
      email: (v) => (!/^\S+@\S+$/.test(v) ? 'Invalid email' : null)
    }
  });

  function filter(key: string, data: InputUnion[]) {
    const filtered: InputUnion[] = [];

    data.forEach((input) => {
      if (input.rKey !== key) {
        if (input.inputType === 'MULTIPLE') {
          filtered.push({
            ...input,
            inputs: filter(key, input.inputs)
          });
        } else {
          filtered.push(input);
        }
      }
    });

    return filtered;
  }

  function set(key: string, changes: object, data: InputUnion[] = state) {
    // eslint-disable-next-line no-restricted-syntax
    for (const [i, input] of data.entries()) {
      if (input.rKey === key) {
        // eslint-disable-next-line no-param-reassign
        data[i] = {
          ...input,
          ...changes
        };
      } else if (input.inputType === 'MULTIPLE') {
        set(key, changes, input.inputs);
      }
    }

    handlers.setState(deepCopyObjArr(data));
  }

  function remove(key: string) {
    handlers.setState(filter(key, state));
  }

  async function save() {
    setSubmitting(true);
    nprogress.start();

    const submitData: Form = {
      id: form?.id,
      name: propertiesForm.getValues().name,
      email: propertiesForm.getValues().email,
      metadata: serialize(metadata),
      inputs: serializeInputs(state)
    };

    async function submit() {
      if (form) {
        return put(`/api/forms/${form.id}`, submitData);
      }

      return post('/api/forms', submitData);
    }

    const resp = await submit();
    setSubmitting(false);
    nprogress.complete();
    if (resp.status !== 200) {
      notifications.show({
        color: 'red',
        message: 'Failed to save form. Ensure that all required fields have values. ' +
          `Error code: ${resp.status}`
      });
    } else if (resp.body.id) {
      notifications.show({
        message: 'Form created'
      });
      navigate(`/forms/edit/${resp.body.id}`);
    } else {
      notifications.show({
        message: 'Form saved'
      });
    }
  }

  return (
    <>
      <LoadingOverlay visible={submitting} />
      <Grid h="100%" overflow="hidden" gutter="0" styles={{
        inner: {
          height: '100%'
        },
        col: {
          height: '100%'
        }
      }}>
        <Grid.Col span={{ md: 9, xs: 12 }}>
          <Container h="100%" p="sm" mx={{ md: 'xl' }} fluid>
            <ScrollArea.Autosize h="100%" scrollbars="y">
              <Burger hiddenFrom="md" onClick={() => propertiesRef.current?.open()}
                w="100%">Properties</Burger>
              <Title mb="sm">{form ? 'Edit' : 'Create'} Form</Title>
              <FormEditorInputs data={[state, handlers]} remove={remove}
                set={set} propertiesForm={propertiesForm} submit={save} />
            </ScrollArea.Autosize>
          </Container>
        </Grid.Col>

        <Sidebar title="Properties" position="right" ref={propertiesRef}>
          <FormProperties propertiesForm={propertiesForm} metadata={metadata}
            setMetadata={setMetadata} />
        </Sidebar>
      </Grid>
    </>
  );
}
