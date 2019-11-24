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
 * A tree node for a 'while' loop statement.
 * <p>
 * For example:
 * <pre>
 *   while ( <em>condition</em> )
 *     <em>statement</em>
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 14.12
 * @since 1.6
 */
public interface WhileLoopTree extends StatementTree {
    ExpressionTree getCondition();

    StatementTree getStatement();
}
