package etu1870.framework.servlet;

import etu1870.framework.Mapping;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet {

    HashMap<String, Mapping> mappingUrls;
    
    protected void processRequest(HttpServletRequest req,
    HttpServletResponse res) throws IOException, ServletException{
        PrintWriter out = res.getWriter();
        out.print("Test Sprint 2 ");
        
    }

    public void doGet(HttpServletRequest req,
                      HttpServletResponse res)
        throws IOException, ServletException{
        processRequest(req, res);
    }

    public void doPost(HttpServletRequest req,
                      HttpServletResponse res)
        throws IOException, ServletException{
        processRequest(req, res);
    }
}

