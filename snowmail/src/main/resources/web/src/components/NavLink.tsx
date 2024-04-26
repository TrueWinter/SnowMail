import { NavLink as MantineLink, NavLinkProps } from '@mantine/core';
import { Link, useLocation, type LinkProps } from 'react-router-dom';

type CombinedProps = NavLinkProps & LinkProps
interface Props extends CombinedProps {
  to: string
}

export default function NavLink(props: Props) {
  const location = useLocation();
  const isActive = location.pathname.startsWith(props.to);

  return (
    <MantineLink w="fit-content" {...props} component={Link} active={isActive} />
  );
}
