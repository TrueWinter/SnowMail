import { type ReactNode, useEffect } from 'react';
import icon from '../snowmail-icon.png';

interface Props {
  title?: string
  children: ReactNode
}

export default function Page({ title, children }: Props) {
  useEffect(() => {
    document.title = title ? `${title} | SnowMail` : 'SnowMail';

    if (!document.querySelector('link[rel="icon"]')) {
      const link = document.createElement('link');
      link.rel = 'icon';
      link.type = 'image/png';
      // @ts-ignore VSCode for some reason thinks it needs to use Astro types here
      link.href = icon;
      document.head.appendChild(link);
    }
  }, []);

  return children;
}
