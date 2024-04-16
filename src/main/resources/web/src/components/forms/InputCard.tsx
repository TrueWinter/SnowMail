import { Card, Text } from '@mantine/core';

interface Props {
  name: string
  onClick: () => void;
}

export default function InputCard({ name, onClick }: Props) {
  return (
    <Card withBorder onClick={onClick} style={{
      cursor: 'pointer',
      userSelect: 'none'
    }}>
      <Text>{name}</Text>
    </Card>
  );
}
