package org.exist.indexing;

import org.exist.dom.DocumentSet;
import org.exist.dom.ExtNodeSet;
import org.exist.dom.NodeSet;
import org.exist.dom.QName;
import org.exist.storage.ElementValue;
import org.exist.xquery.NodeSelector;

/**
 * Core interface for structural indexes. The structural index provides access to elements and attributes
 * through their name and relation.
 */
public interface StructuralIndex {

    public final static String STRUCTURAL_INDEX_ID = "structural-index";

    public final static String DEFAULT_CLASS = "org.exist.storage.structural.NativeStructuralIndex";
    
    public boolean matchElementsByTagName(byte type, DocumentSet docs, QName qname, NodeSelector selector);

    public boolean matchDescendantsByTagName(byte type, QName qname, int axis, DocumentSet docs, ExtNodeSet contextSet,
                                             int contextId);

    public NodeSet findElementsByTagName(byte type, DocumentSet docs, QName qname, NodeSelector selector);

    public NodeSet findDescendantsByTagName(byte type, QName qname, int axis,
        DocumentSet docs, NodeSet contextSet,  int contextId);

    public NodeSet findAncestorsByTagName(byte type, QName qname, int axis, DocumentSet docs, NodeSet contextSet, int contextId);
}
