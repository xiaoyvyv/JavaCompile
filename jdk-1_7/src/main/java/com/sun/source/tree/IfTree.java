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
 * A tree node for an 'if' statement.
 * <p>
 * For example:
 * <pre>
 *   if ( <em>condition</em> )
 *      <em>thenStatement</em>
 *
 *   if ( <em>condition</em> )
 *       <em>thenStatement</em>
 *   else
 *       <em>elseStatement</em>
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 14.9
 * @since 1.6
 */
public interface IfTree extends StatementTree {
    ExpressionTree getCondition();

    StatementTree getThenStatement();

    /**
     * @return null if this if statement has no else branch.
     */
    StatementTree getElseStatement();
}
