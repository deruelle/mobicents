package org.openxdm.xcap.server.slee.resource.datasource;

import java.io.Serializable;

import javax.slee.resource.ResourceAdaptor;

import org.openxdm.xcap.common.uri.DocumentSelector;

public interface DataSourceResourceAdaptor extends ResourceAdaptor,
		Serializable {

	public DataSourceSbbInterface getDataSourceSbbInterface();

	public void postEvent(DocumentUpdatedEvent event, DocumentActivity activity);

	public DocumentActivity createDocumentActivity(DocumentSelector documentSelector);

	public DocumentActivity getDocumentActivity(DocumentSelector documentSelector);

	public void endDocumentActivity(DocumentSelector documentSelector);

}
