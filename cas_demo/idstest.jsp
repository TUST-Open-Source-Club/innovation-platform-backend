<%@page import="java.util.Map"%>
<%@ page
        import="java.security.Principal" %>
<%@ page
        import="org.jasig.cas.client.authentication.AttributePrincipal" %>
<%@ page
        import="java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%
//    cas-client-3.2.1版本集成
	String uid = request.getRemoteUser();
    String cn = "";
	String user_name = "";
    Principal principal = request.getUserPrincipal();
    		if(principal!=null && principal instanceof AttributePrincipal){
    			AttributePrincipal aPrincipal = (AttributePrincipal)principal;
    //获取用户信息中公开的Attributes部分
    			Map<String, Object> map = aPrincipal.getAttributes();
    			/*Iterator<String> it = map.keySet().iterator();
    			while (it.hasNext()) {
    				String k = it.next();
    				response.getWriter().printf("%s:%s\r\n", k, map.get(k));
    			}*/
				cn = (String)map.get("cn"); 
                user_name = (String)map.get("user_name");
    		}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title></title>
	</head>
	
	<body>
		Hello,<%=uid%>! Welcome <%=cn %> &nbsp;
		用户的uid: <%=uid%> <br/>
		界面上操作的姓名cn: <%=cn %> <br/>
		默认传递的user_name: <%=user_name %><br/>

		可以在更新前和更新后进行对比。<br/>

	</body>
</html>