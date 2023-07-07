package emp;

import etu1870.annotation.Urls;
import etu1870.framework.ModelView;

public class Etu {
    @Urls(url = "inscrire-etudiant")
    public ModelView Inscrire(){
        ModelView mv = new ModelView();
        mv.setView("emp-list.jsp");
        return mv;
    }   
    
    public void desinscrire(){
        
    }

}
