package org.openxdm.xcap.server.slee;

import java.io.InputStream;

import javax.slee.SbbLocalObject;

import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.error.BadRequestException;
import org.openxdm.xcap.common.error.CannotDeleteConflictException;
import org.openxdm.xcap.common.error.ConflictException;
import org.openxdm.xcap.common.error.ConstraintFailureConflictException;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.MethodNotAllowedException;
import org.openxdm.xcap.common.error.NotFoundException;
import org.openxdm.xcap.common.error.PreconditionFailedException;
import org.openxdm.xcap.common.error.SchemaValidationErrorConflictException;
import org.openxdm.xcap.common.error.UniquenessFailureConflictException;
import org.openxdm.xcap.common.error.UnsupportedMediaTypeException;
import org.openxdm.xcap.common.uri.ResourceSelector;
import org.openxdm.xcap.server.etag.ETagValidator;
import org.openxdm.xcap.server.result.ReadResult;
import org.openxdm.xcap.server.result.WriteResult;

/**
 * OPENXDM Request Processor interface. Service logic to process the http requests.
 * @author Eduardo Martins
 *
 */
public interface RequestProcessorSbbLocalObject extends SbbLocalObject {
	
	public WriteResult delete(ResourceSelector resourceSelector, ETagValidator eTagValidator, AppUsage appUsage, String xcapRoot) throws NotFoundException, InternalServerErrorException, BadRequestException, CannotDeleteConflictException, PreconditionFailedException, MethodNotAllowedException, SchemaValidationErrorConflictException, UniquenessFailureConflictException, ConstraintFailureConflictException;
		
	public ReadResult get(ResourceSelector resourceSelector, AppUsage appUsage) throws NotFoundException, InternalServerErrorException, BadRequestException;
	
	public WriteResult put(ResourceSelector resourceSelector, String mimetype, InputStream contentStream, ETagValidator eTagValidator, AppUsage appUsage, String xcapRoot) throws ConflictException, MethodNotAllowedException, UnsupportedMediaTypeException, InternalServerErrorException, PreconditionFailedException, BadRequestException;

}
