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
 * A tree node for a basic 'for' loop statement.
 * <p>
 * For example:
 * <pre>
 *   for ( <em>initializer</em> ; <em>condition</em> ; <em>update</em> )
 *       <em>statement</em>
 * </pre>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @jls section 14.14.1
 * @since 1.6
 */
public interface ForLoopTree extends StatementTree {
    List<? extends StatementTree> getInitializer();

    ExpressionTree getCondition();

    List<? extends ExpressionStatementTree> getUpdate();

    StatementTree getStatement();
}
