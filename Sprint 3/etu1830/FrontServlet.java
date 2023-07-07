package etu1870.framework.servlet;

import etu1870.framework.Mapping;
import etu1870.utils.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet {

    HashMap<String, Mapping> mappingUrls;   // liste des methodes annotees avec leurs classes
                                            // <l'annotation et le Mapping correspondant>

    public void init() throws ServletException {
        try{
            String p = "";
            this.mappingUrls =  Utils.getUrlsAnnotedMethods(Utils.getClasses( null  , p ));
        }catch( Exception e ){
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest req,
    HttpServletResponse res) throws IOException, ServletException{
        try{
            PrintWriter out = res.getWriter();
            // parcour de mappingUrls et affichage du contenu
            for(Map.Entry<String, Mapping> entry : this.mappingUrls.entrySet()){
                out.println("Annotation: "+ entry.getKey()+"\tMethode : "+entry.getValue().getMethod()+"\tClasse: "+entry.getValue().getClassName());
            }
           

            
        }catch( Exception e ){
            e.printStackTrace();
        }
        
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

