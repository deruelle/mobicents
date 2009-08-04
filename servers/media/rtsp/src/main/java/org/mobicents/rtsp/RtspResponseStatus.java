package org.mobicents.rtsp;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public class RtspResponseStatus extends HttpResponseStatus {

	/**
	 * 250 Low on Storage Space
	 */
	public static final RtspResponseStatus LOW_STORAGE_SPACE = new RtspResponseStatus(
			250, "Low on Storage Space");

	/**
	 * 302 Moved Temporarily
	 */
	public static final RtspResponseStatus MOVED_TEMPORARILY = new RtspResponseStatus(
			320, "Moved Temporarily");

	/**
	 * 451 Parameter Not Understood
	 */
	public static final RtspResponseStatus PARAMETER_NOT_UNDERSTOOD = new RtspResponseStatus(
			451, "Parameter Not Understood");

	/**
	 * 452 Conference Not Found
	 */
	public static final RtspResponseStatus CONFERENCE_NOT_FOUND = new RtspResponseStatus(
			452, "Conference Not Found");

	/**
	 * 453 Not Enough Bandwidth
	 */
	public static final RtspResponseStatus NOT_ENOUGH_BANDWIDTH = new RtspResponseStatus(
			453, "Not Enough Bandwidth");

	/**
	 * 454 Session Not Found
	 */
	public static final RtspResponseStatus SESSION_NOT_FOUND = new RtspResponseStatus(
			454, "Session Not Found");

	/**
	 * 455 Method Not Valid in This State
	 */
	public static final RtspResponseStatus METHOD_NOT_VALID = new RtspResponseStatus(
			455, "Method Not Valid in This State");

	/**
	 * 456 Header Field Not Valid for Resource
	 */
	public static final RtspResponseStatus HEADER_FIELD_NOT_VALID = new RtspResponseStatus(
			456, "Header Field Not Valid for Resource");

	/**
	 * 457 Invalid Range
	 */
	public static final RtspResponseStatus INVALID_RANGE = new RtspResponseStatus(
			457, "Invalid Range");

	/**
	 * 458 Parameter Is Read-Only
	 */
	public static final RtspResponseStatus PARAMETER_IS_READONLY = new RtspResponseStatus(
			458, "Parameter Is Read-Only");

	/**
	 * 459 Aggregate operation not allowed
	 */
	public static final RtspResponseStatus AGGREGATE_OPERATION_NOT_ALLOWED = new RtspResponseStatus(
			459, "Aggregate operation not allowed");

	/**
	 * 460 Only Aggregate operation allowed
	 */
	public static final RtspResponseStatus ONLY_AGGREGATE_OPERATION_ALLOWED = new RtspResponseStatus(
			460, "Only Aggregate operation allowed");

	/**
	 * 461 Unsupported transport
	 */
	public static final RtspResponseStatus UNSUPPORTED_TRANSPORT = new RtspResponseStatus(
			461, "Unsupported transport");

	/**
	 * 462 Destination unreachable
	 */
	public static final RtspResponseStatus DESTINATION_UNREACHABLE = new RtspResponseStatus(
			462, "Destination unreachable");

	/**
	 * 505 RTSP Version not supported
	 */
	public static final RtspResponseStatus RTSP_VERSION_NOT_SUPPORTED = new RtspResponseStatus(
			505, "RTSP Version not supported");

	/**
	 * 551 Option not supported
	 */
	public static final RtspResponseStatus OPTION_NOT_SUPPORTED = new RtspResponseStatus(
			551, "Option not supported");

	public RtspResponseStatus(int code, String reasonPhrase) {
		super(code, reasonPhrase);
	}

}
