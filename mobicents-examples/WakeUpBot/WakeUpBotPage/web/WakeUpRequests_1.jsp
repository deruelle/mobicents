<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
 <%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
 <%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
 <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
 <%@taglib uri="http://ee.pw.edu.pl/baranowb_kijanowj" prefix="bb"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WakeUpRequests Page</title>
        <link href="/WakeUpBotPage/styles.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>

        <h1>WakeUpRequests</h1>
        <f:view>
            <h:form id="WakeUpRequestForm">
                <h:panelGrid id="formGrid" columns="1" columnClasses="columnRowOdd">
                <h:panelGrid id="mainGrid" columns="2" columnClasses="menu,columnRowEven" headerClass="columnRowOdd">
                    <f:facet name="header">
                        <h:outputText id="HeaderText" value="Choose date to be awakened by WakeUpBot ;]"/>
                    </f:facet>
                    <h:panelGroup id="firstGroup">
                        <h:outputText id="daysPrompt" value="Pickup year/month/day"/>
                    </h:panelGroup>
                    <bb:inputDatePicker id="dayPicker" value="#{datacolector.yearMonthDay}"/> 
                    <h:outputText id="hoursPrompt" value="Set hour/minute/second"/>
                    <bb:inputHourPicker id="hourPicker" value="#{datacolector.hourMinuteSecond}"/> 
                </h:panelGrid>
                <h:inputText id="user" required="true" value="#{datacolector.UID}"/>
                <h:commandButton id="postRequest" value="Create request" action="#{datacolector.sendRequest}"/>
                </h:panelGrid>
            </h:form>
        </f:view>
        <%--
        This example uses JSTL, uncomment the taglib directive above.
        To test, display the page like this: index.jsp?sayHello=true&name=Murphy
        --%>
        <%--
        <c:if test="${param.sayHello}">
        <!-- Let's welcome the user ${param.name} -->
        Hello ${param.name}!
        </c:if>
        --%>
    
    </body>
</html>
