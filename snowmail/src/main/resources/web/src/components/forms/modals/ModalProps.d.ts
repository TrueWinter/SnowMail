import type { InputUnion } from '#types/java';

interface ModalProps {
  id: string
  state: InputUnion[]
  onClose: () => void
}

export interface DeleteModalProps extends ModalProps {
  // eslint-disable-next-line no-unused-vars
  remove: (key: string) => void
}

export interface EditModalProps extends DeleteModalProps {
  // eslint-disable-next-line no-unused-vars
  set: (key: string, changes: object) => void
}

export interface SettingsModalProps extends ModalProps {

}
