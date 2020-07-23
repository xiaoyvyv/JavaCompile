

package com.google.googlejavaformat;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An error that prevented formatting from succeeding.
 */
public class FormatterDiagnostic {
    private final int lineNumber;
    private final String message;
    private final int column;

    public static FormatterDiagnostic create(String message) {
        return new FormatterDiagnostic(-1, -1, message);
    }

    public static FormatterDiagnostic create(int lineNumber, int column, String message) {
        checkArgument(lineNumber >= 0);
        checkArgument(column >= 0);
        checkNotNull(message);
        return new FormatterDiagnostic(lineNumber, column, message);
    }

    private FormatterDiagnostic(int lineNumber, int column, String message) {
        this.lineNumber = lineNumber;
        this.column = column;
        this.message = message;
    }

    /**
     * Returns the line number on which the error occurred, or {@code -1} if the error does not have a
     * line number.
     */
    public int line() {
        return lineNumber;
    }

    /**
     * Returns the 0-indexed column number on which the error occurred, or {@code -1} if the error
     * does not have a column.
     */
    public int column() {
        return column;
    }

    /**
     * Returns a description of the problem that prevented formatting from succeeding.
     */
    public String message() {
        return message;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lineNumber >= 0) {
            sb.append(lineNumber).append(':');
        }
        if (column >= 0) {
            // internal column numbers are 0-based, but diagnostics use 1-based indexing by convention
            sb.append(column + 1).append(':');
        }
        if (lineNumber >= 0 || column >= 0) {
            sb.append(' ');
        }
        sb.append("error: ").append(message);
        return sb.toString();
    }
}
