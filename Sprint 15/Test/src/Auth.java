package emp.utils;

import etu1870.annotation.*;
import etu1870.framework.ModelView;

@Scop(isSingle = true)
public class Auth{
    String profile;
    static int appels = 0;
    
    public Auth(){
        Auth.appels++;
    }

    @Urls(url = "logout.do")
    public ModelView logout(){
        System.out.println("--> Method invocation: LOGOUT ; Profile: "+this.profile);
        System.out.println("### Instances de Auth: "+ Auth.appels);
        ModelView mv = new ModelView();
        mv.setView("index.jsp");
        String[] toInvalidate = {"connectedProfile"};
        mv.setToInvalidate(toInvalidate);
        return mv;
    }

    
    @Urls(url = "invalidateSession.do")
    public ModelView invalidate(){
        System.out.println("--> Method invocation: LOGOUT ; Profile: "+this.profile);
        System.out.println("### Instances de Auth: "+ Auth.appels);
        ModelView mv = new ModelView();
        mv.setView("index.jsp");
        mv.setIsInvalidate(true);
        return mv;
    }


    @Urls(url = "login.do")
    public ModelView login(){
        System.out.println("--> Method invocation: LOGIN ; Profile: "+this.profile);
        System.out.println("### Instances de Auth: "+ Auth.appels);
        ModelView mv = new ModelView();
        mv.setView("index.jsp");
        mv.addSessionItem("connectedProfile", this.profile);
        return mv;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    
}