

package com.google.googlejavaformat;

/**
 * An {@code Op} is a member of the sequence of formatting operations emitted by {@link OpsBuilder}
 * and transformed by {@link DocBuilder} into a {@link Doc}. Leaf subclasses of {@link Doc}
 * implement {@code Op}; {@link Doc.Level} is the only non-leaf, and is represented by paired {@link
 * OpenOp}-{@link CloseOp} {@code Op}s.
 */
public interface Op {
    /**
     * Add an {@code Op} to a {@link DocBuilder}.
     *
     * @param builder the {@link DocBuilder}
     */
    void add(DocBuilder builder);
}
