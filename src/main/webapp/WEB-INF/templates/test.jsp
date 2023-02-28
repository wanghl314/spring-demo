<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
  <head>
    <title>test</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <script type="text/javascript" src="<%=path%>/scripts/jquery.js"></script>
  </head>
  <body>
    <h2>Test!</h2>
    <button type="button" class="test-button">click me</button>
    <script type="text/javascript">
      $(function() {
        $(".test-button").click(function() {
          alert("I am a button!");
        });
      });
    </script>
  </body>
</html>
