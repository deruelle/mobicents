<%@ page contentType="text/html; charset=UTF-8" %>

<html>
   <head>
      <title>Jayway's SIP 3PCC demo app</title>
      <script type="text/javascript">
      		function stopBlinking() {	
      			document.getElementById("callStatusTD").style.textDecoration = "none";
      			document.getElementById("blinkingTD").innerHTML = "";
      			document.getElementByName("call_service_decoration_field").value = "none";
      		}
      </script>		 
   </head>
   <body>  
       <jsp:include page="header.jsp" />
       <br clear="left">
       <br><br><br>
       <table>
       <tr><td><img id="callerImage" width="72" height="75" src="resources/images/states/initial.gif" ALIGN="center"><td><h2>Caller: <%= request.getParameter("caller")%> </h2>
       <br>
       <tr><td><img id="calleeImage" width="72" height="75" src="resources/images/states/trying.gif" ALIGN="center"><td><h2>Callee: <%= request.getParameter("callee")%></h2>
       </table>
		
	   <br>
	  		
	   <table>
       <tr><td>
	       <form style="margin-bottom:0;" action="callControlServlet" method="post" id="cancelRequestForm">
				<input type="hidden" name="action" value="cancelRequest"/>
		      <input type="hidden" name="call_service_decoration_field" value=" <%= session.getAttribute("call_service_decoration") %>">
		       <input type="submit" value="Cancel call request" id="cancelRequestButton"/> 
	      </form> <%-- End entry form --%>  
	      <td>
	       <form style="margin-bottom:0;" action="callControlServlet" method="post" id="endCallForm">
			   <input type="hidden" name="action" value="endCall"/>
		       <input type="submit" value="End call" id="endCallButton"/> 
	      </form> <%-- End entry form --%>   
	   </table>
	   
	   <br><br>	
	   
     <table>
          <tr> <td>Call status:</td> <td id="status_id" style="{color:#B2100B}">DEFAULT_STATUS</td>
          <td><a href="javascript:sendRequest()">[Update state]</a></td>
       </table>   
	   
	   <p>
	   
	   <table>
          <tr> 
          <td>Call service:</td> 
          <td id="callStatusTD" style="{color:#B2100B;text-decoration:<%= session.getAttribute("call_service_decoration") %>}"> <%= request.getAttribute("call_service") %> </td>
          <% if ("blink".equals(session.getAttribute("call_service_decoration"))) { %>
          	<td id="blinkingTD">&nbsp;&nbsp;&nbsp;<input type="button" value="Stop blinking!!!" onclick="stopBlinking();"/>  
          <% } %>
       </table>
	   		 
	   </div>
	   		 
       <script type="text/javascript"> 
       		var pollingActive = true;
       		var timeoutId;
       
	        <%-- Initial --%>        
	         window.onload = function() {
		          sendRequest(); //Send the first AJAX request
		      }
		      
	      
          function createRequestObject() {
             var httpRequest;
               <%-- Get type of browser --%> 
               var browser = navigator.appName;
                        
               <%-- Creating the request object --%>      
             if (browser == "Microsoft Internet Explorer") {
                 httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
             } else {
                 httpRequest = new XMLHttpRequest();
             }
             return httpRequest;
         }

         <%-- Create the request object --%> 
         var httpRequest = createRequestObject();

         function sendRequest() {
            <%-- Set the event handler method that will run on every state change --%>
            httpRequest.onreadystatechange = processResponseChange;
            
            <%-- Specify the request method and url for the open method --%>
            <%-- httpRequest.open("GET", "getStatus?callId="+callId); --%>
            httpRequest.open("GET", "callControlServlet?action=getCallInformation");
            
            <%-- Send the request --%>
            httpRequest.send(null);
         }
         
         function processResponseChange() {
             if (httpRequest.readyState == 4) {
                   if (httpRequest.status == 200) {
                     // process the response
                     updateCallState();
                 } else {
                       // show error
                       //alert("Error retrieving call state: " + httpRequest.status);
                       //alert("httpRequest.readyState = " + httpRequest.readyState);
                 }
                 	if(pollingActive == true)
               			loopRequest(1000); //Invoke polling of status
             }
         }
         
         function loopRequest(delay) {
         	clearTimeout(timeoutId);
          	timeoutId = setTimeout('sendRequest();',delay); 
          	return true;
         }
         
         
           function updateCallState() {
             <%-- Expected response: --%>
            <%-- 
               <status>100 - Trying</status> 
            --%>
   
            <%-- Get status from xml response --%>
            
            <%-- output("updateCallState, httpRequest.responseXML = " + httpRequest.responseXML);
            alert(httpRequest.responseXML.getElementsByTagName('root'));   
            alert("[0] = " + httpRequest.responseXML.getElementsByTagName('root'));
            var roots = httpRequest.responseXML.getElementsByTagName('root');
            alert("roots.length = "+roots.length);
            alert("httpRequest.responseText = " + httpRequest.responseText);
            alert(httpRequest.responseXML.getElementsByTagName('root')[0]);
            alert(httpRequest.responseXML.getElementsByTagName('root')[0].childNodes[0]);
            alert(httpRequest.responseXML.getElementsByTagName('root')[0].firstChild.nodeValue);
            var theStatus = httpRequest.responseXML.getElementsByTagName('root')[0].childNodes[0].nodeValue;
            --%>
            var theStatus = httpRequest.responseText;
            <%-- Get HTML element to set the status for --%>       
            var firstNameElement = document.getElementById("status_id");
            <%-- set the status value in the HTML element --%>
            firstNameElement.innerHTML = theStatus;  
            updateStatusImages(theStatus);   
         }         
   		
   		<%-- Update the page according to the new status --%>
   		function updateStatusImages(state) {
   			var callerImageLink;
   			var calleeImageLink;
				
   			if(state.indexOf("InitialState") != -1 ) {
				callerImageLink="resources/images/states/initial.gif";
				calleeImageLink="resources/images/states/initial.gif";
				document.getElementById("cancelRequestButton").disabled=false;
				document.getElementById("endCallButton").disabled=true;
   			} else if (state.indexOf("CalleeTryingState") != -1) {	
   				callerImageLink="resources/images/states/initial.gif";
				calleeImageLink="resources/images/states/trying.gif";
   				document.getElementById("cancelRequestButton").disabled=false;
				document.getElementById("endCallButton").disabled=true;			
   			} else if (state.indexOf("CalleeRingingState") != -1) {		
			   	callerImageLink="resources/images/states/initial.gif";
				calleeImageLink="resources/images/states/ringing.gif";
				document.getElementById("cancelRequestButton").disabled=false;
				document.getElementById("endCallButton").disabled=true;			
   			} else if (state.indexOf("CallerInvitedState") != -1) {	
				callerImageLink="resources/images/states/initial.gif";
				calleeImageLink="resources/images/states/established.gif";
			} else if (state.indexOf("CallerTryingState") != -1) {	
		  		callerImageLink="resources/images/states/trying.gif";
				calleeImageLink="resources/images/states/established.gif";			
   			} else if (state.indexOf("CallerRingingState") != -1) {				
		  		callerImageLink="resources/images/states/ringing.gif";
				calleeImageLink="resources/images/states/established.gif";	
   			} else if (state.indexOf("SessionEstablishedState") != -1) {
		  		callerImageLink="resources/images/states/established.gif";
				calleeImageLink="resources/images/states/established.gif";					
				document.getElementById("cancelRequestButton").disabled=true;
				document.getElementById("endCallButton").disabled=false;
   			} else if (state.indexOf("UATerminationState") != -1) {			
			   	callerImageLink="resources/images/states/terminating.gif";
				calleeImageLink="resources/images/states/terminating.gif";	
				document.getElementById("cancelRequestButton").disabled=true;
				document.getElementById("endCallButton").disabled=true;
   			} else if (state.indexOf("TerminationState") != -1) {
		   		callerImageLink="resources/images/states/initial.gif";
				calleeImageLink="resources/images/states/initial.gif";	
				document.getElementById("cancelRequestButton").disabled=true;
				document.getElementById("endCallButton").disabled=true;
				clearTimeout(timeoutId);
				pollingActive = false;
   			} else if (state.indexOf("ExternalTerminationCalleeState") != -1) {
		   		callerImageLink="resources/images/states/established.gif";
				calleeImageLink="resources/images/states/terminating.gif";
 				document.getElementById("cancelRequestButton").disabled=true;
				document.getElementById("endCallButton").disabled=false;
				clearTimeout(timeoutId);
   			} else if (state.indexOf("ExternalTerminationCallerState") != -1) {
		   		callerImageLink="resources/images/states/terminating.gif";
				calleeImageLink="resources/images/states/established.gif";	
 				document.getElementById("cancelRequestButton").disabled=true;
				document.getElementById("endCallButton").disabled=false;
				clearTimeout(timeoutId);
   			} else if (state.indexOf("ExternalCancellationState") != -1) {
		   		callerImageLink="resources/images/states/terminating.gif";
				calleeImageLink="resources/images/states/terminating.gif";	
 				document.getElementById("cancelRequestButton").disabled=true;
				document.getElementById("endCallButton").disabled=false;
				clearTimeout(timeoutId);
				pollingActive = false;
			} else {
   				alert('Internal error: Unknown status received: '+ state);
   			}
   			
   			 <%-- Update status image for caller --%>       
            document.getElementById("callerImage").src = callerImageLink;
           
   			 <%-- Update status image for caller --%>       
            document.getElementById("calleeImage").src = calleeImageLink;
            
   		}
            
      </script>   
      <br><br><br><br>
      
       <jsp:include page="footer.jsp" />
      
   </body>
</html>   
