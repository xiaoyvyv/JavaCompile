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

import java.util.List;

/**
 * A tree node for a 'switch' statement.
 * <p>
 * For example:
 * <pre>
 *   switch ( <em>expression</em> ) {
 *     <em>cases</em>
 *   }
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 14.11
 * @since 1.6
 */
public interface SwitchTree extends StatementTree {
    ExpressionTree getExpression();

    List<? extends CaseTree> getCases();
}
