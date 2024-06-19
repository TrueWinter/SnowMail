import { Text } from '@mantine/core';
import { getRole } from '../util/api';

export default function NoFormsNotice() {
  const role = getRole();

  return role === 'ADMIN' ? (
    <Text>No forms added yet. Click the create button above to get started.</Text>
  ) : (
    <Text>You do not have permission to access any forms.</Text>
  );
}
