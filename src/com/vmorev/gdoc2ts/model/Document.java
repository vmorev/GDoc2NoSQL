package com.vmorev.gdoc2ts.model;

/**
 * User: vmorev
 * Date: 11/13/11 4:03 PM
 */
public class Document extends AbstractDocument {
    private String xml;

    public Document() {
        super(AbstractDocument.DOC_TYPE_DOCUMENT);
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

}
