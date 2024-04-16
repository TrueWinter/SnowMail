import { Navigate, createBrowserRouter } from 'react-router-dom';
import ErrorPage from './ErrorPage';

export const router = createBrowserRouter([{
  lazy: () => import('./RootLayout'),
  errorElement: <ErrorPage />,
  children: [{
    path: '/',
    element: <Navigate to="forms" />
  }, {
    path: '/login',
    lazy: () => import('./pages/Login')
  }, {
    path: '/logout',
    lazy: () => import('./pages/Logout')
  }, {
    path: '/accounts',
    lazy: () => import('./pages/Accounts'),
  }, {
    path: '/accounts/add',
    lazy: () => import('./pages/AddAccount')
  }, {
    path: '/accounts/edit/:username',
    lazy: () => import('./pages/EditAccount')
  }, {
    path: '/forms',
    lazy: () => import('./pages/Forms')
  }, {
    path: '/forms/add',
    lazy: () => import('./pages/AddForm')
  }, {
    path: '/forms/edit/:id',
    lazy: () => import('./pages/EditForm')
  }]
}]);
