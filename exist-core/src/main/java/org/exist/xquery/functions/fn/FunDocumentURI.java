/*
 * eXist Open Source Native XML Database
 * Copyright (C) 2001-2019 The eXist Project
 * http://exist-db.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.exist.xquery.functions.fn;

import org.exist.dom.persistent.NodeProxy;
import org.exist.dom.QName;
import org.exist.dom.memtree.DocumentImpl;
import org.exist.xmldb.XmldbURI;
import org.exist.xquery.Cardinality;
import org.exist.xquery.Dependency;
import org.exist.xquery.Function;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.Profiler;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.AnyURIValue;
import org.exist.xquery.value.FunctionReturnSequenceType;
import org.exist.xquery.value.FunctionParameterSequenceType;
import org.exist.xquery.value.Item;
import org.exist.xquery.value.NodeValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.Type;

import static org.exist.xquery.FunctionDSL.*;

/**
 * @author wolf, agh
 */
public class FunDocumentURI extends Function {

    private static final String FS_DOCUMENT_URI_NAME = "document-uri";
    private static final FunctionParameterSequenceType FS_DOCUMENT_URI_OPT_PARAM_NODE = optParam("node", Type.NODE, "The input document-node");
    static final FunctionSignature[] FS_DOCUMENT_URI = functionSignatures(
            new QName(FS_DOCUMENT_URI_NAME, Function.BUILTIN_FUNCTION_NS),
            "Returns the absolute URI of the resource from which the " +
                    "document node $document-node was constructed. " +
                    "If none such URI exists returns the empty sequence. " +
                    "If $document-node is the empty sequence, returns the empty sequence.",
            returnsOpt(Type.ANY_URI, "The document URI of $document-node"),
            arities(
                    arity(),
                    arity(FS_DOCUMENT_URI_OPT_PARAM_NODE)
            )
    );

    public FunDocumentURI(final XQueryContext context, final FunctionSignature signature) {
        super(context, signature);
    }

    /* (non-Javadoc)
     * @see org.exist.xquery.Expression#eval(org.exist.xquery.StaticContext, org.exist.dom.persistent.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)
     */
    public Sequence eval(final Sequence contextSequence, final Item contextItem) throws XPathException {
        if (context.getProfiler().isEnabled()) {
            context.getProfiler().start(this);
            context.getProfiler().message(this, Profiler.DEPENDENCIES,
                    "DEPENDENCIES", Dependency.getDependenciesName(this.getDependencies()));
            if (contextSequence != null) {
                context.getProfiler().message(this, Profiler.START_SEQUENCES,
                        "CONTEXT SEQUENCE", contextSequence);
            }
            if (contextItem != null) {
                context.getProfiler().message(this, Profiler.START_SEQUENCES,
                        "CONTEXT ITEM", contextItem.toSequence());
            }
        }
        final Sequence seq = getArgument(0).eval(contextSequence, contextItem);
        Sequence result = Sequence.EMPTY_SEQUENCE;
        if (!seq.isEmpty()) {
            final NodeValue value = (NodeValue) seq.itemAt(0);
            if (value.getImplementationType() == NodeValue.PERSISTENT_NODE) {
                final NodeProxy node = (NodeProxy) value;
                //Returns the empty sequence if the node is not a document node. 
                if (node.isDocument()) {
                    final XmldbURI path = node.getOwnerDocument().getURI();
                    result = new AnyURIValue(path);
                }
            } else {
                if (value instanceof DocumentImpl &&
                        ((DocumentImpl) value).getDocumentURI() != null) {
                    result = new AnyURIValue(((DocumentImpl) value).getDocumentURI());
                }
            }
        }
        if (context.getProfiler().isEnabled()) {
            context.getProfiler().end(this, "", result);
        }
        return result;
    }
}
