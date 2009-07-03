package org.openxdm.xcap.server.slee;

import java.io.InputStream;

import javax.slee.SbbLocalObject;

import org.openxdm.xcap.common.error.BadRequestException;
import org.openxdm.xcap.common.error.CannotDeleteConflictException;
import org.openxdm.xcap.common.error.ConflictException;
import org.openxdm.xcap.common.error.ConstraintFailureConflictException;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.MethodNotAllowedException;
import org.openxdm.xcap.common.error.NotAuthorizedRequestException;
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
 * Request Processor interface. Service logic to process the xcap requests.
 * 
 * @author Eduardo Martins
 * 
 */
public interface RequestProcessorSbbLocalObject extends SbbLocalObject {

	/**
	 * 
	 * @param resourceSelector
	 * @param eTagValidator
	 * @param xcapRoot
	 * @param authenticatedUser
	 * @return
	 * @throws NotFoundException
	 * @throws InternalServerErrorException
	 * @throws BadRequestException
	 * @throws CannotDeleteConflictException
	 * @throws PreconditionFailedException
	 * @throws MethodNotAllowedException
	 * @throws SchemaValidationErrorConflictException
	 * @throws UniquenessFailureConflictException
	 * @throws ConstraintFailureConflictException
	 * @throws NotAuthorizedRequestException
	 */
	public WriteResult delete(ResourceSelector resourceSelector,
			ETagValidator eTagValidator, String xcapRoot,
			String authenticatedUser) throws NotFoundException,
			InternalServerErrorException, BadRequestException,
			CannotDeleteConflictException, PreconditionFailedException,
			MethodNotAllowedException, SchemaValidationErrorConflictException,
			UniquenessFailureConflictException,
			ConstraintFailureConflictException, NotAuthorizedRequestException;

	/**
	 * 
	 * @param resourceSelector
	 * @param authenticatedUser
	 * @return
	 * @throws NotFoundException
	 * @throws InternalServerErrorException
	 * @throws BadRequestException
	 * @throws NotAuthorizedRequestException
	 */
	public ReadResult get(ResourceSelector resourceSelector,
			String authenticatedUser) throws NotFoundException,
			InternalServerErrorException, BadRequestException,
			NotAuthorizedRequestException;

	/**
	 * 
	 * @param resourceSelector
	 * @param mimetype
	 * @param contentStream
	 * @param eTagValidator
	 * @param xcapRoot
	 * @param authenticatedUser
	 * @return
	 * @throws ConflictException
	 * @throws MethodNotAllowedException
	 * @throws UnsupportedMediaTypeException
	 * @throws InternalServerErrorException
	 * @throws PreconditionFailedException
	 * @throws BadRequestException
	 * @throws NotAuthorizedRequestException
	 */
	public WriteResult put(ResourceSelector resourceSelector, String mimetype,
			InputStream contentStream, ETagValidator eTagValidator,
			String xcapRoot, String authenticatedUser)
			throws ConflictException, MethodNotAllowedException,
			UnsupportedMediaTypeException, InternalServerErrorException,
			PreconditionFailedException, BadRequestException,
			NotAuthorizedRequestException;

}
