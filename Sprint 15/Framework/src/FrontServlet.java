package etu1870.framework.servlet;

import etu1870.framework.Mapping;
import etu1870.framework.ModelView;
import etu1870.framework.FileUpload;
import etu1870.utils.Utils;
import etu1870.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
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
import javax.servlet.http.Part;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.google.gson.Gson;


@MultipartConfig
public class FrontServlet extends HttpServlet {

    HashMap<String, Object> singles;                        // liste des classes singleton
    HashMap<String, Mapping> mappingUrls;   // liste des methodes annotees avec leurs classes
                                            // <l'annotation et le Mapping correspondant>
    String context;
    String connectedProfileKey;
    String instanceNumberVariable;

    public void init() throws ServletException {
        try{
            this.instanceNumberVariable = this.getInitParameter("instanceNumberVariable");
            this.connectedProfileKey = this.getInitParameter("connectedProfileKey");
            this.context = this.getInitParameter("context");
            String p = "";
            Vector<Class<?>> classes  = Utils.getClasses( null  , p );
            this.mappingUrls =  Utils.getUrlsAnnotedMethods(classes);
            this.singles = Utils.getScopAnnotedClasses(classes);
        }catch( Exception e ){
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest req,
    HttpServletResponse res) throws IOException, ServletException{
        try{

            HttpSession session = req.getSession();
            PrintWriter out = res.getWriter();
            //out.println("BIENVENUE");
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
                Object instance = this.getInstance(map);
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

                Object[] args;
                Object result = null;

                try {
                    this.authenticate(methode, session);
                    this.needSession(methode,instance, classe, session);
                     args = FrontServlet.generateArgs(req, methode);
                     result = methode.invoke(classe.cast(instance),args);
                    
                } catch (Exception e1) {
                   out.print(e1);
                   //e1.printStackTrace();
                }
                
                //Object result = FrontServlet.invoke(classe.cast(instance), req, methode);  
                
                // Passage de donnees vers une vue
                if(result instanceof ModelView == true){
                    ModelView mv =  (ModelView)result;
                    this.invalidateSession(mv, session);
                    if(mv.getIsJson() == true){
                        String json = this.serialize(mv);
                         // Modification du Content-Type de la réponse en JSON
                        res.setContentType("application/json");
                        out.print(json);
                    }
                    else{
                        // ajout de toutes les donnees a
                        // a passer vers la vue (data et session)
                        FrontServlet.setRequestAttributes(req, mv);
                        FrontServlet.setSessionAttributes(session, mv);
                        
                        RequestDispatcher dispat = req.getRequestDispatcher(mv.getView());
                        dispat.forward(req,res);  
                    }
                    
                }
                if (methode.getAnnotation(IsJson.class) != null ){
                    String json = this.serialize(result);
                    res.setContentType("application/json");
                    out.print(json);
                }
            }    
        }catch( Exception e ){
            e.printStackTrace();
        }
        
    }

    // invalidation de session
    public void invalidateSession(ModelView mv, HttpSession session){
        if(mv.getIsInvalidate() == true){
            session.invalidate();
        }
        String[] toInvalidate = mv.getToInvalidate();
        if(toInvalidate != null){
            for(String index : toInvalidate){
                session.removeAttribute(index);
            }
        }
        
    }

    // serialisation d'un Objet sous forme de JSON
    public String serialize(Object mv){
        // Création de l'objet Gson
        Gson gson = new Gson();

        // Conversion de la HashMap en JSON
        String json = gson.toJson(mv);
        return json;
   }


    // serialisation des donnees d'une MV sous forme de JSON
    public String serialize(ModelView mv){
         // Création de l'objet Gson
         Gson gson = new Gson();

         // Conversion de la HashMap en JSON
         String json = gson.toJson(mv.getData());
         return json;
    }

    // transformation de la session sous forme de HashMap

    public HashMap<String, Object> serializeSession(HttpSession session){
        // Créez un HashMap pour stocker les paires clé-valeur
        HashMap<String, Object> hashMap = new HashMap<String, Object>();

        // Récupérez toutes les clés de la session
        Enumeration<String> keys = session.getAttributeNames();

        // Parcourez les clés et insérez-les dans le HashMap
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            Object value = session.getAttribute(key);
            hashMap.put(key, value);
        }
        
        return hashMap;
    }

    // verification si une methode est anotee Session et passage du contenu de la Session a l'instance appelante
    public void needSession(Method methode, Object instance, Class classe, HttpSession session){
        if(methode.getAnnotation(Session.class)!= null){
            try {
                classe.getDeclaredMethod("setSession", HashMap.class).invoke(instance, this.serializeSession(session));                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }

    // verification d'autorisation d'acces a une methode d'action
    public void authenticate(Method methode,   HttpSession session) throws Exception{
        Auth annotation = methode.getAnnotation(Auth.class) ;
        String profile = null;
        try{
            profile = annotation.profile();
        }
        catch(Exception e){}

        if (annotation != null && profile.equals(session.getAttribute(this.connectedProfileKey)) == false){
            throw new Exception(methode.getName()+" Non autorisee pour le profil "+ session.getAttribute(this.connectedProfileKey));
        }
    }

    // transfert des donnes du MV vers les attributs de la variable session
    public static void setSessionAttributes(HttpSession session, ModelView mv){
        System.out.println("--> Setting session Attributes...");
        for(Map.Entry<String, Object> entree : mv.getSession().entrySet()){
            System.out.println("Attribute: "+entree.getKey()+" Value:"+entree.getValue());
            session.setAttribute(entree.getKey(), entree.getValue());
        }
    }

    //tranfert des donnees dans le MV vers les attributs de la requete
    public static void setRequestAttributes(HttpServletRequest req, ModelView mv){
        for(Map.Entry<String, Object> entree : mv.getData().entrySet()){
            req.setAttribute(entree.getKey(), entree.getValue());
        }
    }

    // generation des arguments necessaire a une methode a partir d'une requete HTTP
    public static Object[] generateArgs(HttpServletRequest req, Method methode){
        // liste des arguments formels de la fonction
        Parameter[] argForms = methode.getParameters();
        Parameter arg;

        // liste de tous les arguments a passer a la fin(le resultat) 
        Object[] args = new  Object[argForms.length];
        
        //Valeur de l'argument courant
        String argVal = null;
        String[] argValues;

        //instance castee vers le type attendu
        Object argValCast; 

        // CONSTRUCTION DE LA LISTE DES ARGUMENTS EFFECTIFS
        // parcours de la liste des arguments formels de la fonction
        String argName; // valeur de l'url de l'Annotatioln Args

        for(int i = 0; i < args.length; i++){
            arg = argForms[i];            
            argName = arg.getAnnotation(Args.class).argName();
            System.out.println("NOMS D'args: "+ argName + "\n" + "TYPE ATTENDU: " + arg.getType().getName());
            argValues = req.getParameterValues(argName);

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
        return args;
    }
    
    // invocation d'Une methode d'un modele donne dont on connait le nom, l'instance appelante et la liste des arguments
    
    public static Object invoke(Object instance, Method methode, Object[] args)throws Exception{
        return methode.invoke(instance, args);
    }
    
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
        String argName; // valeur de l'url de l'Annotatioln Urls

        for(int i = 0; i < args.length; i++){
            arg = argForms[i];            
            argName = arg.getAnnotation(Args.class).argName();
            System.out.println("NOMS D'args: "+ argName + "\n" + "TYPE ATTENDU: " + arg.getType().getName());
            argValues = req.getParameterValues(argName);

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
                        //e4.printStackTrace();
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
        Object[] args = new Object[1]; // liste des arguments a passer aux setters
        String argValInReq; // valeur passee correspondant a un attribut
        Object argValCast;  // instance correpondante a l'argument formel du setter

        // parcours de la liste des attributs
        for (Field attr : attrList) {
            attrName = attr.getName();

            // formation du nom du setter correspondant a cet attribut
            attrSetterName = "set" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);

            String contentType = req.getContentType();
            // cas de passage de fichier
            if(attr.getType().getSimpleName().equalsIgnoreCase("fileupload") && contentType != null && contentType.startsWith("multipart/form-data")){
                Part partfile = req.getPart(attrName);
                if (partfile != null) {
                    System.out.println("---> PARTFILE NON NULL  "+ partfile);
                    // nom du fichier passe depuis la vue
                    String fileName = partfile.getSubmittedFileName();
                    InputStream is = partfile.getInputStream();
                    byte[] bytes = new byte[(int) partfile.getSize()];
                    // lecture du Part et ajout du contenu au byte[]
                    is.read(bytes);
                    FileUpload fu = new FileUpload(fileName, bytes);
                    args[0] = fu;

                    // recherche du setter correspondant
                    for (Method method : methods) {
                        if (method.getName().equalsIgnoreCase(attrSetterName)) {
                            method.invoke(instance, args);
                            //FrontServlet.invoke(instance, method, args);
                        }
                    }
                }
                
            }
            // cas des autres types d'attributs
            else{
                
                String[] argValsInReq = req.getParameterValues(attrName);
                if (argValsInReq != null) {
                    argValInReq = argValsInReq[0];
                    for (Method method : methods) {
                        if (method.getName().equalsIgnoreCase(attrSetterName)) {
                            Parameter arg = method.getParameters()[0];
                            argValCast = FrontServlet.cast(argValInReq, arg.getType());
                            args[0] = argValCast;
                            method.invoke(instance,args);
                            //args = FrontServlet.generateArgs(req, method);
                            //FrontServlet.invoke(instance, method, args);
                        }   
                    }
                }
            }
            
        }
    }


    public void resetAttributes(Object instance) {
        Class<?> clazz = instance.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            if(field.getName().compareTo(this.instanceNumberVariable) != 0){
                field.setAccessible(true);
            
                try {
                    Object value = null;

                    if (field.getType().equals(int.class)) {
                        value = 0;
                    } else if (field.getType().equals(double.class)) {
                        value = 0.0;
                    } else if (field.getType().equals(float.class)) {
                        value = 0.0f;
                    } else if (field.getType().equals(boolean.class)) {
                        value = false;
                    }

                    field.set(instance, value);
                } catch (IllegalAccessException e) {
                    // Gérer l'exception appropriée ici
                    e.printStackTrace();
                }
            }
            
        }
    }
    
    public Object getInstance(Mapping map) throws Exception{
        String className = map.getClassName();
        Object res;         // instance a retourner

        if(this.singles.containsKey(className) && this.singles.get(className) != null){
            // cas d'une classe a instance unique et deja instanciee
            System.out.println("Classe :"+className+" "+"INSTANCE PRESENTE");
            Object instance = this.singles.get(className);
            this.resetAttributes(instance);     //reinitialiser les attributs pour eviter lesconflits entre les valeurs
            res =  instance;
        }
        
        else{
            // cas d'une classe multi-instance  ou d'une classe de singleton mais non encore instanciee       
            try{
                System.out.println("Classe :"+className+" "+"INSTANCIATION...");
                Class classe = Class.forName(map.getClassName());
                res = classe.cast(classe.newInstance());
                if(this.singles.containsKey(className) && this.singles.get(className) == null){
                    this.singles.put(className, res);
                }
            }
            catch(Exception e){
                throw e;
            }
        }
        return res;
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

