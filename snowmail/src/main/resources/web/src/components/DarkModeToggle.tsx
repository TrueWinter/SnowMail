import { useMantineColorScheme, ActionIcon, useComputedColorScheme } from '@mantine/core';
import { IconSun, IconMoon } from '@tabler/icons-react';

export default function DarkModeToggle() {
  const { colorScheme, setColorScheme } = useMantineColorScheme();
  const computedColorSchme = useComputedColorScheme('dark');
  const toggleColorScheme = () => setColorScheme(computedColorSchme === 'dark' ? 'light' : 'dark');

  return (
    <ActionIcon size="lg" color="gray" c="white" onClick={toggleColorScheme}>
      {colorScheme === 'dark' ? <IconSun /> : <IconMoon />}
    </ActionIcon>
  );
}
