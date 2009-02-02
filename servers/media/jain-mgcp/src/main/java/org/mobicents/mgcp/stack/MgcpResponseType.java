package org.mobicents.mgcp.stack;

public enum MgcpResponseType {

	ResponseAcknowledgement(0,99), ProvisionalResponse(100,199), SuccessResponse(200,299),TransientError(400,499),PermanentError(500,599), PackageSpecific(800,899);
	
	
	private int lowRange=-1;
	private int highRange=-1;
	
	private MgcpResponseType(int low, int high)
	{
		this.lowRange=low;
		this.highRange=high;
	}
	
	
	public static MgcpResponseType getResponseTypeFromCode(int responseCode)
	{
		if(responseCode>=0 && responseCode<=99)
		{
			return ResponseAcknowledgement;
		}else if(responseCode>=100 && responseCode<=199)
		{
			return ProvisionalResponse;
		}else if(responseCode>=200 && responseCode<=299)
		{
			return SuccessResponse;
		}else if(responseCode>=400 && responseCode<=499)
		{
			return TransientError;
		}else if(responseCode>=500 && responseCode<=599)
		{
			return PermanentError;
		}else if(responseCode>=800 && responseCode<=899)
		{
			return PackageSpecific;
		}else 
		{
			return null;
		}
		
		
		
		
	}
	
	
}
