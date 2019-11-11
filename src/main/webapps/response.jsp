<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.openlogic.com/struts-tags" prefix="s" %>
   
<html>
<head>
  <title>My Struts</title>
</head>
<body>
  <h1>Whose Struts?</h1>
  <s:form action="MyStruts" >
    <s:textfield name="username" label="Name: " />
    <s:submit value="Send" />
  </s:form>
</body>
</html>
