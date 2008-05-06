package org.openxdm.xcap.server.slee.resource.datasource;

import java.io.Serializable;

import javax.slee.resource.ResourceAdaptor;

import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.uri.DocumentSelector;

public interface DataSourceResourceAdaptor extends ResourceAdaptor,
		Serializable {

	public DataSource getDataSource();

	public void postDocumentUpdatedEvent(DocumentUpdatedEvent event);
	
	public void postElementUpdatedEvent(ElementUpdatedEvent event);
	
	public void postAttributeUpdatedEvent(AttributeUpdatedEvent event);

	public void endActivity(ActivityHandle handle);

	public DocumentActivity createDocumentActivity(DocumentSelector documentSelector);

	public AppUsageActivity createAppUsageActivity(String auid);
	
}
