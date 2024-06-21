import { type Dispatch, type SetStateAction, createContext } from 'react';
import { type KV } from '../KeyValueInput';

export const MetadataContext = createContext<{
  metadata: KV[]
  setMetadata: Dispatch<SetStateAction<KV[]>>
}>(null);
