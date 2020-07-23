

package com.google.googlejavaformat.java;

import com.google.common.collect.ImmutableList;
import com.google.googlejavaformat.FormatterDiagnostic;

import java.util.List;

/**
 * Checked exception class for formatter errors.
 */
public final class FormatterException extends Exception {

    private ImmutableList<FormatterDiagnostic> diagnostics;

    public FormatterException(String message) {
        this(FormatterDiagnostic.create(message));
    }

    public FormatterException(FormatterDiagnostic diagnostic) {
        this(ImmutableList.of(diagnostic));
    }

    public FormatterException(Iterable<FormatterDiagnostic> diagnostics) {
        super(diagnostics.iterator().next().toString());
        this.diagnostics = ImmutableList.copyOf(diagnostics);
    }

    public List<FormatterDiagnostic> diagnostics() {
        return diagnostics;
    }
}
