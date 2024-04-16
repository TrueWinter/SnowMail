import { router } from '../router';

let redirectUrl = '/';
export function redirectAfterLogin() {
  return redirectUrl;
}

interface Opts {
  noRedirectOn401?: boolean
}

interface RequestBase extends Opts {
    route: string
}

const METHODS_THAT_CANNOT_CONTAIN_DATA = ['GET', 'DELETE'] as const;
interface RequestWithoutData extends RequestBase {
    method: typeof METHODS_THAT_CANNOT_CONTAIN_DATA[number]
}

const METHODS_THAT_CAN_CONTAIN_DATA = ['POST', 'PUT', 'PATCH'] as const;
interface RequestWithData extends RequestBase {
    method: typeof METHODS_THAT_CAN_CONTAIN_DATA[number],
    data?: FormData | object
}

type Request = RequestWithData | RequestWithoutData

export interface HttpResponse {
  status: number
  body: Record<string, any>
}

async function request(opts: Request): Promise<HttpResponse> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    Accept: 'application/json'
  };

  const jwt = localStorage.getItem('token');
  if (jwt) headers.Authorization = `Bearer ${jwt}`;

  let serializedData: string;

  // METHODS_THAT_CAN_CONTAIN_DATA.includes(request.method) would be better, but TypeScript wouldn't allow it
  if (opts.method === 'POST' || opts.method === 'PUT' || opts.method === 'PATCH') {
    if (!opts.data) {
      serializedData = undefined;
    } else if (opts.data instanceof FormData) {
      serializedData = JSON.stringify(Object.fromEntries(opts.data));
    } else {
      serializedData = JSON.stringify(opts.data);
    }
  }

  const resp = await fetch(opts.route, {
    method: opts.method,
    headers,
    body: serializedData
  });

  if (resp.status === 401 && !opts.noRedirectOn401) {
    localStorage.removeItem('token');
    redirectUrl = location.pathname;
    // https://github.com/remix-run/react-router/issues/9422#issuecomment-1301182219
    router.navigate('/login');
    return {
      status: 401,
      body: null
    };
  }

  const respData = await resp.text();
  return {
    status: resp.status,
    body: respData ? JSON.parse(respData) : {}
  };
}

export async function get(route: string, opts?: Opts) {
  return request({
    method: 'GET',
    route,
    ...opts
  });
}

export async function post(route: string, data: FormData | object, opts?: Opts) {
  return request({
    method: 'POST',
    route,
    data,
    ...opts
  });
}

export async function patch(route: string, data: FormData | object, opts?: Opts) {
  return request({
    method: 'PATCH',
    route,
    data,
    ...opts
  });
}

export async function put(route: string, data: FormData | object, opts?: Opts) {
  return request({
    method: 'PUT',
    route,
    data,
    ...opts
  });
}

export async function del(route: string, opts?: Opts) {
  return request({
    method: 'DELETE',
    route,
    ...opts
  });
}

/**
 * Only use this on pages that do not request data from the server on load
 */
export async function ensureUserIsLoggedIn(): Promise<HttpResponse> {
  return get('/api/is-logged-in');
}

export function getUsername(): string {
  const jwt = localStorage.getItem('token');
  return jwt ? JSON.parse(atob(jwt.split('.')[1])).username : null;
}
