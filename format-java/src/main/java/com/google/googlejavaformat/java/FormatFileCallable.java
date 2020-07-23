

package com.google.googlejavaformat.java;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import java.util.concurrent.Callable;

/**
 * Encapsulates information about a file to be formatted, including which parts of the file to
 * format.
 */
public class FormatFileCallable implements Callable<String> {
    private final String input;
    private final CommandLineOptions parameters;
    private final JavaFormatterOptions options;

    public FormatFileCallable(
            CommandLineOptions parameters, String input, JavaFormatterOptions options) {
        this.input = input;
        this.parameters = parameters;
        this.options = options;
    }

    @Override
    public String call() throws FormatterException {
        if (parameters.fixImportsOnly()) {
            return fixImports(input);
        }

        String formatted =
                new Formatter(options).formatSource(input, characterRanges(input).asRanges());
        formatted = fixImports(formatted);
        return formatted;
    }

    private String fixImports(String input) throws FormatterException {
        if (parameters.removeUnusedImports()) {
            input =
                    RemoveUnusedImports.removeUnusedImports(
                            input, new Object());
        }
        if (parameters.sortImports()) {
            input = ImportOrderer.reorderImports(input);
        }
        return input;
    }

    private RangeSet<Integer> characterRanges(String input) {
        final RangeSet<Integer> characterRanges = TreeRangeSet.create();

        if (parameters.lines().isEmpty() && parameters.offsets().isEmpty()) {
            characterRanges.add(Range.closedOpen(0, input.length()));
            return characterRanges;
        }

        characterRanges.addAll(Formatter.lineRangesToCharRanges(input, parameters.lines()));

        for (int i = 0; i < parameters.offsets().size(); i++) {
            Integer length = parameters.lengths().get(i);
            if (length == 0) {
                // 0 stands for "format the line under the cursor"
                length = 1;
            }
            characterRanges.add(
                    Range.closedOpen(parameters.offsets().get(i), parameters.offsets().get(i) + length));
        }

        return characterRanges;
    }
}
