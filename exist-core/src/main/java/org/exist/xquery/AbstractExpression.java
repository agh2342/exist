/*
 * eXist-db Open Source Native XML Database
 * Copyright (C) 2001 The eXist-db Authors
 *
 * info@exist-db.org
 * http://www.exist-db.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.exist.xquery;

import org.exist.dom.persistent.DocumentSet;
import org.exist.source.Source;
import org.exist.xquery.parser.XQueryAST;
import org.exist.xquery.value.Item;
import org.exist.xquery.value.Sequence;

public abstract class AbstractExpression implements Expression {

    private int expressionId = EXPRESSION_ID_INVALID;
    protected XQueryContext context;
    protected int line = -1;
    protected int column = -1;
    protected DocumentSet contextDocSet = null;

    /**
     * Holds the context id for the context of this expression.
     */
    protected int contextId = Expression.NO_CONTEXT_ID;

    /**
     * The purpose of ordered and unordered flag is to set the ordering mode in 
     * the static context to ordered or unordered for a certain region in a query. 
     */
    protected boolean unordered = false;

    public AbstractExpression(XQueryContext context) {
        this.context = context;
        this.expressionId = context.nextExpressionId();
    }

    @Override
    public int getExpressionId() {
        return expressionId;
    }

    @Override
    public void setContextId(int contextId) {
        this.contextId = contextId;
    }

    @Override
    public int getContextId() {
        return contextId;
    }

    @Override
    public void resetState(boolean postOptimization) {
        contextDocSet = null;
    }

    @Override
    public boolean needsReset() {
        // always return true unless a subclass overwrites this
        return true;
    }

    /**
     * The default cardinality is {@link Cardinality#EXACTLY_ONE}.
     */
    @Override
    public Cardinality getCardinality() {
        return Cardinality.EXACTLY_ONE; // default cardinality
    }

    /**
     * Returns {@link Dependency#DEFAULT_DEPENDENCIES}.
     *
     * @see org.exist.xquery.Expression#getDependencies()
     */
    @Override
    public int getDependencies() {
        return Dependency.DEFAULT_DEPENDENCIES;
    }

    @Override
    public void setPrimaryAxis(int axis) {
    }

    @Override
    public int getPrimaryAxis() {
        return Constants.UNKNOWN_AXIS;
    }

    @Override
    public void setContextDocSet(DocumentSet contextSet) {
        this.contextDocSet = contextSet;
    }

    @Override
    public DocumentSet getContextDocSet() {
        return contextDocSet;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void setASTNode(XQueryAST ast) {
        if(ast != null) {
            line = ast.getLine();
            column = ast.getColumn();
        }
    }

    @Override
    public void setLocation(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public Source getSource() {
        try {
            return context.getSource();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    @Override
    public XQueryContext getContext() {
        return context;
    }

    @Override
    public int getSubExpressionCount() {
        //default value
        return 0;
    }

    @Override
    public Expression getSubExpression(int index) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + getSubExpressionCount());
    }

    @Override
    public boolean allowMixedNodesInReturn() {
        return false;
    }

    @Override
    public Expression simplify() {
        return this;
    }

    @Override
    public Expression getParent() {
        return null;
    }

    @Override
    public boolean evalNextExpressionOnEmptyContextSequence() {
        return false;
    }
}