import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getUsername } from '../util/api';

export function Component() {
  const navigate = useNavigate();
  const username = getUsername();
  localStorage.removeItem('token');

  useEffect(() => {
    if (!username) {
      location.href = '/login/sso/logout';
      return;
    }

    navigate('/login');
  }, []);

  return null;
}
