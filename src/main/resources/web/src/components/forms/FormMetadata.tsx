import { type Dispatch, type SetStateAction } from 'react';
import KeyValueInputs from './KeyValueInputs';
import { type KV } from './KeyValueInput';

export interface FormMetadataProps {
  metadata: KV[]
  setMetadata: Dispatch<SetStateAction<KV[]>>
}

export default function FormMetadata({ metadata, setMetadata }: FormMetadataProps) {
  function change(m: KV[]) {
    setMetadata(m);
  }

  return <KeyValueInputs current={metadata} onChange={change} />;
}
