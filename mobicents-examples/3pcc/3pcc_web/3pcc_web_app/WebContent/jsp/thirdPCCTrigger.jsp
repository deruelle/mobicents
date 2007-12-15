<%@ page contentType="text/html; charset=UTF-8" %>

<html>
   <head>
      <title>Jayway's SIP 3PCC demo app</title>
      <%-- <script src="scripts.js" language="JavaScript" type="text/javascript"></script> --%>
   </head>
   <body>
    
       <jsp:include page="header.jsp" />
    	<br clear="left">
    	<br><br><br>
       <form style="margin-bottom:0;" action="callControlServlet" method="post">
       <table>
         <tr><td><b>Caller: </b><td><input type="text" style="{font-family:palatino linotype; font-size:0.85em; width:250px; text-align:left;}"
                  value="sip:caller@domain" name="caller"/>
         <td><br>
         <tr><td><b>Callee: <b><td><input type="text" style="{font-family:palatino linotype; font-size:0.85em; width:250px; text-align:left;}"
                  value="sip:callee@domain" name="callee"/>
               <input type="hidden" name="action" value="makeCall"/>
         <td><br>
         <tr><td><input type="submit" value="Launch Call"/> 
      </form> <%-- End entry form --%>  
               </table>

       <br><br><br><br><br><br><br><br><br><br><br><br><br>
       <jsp:include page="footer.jsp" />
      
   </body>
</html>   
