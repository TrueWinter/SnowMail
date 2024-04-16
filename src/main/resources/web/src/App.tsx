import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import { MantineProvider } from '@mantine/core';
import { NavigationProgress } from '@mantine/nprogress';
import { Notifications } from '@mantine/notifications';
import { ModalsProvider } from '@mantine/modals';
import { router } from './router';

import '@mantine/core/styles.css';
import '@mantine/nprogress/styles.css';
import '@mantine/notifications/styles.css';

// eslint-disable-next-line no-undef
createRoot(document.getElementById('app')).render(
  <MantineProvider defaultColorScheme="dark">
    <ModalsProvider>
      <NavigationProgress />
      <Notifications />
      <RouterProvider router={router} />
    </ModalsProvider>
  </MantineProvider>
);
