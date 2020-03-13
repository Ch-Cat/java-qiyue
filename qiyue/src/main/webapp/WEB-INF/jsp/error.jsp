<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<!--content='页面停留时间(这里单位是秒);url=跳转至另一页面'  -->
<meta http-equiv='refresh' content='3;url=begin'>
<script type='text/javascript'>
var i=3;
function getTime(){
document.getElementById('num').innerHTML="<font color='red'>"+i+"</font>";
i-=1;
var x=setTimeout('getTime()',1000)//1000毫秒=1秒
if(i<=0){
clearTimeout(x);
}
}
window.οnlοad=getTime;//开始执行倒计时
</script>
<title>错误</title>
</head>
<body>
<br>
	<center>
		<h2>
			<br/>
		发生错误！！页面将在<span id='num' style='display=inline;'>3</span>秒后跳转至主页
		</h2>
	</center>

</body>
</html>