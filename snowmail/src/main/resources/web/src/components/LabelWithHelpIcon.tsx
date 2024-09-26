import { Group, Input, Tooltip } from '@mantine/core';
import { IconHelpCircle } from '@tabler/icons-react';
import { type ReactNode } from 'react';

interface Props {
  label: string
  tooltip: ReactNode
}

export default function LabelWithHelpIcon({ label, tooltip }: Props) {
  return (
    <Group gap="4px">
      <Input.Label>{label}</Input.Label>
      <Tooltip label={tooltip}>
        <IconHelpCircle />
      </Tooltip>
    </Group>
  );
}
