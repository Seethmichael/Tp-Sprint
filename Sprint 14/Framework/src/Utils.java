package etu1870.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;
import etu1830.annotation.Urls;
import etu1830.annotation.Scop;
import etu1830.framework.Mapping;
import etu1830.framework.ModelView;


public class Utils {

    // set d'un attribut de nom et de valeur connue
    public static void setAttribute(String fieldName, String[] fieldValue, Object instance){
        
    }
    
    // recuperation d'une instance de methode
    // et son objet appelant
    // Obsolete a partir du SPRINT 7
    public static ModelView getModelView(Mapping map) throws Exception{
        Object mv;
        try{
            Class classe = Class.forName(map.getClassName());
            Method[] methods = classe.getDeclaredMethods();
            Method methode = null;
            for(Method courant: methods){
                //recuperation de la methode 
                if (courant.getName().equals(map.getMethod())) methode = courant;
            }
            // instanciation de l'objet appelant
            Object instance  = classe.cast(classe.newInstance());
            
            // invocation de la methode
            mv = methode.invoke(instance);
            if(mv instanceof ModelView == false){
                throw new Exception("La methode " + map.getMethod()+" de la classe "+map.getClass()+" ne conduit pas a une vue...");
            }

            return (ModelView)mv;
        }
        catch(Exception e){
            throw e;
        }
        
    }
    
     // recuperation des classes annotees SCOP
    public static HashMap<String, Object> getScopAnnotedClasses(Vector<Class<?>> classes) {
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        Scop annotation;
        String className;
        
        for (Class<?> clazz : classes) {
            annotation = clazz.getAnnotation(Scop.class);
            
            if (annotation != null && annotation.isSingle() == true) {
                className = clazz.getName();
                resultMap.put(className, null);
            }
        }
        
        return resultMap;
    }

    // recuperation de toutes les methodes annotees URls
    public static HashMap<String, Mapping> getUrlsAnnotedMethods(Vector<Class<?>> classes){
        java.lang.reflect.Method[] methods;
        String valeurAnnotation,
                nomClassse,
                nomMehtode;
        Mapping mapping;
        HashMap<String, Mapping> res = new HashMap<String, Mapping>();

        for (Class<?> courante : classes) {
            nomClassse = courante.getName();
            methods = courante.getDeclaredMethods();
            for (java.lang.reflect.Method method : methods) {
                Urls annotation = method.getAnnotation(Urls.class);
                if(annotation != null){
                    valeurAnnotation = annotation.url();
                    nomMehtode = method.getName();
                    mapping = new Mapping(nomClassse, nomMehtode);
                    res.put(valeurAnnotation, mapping);
                } 
            }
        }
        return res;
    }

    // recuperation de toutes les classes a partir d'un dossier de depart
    public static Vector<Class<?>> getClasses(Vector<Class<?>> classes ,  String name ) throws ClassNotFoundException {
        String packageName= name;
        if (classes == null) {
            classes = new Vector<Class<?>>();
        }
        
        // Get a File object for the package
        File directory = null;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = packageName.replace('.', '/');
            System.out.println("RESSOURCE: "+path);
            URL resource = classLoader.getResource(path);
            if (resource == null) {
                throw new ClassNotFoundException("No resource for " + path);
            }
            directory = new File(resource.getFile());
        } catch (NullPointerException x) {
            x.printStackTrace();
            throw new ClassNotFoundException(packageName + " (" + directory + ") does not appear to be a valid package");
        }

        if (directory.exists()) {
            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                // we are only interested in .class files
                File file = new File(directory.getAbsolutePath() + File.separator + files[i]);
                System.out.println( " path : "+file.getAbsolutePath() );
                if (file.isDirectory()) {
                    if(packageName.isEmpty()) {
                        //System.out.println("Aucun nom specifie");
                        getClasses(classes, file.getName());
                    }
                    else getClasses(classes, packageName + "." + file.getName());
                } else {
                    if (files[i].endsWith(".class")) {
                        // removes the .class extension
                        System.out.println( "files : "+files[i] );
                        if(packageName.isEmpty()) classes.add(Class.forName(files[i].substring(0, files[i].length() - 6)));

                        else classes.add(Class.forName(packageName + '.' + files[i].substring(0, files[i].length() - 6)));
                    }
                }
            }
        } else {
            throw new ClassNotFoundException(packageName + " does not appear to be a valid package");
        }
        return classes;
    }

    
    public static String getPathFromURL(String url, String context){
        String res;
        String[] spliting = url.split(context+"/"); //le but est de recuperer la partie qui vient apres le contexte
        for (Object o: spliting){
            System.out.println("Spliting: "+o.toString());
        }
        
        if (spliting.length < 2) res = "";    // cas ou on uniquement le contexte dans l'URL
        else res = spliting[1];
        return res;   
    }
}
