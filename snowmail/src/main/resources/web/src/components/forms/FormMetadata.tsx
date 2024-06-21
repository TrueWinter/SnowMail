import { useContext } from 'react';
import KeyValueInputs from './KeyValueInputs';
import { type KV } from './KeyValueInput';
import { MetadataContext } from './context/MetadataContext';

export default function FormMetadata() {
  const { metadata, setMetadata } = useContext(MetadataContext);
  function change(m: KV[]) {
    setMetadata(m);
  }

  return <KeyValueInputs current={metadata} onChange={change} pattern="^[0-9a-zA-Z\-_]+$" />;
}
