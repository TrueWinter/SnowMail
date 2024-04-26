import { useEffect, useState } from 'react';

/** @param {import('./ContactForm').ContactFormProps} props */
export default function ContactForm(props) {
  /** @type {[import('../types/public').InputUnion[], import('react').Dispatch<import('react').SetStateAction<import('../types/public').InputUnion[]>]} */
  const [formState, setFormState] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch(`${props.url}/public-api/forms/${props.id}`).then((resp) => {
      if (resp.status !== 200) {
        throw new Error();
      }

      resp.json().then(setFormState);
    }).catch(() => {
      setError('An error occurred, please try again later');
    });
  }, []);

  console.log(formState, error);

  return (
    <>
      {props.url}:{props.id}
    </>
  );
}
