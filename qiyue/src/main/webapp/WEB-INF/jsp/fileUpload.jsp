<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
<title>文件上传</title>
<link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" 
integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
<script>
	//判断是否填写上传人并已选择上传文件
	function check(){
		var name=document.getElementById("name").value;
		var file=document.getElementById("file").value;
		if(name==""){
			alert("填写上传人");
			return false;
		}
		if(file.lenght==0||file==""){
			alert("请选择上传文件");
			return false;
		}
		return true;
	}
</script>
</head>
<body>
	<form action="/qiyue/fileUpload" method="post" enctype="multipart/form-data" onsubmit="return check()">
		上传人:<input id="name" type="text" name="name" /><br/>
		请选择文件:<input id="file" type="file" name="uploadfile" multiple="multipale" /><br/>
		<input type="submit" value="上传" />
	</form>
</body>

</html>