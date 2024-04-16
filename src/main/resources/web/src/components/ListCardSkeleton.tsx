import { Skeleton } from '@mantine/core';

interface Props {
  amount?: number
}

export default function ListCardSkeleton({ amount = 3 }: Props) {
  // eslint-disable-next-line react/no-array-index-key
  return new Array(amount).fill(null).map((e, i) => <Skeleton key={i} h="64" />);
}
