/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.source.tree;

/**
 * A tree node for a 'do' statement.
 * <p>
 * For example:
 * <pre>
 *   do
 *       <em>statement</em>
 *   while ( <em>expression</em> );
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 14.13
 * @since 1.6
 */
public interface DoWhileLoopTree extends StatementTree {
    ExpressionTree getCondition();

    StatementTree getStatement();
}
