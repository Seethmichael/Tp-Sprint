package etu1870.framework;

public class Mapping{
    String className;
    String Method;

    
    // _ _ _ CONSTRUCTORS _ _ _
    
    public Mapping(String className, String method) {
        this.className = className;
        Method = method;
    }

    //_ _ _ GET SET  _ _ _

    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String getMethod() {
        return Method;
    }
    public void setMethod(String method) {
        Method = method;
    }

    
}