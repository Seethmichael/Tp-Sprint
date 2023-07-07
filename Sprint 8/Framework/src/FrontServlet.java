package etu1870.framework.servlet;

import etu1870.framework.Mapping;
import etu1870.framework.ModelView;
import etu1870.utils.Utils;
import java.util.Enumeration;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class FrontServlet extends HttpServlet {

    HashMap<String, Mapping> mappingUrls;   // liste des methodes annotees avec leurs classes
                                            // <l'annotation et le Mapping correspondant>
    String context;

    public void init() throws ServletException {
        try{
            this.context = this.getInitParameter("context");
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
            out.println("BIENVENUE");
            // recupeartion de l'URL de la requete
            String url = req.getRequestURL().toString();

            // recuperation de l'attr URL de l'annotation Urls
            String slug = Utils.getPathFromURL(url, this.context);
            //out.println("URL: "+ url);
            //out.println("slug: "+slug);

            //liste des annotations disponibles, classes et methodes correspondantes 
            //for(Map.Entry<String, Mapping> entry : this.mappingUrls.entrySet()){
                //out.println("Annotation: "+ entry.getKey()+"\tMethode : "+entry.getValue().getMethod()+"\tClasse: "+entry.getValue().getClassName());
            //}

            // recuperation du mapping correspondant a l'URL donnee
            // pour avoir le nom de classe et de methode a invoquer
            // et quelle est la classe d'appartenance

            Mapping map = this.mappingUrls.get(slug);
            if(map == null) out.println("URL non atteignable car Annotation inconnue");
            else{
                Object instance = FrontServlet.getInstance(map);
                Class<?> classe = instance.getClass();
                Method[] methods = classe.getDeclaredMethods();
        
                // donner aux attributs de l'instance leur valeurs
                FrontServlet.setAttributes(instance, req, classe, methods);

                // recherche et invocation
                // de la methode du mapping
                // si elle est existante
                Method methode = null;
                try {
                    // cas d'une methode sans arguments
                    methode = classe.getMethod(map.getMethod());
                    
                } catch (Exception e1) {
                    // cas des methodes demandant certains arguments
                    for(Method courant: methods){
                        if (courant.getName().equals(map.getMethod())) methode = courant;
                    }
                }
                
                
                Object result = FrontServlet.invoke(classe.cast(instance), req, methode);  
                
                // Passage de donnees vers une vue
                if(result instanceof ModelView == true){
                    ModelView mv =  (ModelView)result;

                    // ajout de toutes les donnees a
                    // a passer vers la vue
                    for(Map.Entry<String, Object> entree : mv.getData().entrySet()){
                        req.setAttribute(entree.getKey(), entree.getValue());
                    }

                    RequestDispatcher dispat = req.getRequestDispatcher(mv.getView());
                    dispat.forward(req,res);  
                }
            }    
        }catch( Exception e ){
            e.printStackTrace();
        }
        
    }


    // invocation d'Une methode d'un modele donne dont on connait le nom, l'instance appelante et la liste des arguments
    public static Object invoke(Object instance, HttpServletRequest req, Method methode) throws Exception{
        System.out.println("----------------------------------\nMETHODE: "+ methode.getName());
        // liste de tous les noms de parametres de la requete
        //Enumeration<String> parameterNames = req.getParameterNames();

        // liste des arguments formels de la fonction
        Parameter[] argForms = methode.getParameters();
        Parameter arg;

        // liste de tous les arguments a passer a la fin
        Object[] args = new  Object[argForms.length];
        
        //Valeur de l'argument courant
        String argVal = null;
        String[] argValues;

        //instance castee vers le type attendu
        Object argValCast; 

        // CONSTRUCTION DE LA LISTE DES ARGUMENTS EFFECTIFS
        // parcours de la liste des arguments formels de la fonction
        for(int i = 0; i < args.length; i++){
            arg = argForms[i];
            System.out.println("NOMS D'args: "+ arg.getName()+ "\n" + "TYPE ATTENDU: " + arg.getType().getName());
            argValues = req.getParameterValues(arg.getName());

            //RECUPERATION DE LA BONNE INSTANCE DE CHAQUE ARGUMENT SELON LE TYPE D'ARGUMENT ATTENDU
            try{
                // cas ou le parametre est present dans la requete
                // mais le champ n'est pas rempli
                argVal = argValues[0];
                argValCast = FrontServlet.cast(argVal, arg.getType());
                System.out.println("VALEUR D'args: "+ argValCast+ "\n" + "TYPE PRESENT: " + argValCast.getClass().getName());
                argVal = null;
            }
            catch(Exception e){
                // cas OU le parametre ne figure pas dans la requete
                // envoi de NULL vers la methode a invoquer
                argValCast = FrontServlet.cast(argVal, arg.getType());
            }   

            args[i] = argValCast;
        }

        //INVOCATION DE LA METHODE D'INSTANCE
        return methode.invoke(instance, args);

    }

    public static Object cast(String value, Class<?> type){

            // caster l'argument vers le type attendu
            try{
                // pour les types ayant la methode valueOf(String s)
                return type.getDeclaredMethod("valueOf", String.class).invoke(null, value); 
            } 
            catch (java.lang.reflect.InvocationTargetException eg){
                //eg.printStackTrace();
                return null;
            }
            catch(Exception e){
                //e.printStackTrace();
            }
                // arguments de types primitifs

            try {
                String nomDeType = type.getSimpleName();
                //int 
                if(nomDeType.compareToIgnoreCase("int") == 0){
                    try{
                        return Integer.parseInt(value); 
                    }
                    catch(Exception e4){
                        e4.printStackTrace();
                        return 0;
                    }
                }

                // double
                else if(nomDeType.compareToIgnoreCase("double") == 0){
                    try{
                        return Double.parseDouble(value);                     
                    }
                    catch(Exception e5){
                        //e5.printStackTrace();
                        return Double.parseDouble("0.0");
                    }                                  

                }

                //float
                else if(nomDeType.compareToIgnoreCase("float") == 0){
                    try{
                        return Float.parseFloat(value);                     
                    }
                    catch(Exception e6){
                        //e6.printStackTrace();
                        return Float.parseFloat("0.0");
                    }
                }

                // boolean            
                else if(nomDeType.compareToIgnoreCase("boolean") == 0){
                    try{
                        return Boolean.parseBoolean(value);                     
                    }
                    catch(Exception e7){
                        //e7.printStackTrace();
                        return false;
                    }
                } 
            }
            catch (Exception e2) {}
            
            //String
            return value;
    }

            

    // donner des valeurs aux attributs 
    public static void setAttributes(Object instance, HttpServletRequest req, Class<?> classe, Method[] methods) throws Exception {
        // liste des attributs de la classe
        Field[] attrList = classe.getDeclaredFields();

        String attrName;    // nom de l'attribut courant
        String attrSetterName;  // nom du setter correspondant
        String[] argVal;  // valeur eventuelle de cet attribut dans les variables de la requete

        // parcours de la liste des attributs
        for (Field attr : attrList) {
            attrName = attr.getName();

            // formation du nom du setter correspondant a cet attribut
            attrSetterName = "set" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);

            argVal = req.getParameterValues(attrName);

            if (argVal != null) {
                for (Method method : methods) {
                    if (method.getName().equalsIgnoreCase(attrSetterName)) {
                        FrontServlet.invoke(instance, req, method);
                    }
                }
            }
        }
    }
        
    public static Object getInstance(Mapping map) throws Exception{
        try{
            Class classe = Class.forName(map.getClassName());
            return classe.cast(classe.newInstance());
        }
        catch(Exception e){
            throw e;
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

