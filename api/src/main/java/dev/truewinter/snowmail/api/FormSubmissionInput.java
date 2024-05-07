package dev.truewinter.snowmail.api;

import dev.truewinter.snowmail.api.inputs.Input;

/**
 * @param input The input
 * @param value The value
 */
public record FormSubmissionInput(Input input, String value) {
}
