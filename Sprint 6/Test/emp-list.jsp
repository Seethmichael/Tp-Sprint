<%@ page import="java.util.Vector, model.societe.Emp" %>

<%
    Vector<Emp> liste = (Vector<Emp>)request.getAttribute("liste");
    for(Emp emp : liste){
        out.println("EMPLOYE: "+emp.getNom());
    }
%>