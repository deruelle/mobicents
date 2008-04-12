package org.openxdm.xcap.server.slee.datasource.jdbc;

import java.io.StringReader;

import org.openxdm.xcap.common.datasource.Document;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.NotWellFormedConflictException;
import org.openxdm.xcap.common.xml.XMLValidator;

/**
 * @author Luis Barreiro
 */
public class JDBCDocument implements Document {

    private final String xml;
    private final String eTag;

    public JDBCDocument(String eTag,String xml) {
        this.xml = xml;
        this.eTag = eTag;
    }

    public String getAsString() throws InternalServerErrorException {
        return xml;
    }

    public org.w3c.dom.Document getAsDOMDocument() throws InternalServerErrorException {
        try {
            return XMLValidator.getWellFormedDocument(new StringReader(xml));
        } catch (NotWellFormedConflictException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
    
    public String getETag() {
    	return eTag;
    }
}
