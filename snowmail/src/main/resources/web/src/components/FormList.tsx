import { type FormSummary } from '../pages/Forms';
import FormCard from './FormCard';
import NoFormsNotice from './NoFormsNotice';

interface Props {
  forms: FormSummary[]
}

export default function FormList({ forms }: Props) {
  return forms.length === 0 ?
    <NoFormsNotice /> :
    forms.map((form) => <FormCard key={form.id} data={form} />);
}
