import { Link, Outlet, useLocation, useNavigation } from 'react-router-dom';
import { Paper, Container, Group, NavLink as MantineNavLink,
  useComputedColorScheme, Burger, Flex, Anchor } from '@mantine/core';
import { nprogress } from '@mantine/nprogress';
import { useDisclosure, useMediaQuery } from '@mantine/hooks';
import { PropsWithChildren } from 'react';
import NavLink from './components/NavLink';
import DarkModeToggle from './components/DarkModeToggle';
import { getUsername } from './util/api';

interface Links {
  label: string
  to: string
}
const links: Links[] = [{
  label: 'Forms',
  to: '/forms'
}, {
  label: 'Accounts',
  to: '/accounts'
}];

function MainContent(props: PropsWithChildren) {
  const location = useLocation();
  return ['/forms/add', '/forms/edit/'].some((e) => location.pathname.startsWith(e)) ? (
    <div style={{
      width: '100%',
      height: '100%',
      overflow: 'hidden'
    }}>
      {props.children}
    </div>
  ) : (
    <Container mx="lg" w="100%">
      {props.children}
    </Container>
  );
}

export function Component() {
  const computedColorSchme = useComputedColorScheme('dark');
  const location = useLocation();
  const navigation = useNavigation();
  const [opened, { toggle }] = useDisclosure(false);
  const mobile = useMediaQuery('(max-width: 576px)');

  setTimeout(() => {
    switch (navigation.state) {
      case 'idle':
        nprogress.complete();
        break;
      default:
        nprogress.start();
        break;
    }
  });

  const username = getUsername();

  const rightNavLinks = (
    <>
      <MantineNavLink label="My Account" component={Link}
        to={username ? `/accounts/edit/${username}` : null} />
      <MantineNavLink
        w="fit-content"
        label="Logout"
        style={{
          display: mobile && !opened ? 'none' : undefined
        }}
        component={Link}
        to="/logout"
      />
    </>
  );
  const nav = (
    <Flex gap="md" direction={mobile ? 'column' : 'row'}>
      {links.map((e) => <NavLink key={e.to} {...e} />)}
      {mobile && rightNavLinks}
    </Flex>
  );

  return (
    <Flex direction="column" h="100vh" align="center">
      {!['/login', '/logout'].includes(location.pathname) && (
        <Paper px="xl" py="md" w="100vw" radius={0}
          bg={computedColorSchme === 'dark' ? 'dark.9' : 'gray.3'}
          c={computedColorSchme === 'dark' ? 'white' : undefined}>
          <Flex justify="space-between" direction="row">
            <Group>
              <Anchor component={Link} fw={700} size="lg"
                c={computedColorSchme === 'dark' ? 'white' : 'dark'} to="/">SnowMail</Anchor>
              {!mobile && nav}
            </Group>
            <Flex gap="sm" align="center">
              <DarkModeToggle />
              {!mobile && rightNavLinks}
              <Burger opened={opened} onClick={toggle} hidden={!mobile} />
            </Flex>
          </Flex>
          {mobile && opened && nav}
        </Paper>
      )}
      <MainContent>
        <Outlet />
      </MainContent>
    </Flex>
  );
}
