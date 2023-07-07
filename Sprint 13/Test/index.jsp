<%
    if(session.getAttribute("connectedProfileKey") != null){
        out.print("Conncte en tant que: "+ session.getAttribute("connectedProfileKey"));
    }
    else{
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <form action="login">
        <p>Profile: <input type="text" name="profile"></p>
        <input type="submit" value="Connexion">
    </form>
</body>
</html>
<%
    }
%>