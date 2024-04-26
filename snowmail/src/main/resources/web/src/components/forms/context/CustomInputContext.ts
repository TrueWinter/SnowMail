import { createContext } from 'react';
import { InputUnion } from '#types/java';

export const CustomInputContext = createContext<InputUnion[]>([]);
