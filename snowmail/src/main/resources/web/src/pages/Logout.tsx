import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export function Component() {
  const navigate = useNavigate();
  localStorage.removeItem('token');

  useEffect(() => {
    navigate('/login');
  }, []);

  return null;
}
