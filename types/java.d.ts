/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2024-04-17 23:31:37.

export interface Account {
    username: string;
    password: string;
}

export interface Form {
    id: string;
    name: string;
    email: string;
    metadata: { [index: string]: string };
    inputs: InputUnion[];
}

export interface Input {
    inputType: "SCRIPT" | "MULTIPLE" | "CUSTOM" | "BUTTON" | "TEXT" | "TEXTAREA";
    customDisplayName: string;
    customAttributes: { [index: string]: string };
    ignoredOnClient: boolean;
}

export interface TextInput extends AbstractTextInput {
    inputType: "TEXT";
    type: TextInputTypes;
    pattern: string;
    patternError: string;
}

export interface TextAreaInput extends AbstractTextInput {
    inputType: "TEXTAREA";
    cols: string;
    rows: string;
}

export interface ScriptInput extends Input {
    inputType: "SCRIPT";
    src: string;
    defer: boolean;
    async: boolean;
    module: boolean;
}

export interface ButtonInput extends StylableInput {
    inputType: "BUTTON";
    type: ButtonInputTypes;
    text: string;
}

export interface MultipleInputs extends Input {
    inputType: "MULTIPLE";
    inputs: InputUnion[];
}

export interface CustomElementInput extends Input {
    inputType: "CUSTOM";
    type: string;
    innerHtml: string;
}

export interface AbstractTextInput extends StylableInput {
    inputType: "TEXT" | "TEXTAREA";
    label: string;
    name: string;
    required: boolean;
    placeholder: string;
    maxLength: number;
    includedInEmail: boolean;
}

export interface StylableInput extends Input {
    inputType: "BUTTON" | "TEXT" | "TEXTAREA";
    unstyled: boolean;
    cssStyles: { [index: string]: string };
    cssClasses: string[];
}

export type TextInputTypes = "text" | "email" | "number" | "password" | "tel" | "url" | "hidden";

export type ButtonInputTypes = "button" | "reset" | "submit";

export type InputUnion = TextInput | TextAreaInput | ScriptInput | ButtonInput | MultipleInputs | CustomElementInput;
