<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%
	session.invalidate();
    // ids的退出地址，ids6.wisedu.com为ids的域名  authserver为ids的上下文，logout为固定值
	String casLogoutURL = "http://ids6.wisedu.com/authserver/logout";
    // service后面带的参数为应用的访问地址，需要使用URLEncoder进行编码
    String redirectURL=casLogoutURL+"?service="+URLEncoder.encode("http://172.16.4.78:8080/testjava/caslogin.jsp");
	response.sendRedirect(redirectURL);

%>
