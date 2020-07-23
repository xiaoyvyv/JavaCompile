

package com.google.googlejavaformat;

/**
 * Rewrite comments. This interface is implemented by {@link
 * com.google.googlejavaformat.java.JavaCommentsHelper JavaCommentsHelper}.
 */
public interface CommentsHelper {
    /**
     * Try to rewrite comments, returning rewritten text.
     *
     * @param tok      the comment's tok
     * @param maxWidth the line length for the output
     * @param column0  the current column
     * @return the rewritten comment
     */
    String rewrite(Input.Tok tok, int maxWidth, int column0);
}
