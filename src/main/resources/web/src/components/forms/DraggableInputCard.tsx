import { ActionIcon, Card, Group, Text } from '@mantine/core';
import { IconEdit, IconGripVertical, IconTrash } from '@tabler/icons-react';

interface Props {
  input: string
  name: string
}

export default function DraggableInputCard({ input, name }: Props) {
  return (
    <Card withBorder style={{
      cursor: 'pointer'
    }}>
      <Group justify="space-between" style={{
        userSelect: 'none'
      }}>
        <Group>
          <IconGripVertical style={{
            cursor: 'move'
          }} />
          <Text size="lg">{input}: {name}</Text>
        </Group>
        <Group>
          <ActionIcon size="lg">
            <IconEdit />
          </ActionIcon>
          <ActionIcon bg="red" size="lg">
            <IconTrash />
          </ActionIcon>
        </Group>
      </Group>
    </Card>
  );
}
