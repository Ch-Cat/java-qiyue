<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="UTF-8">
<title>首页</title>
</head>
<body>
<a href="upLoadpage">文件上传</a>
<c:set value="${requestScope.X_SID }" scope="session" var="SID"/>
<c:set value="${requestScope.X_Signature }" scope="session" var="Signature"/>
<table  border='1' cellspacing='0' align='center'>
		<tr>
			<td>文件名</td>
			<td>大小</td>
			<td>类型</td>
			<td>上传时间</td>
			<td>存储路径</td>
			<td>数字信封</td>
			<td>文件下载</td>
		</tr>
		<c:forEach var="fileinfo" items="${requestScope.list}">
			<tr>
				<td>${fileinfo.f_name}</td>
				<td>${fileinfo.f_size}</td>
				<td>${fileinfo.f_type}</td>
				<td>${fileinfo.f_time}</td>
				<td>${fileinfo.f_url}</td>
				<td>${fileinfo.f_enve}</td>
				<td>
					<a href="${pageContext.request.contextPath}/download?filename=${fileinfo.f_name}">下载</a>
				</td>
			</tr>
		</c:forEach>

</table>
</body>
</html>