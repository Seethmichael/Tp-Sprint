<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <%
        if(session.getAttribute("connectedProfile") != null){
            out.print("Conncte en tant que: "+ session.getAttribute ("connectedProfile"));
    %>
    <div>
        <a href="invalidateSession.do">Se deconnecter(#SPRINT 15: invalidateSession)</a>
    </div>

    <div>
        <a href="logout.do">Se deconnecter(#SPRINT 15: session.removeAttrribute)</a>
    </div>
    <%

        }
        else{
    %>

    <form action="login.do">
        <P>CONNEXION</P>
        <p>Profile: <input type="text" name="profile"></p>
        <input type="submit" value="Connexion (#SPRINT11: ajout de donnees a la sesssion)">
    </form>
    <%
        }
    %>

    <div>
        <P>Entrer des donnees sur un employe</P>
        <a href="empForm.jsp">Enter les donnes</a>
    </div>

    <div>
        <P>Recuperer un mv sous Forme de JSON</P>
        <a href="mvToJson.do">lancer</a>
    </div>

    <div>
        <P>Recuperer un objet de retour sous forme de JSON</P>
        <a href="obejctToJson.do">lancer</a>
    </div>
</body>
</html>
    
