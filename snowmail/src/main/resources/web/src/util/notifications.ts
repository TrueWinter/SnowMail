import { notifications } from '@mantine/notifications';

export function noPermissionForPage() {
  notifications.show({
    message: 'You do not have permission to access that page',
    color: 'red'
  });
}
