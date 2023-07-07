package emp2;

import etu1870.annotation.Urls;
import etu1870.framework.ModelView;

public class Empe {
    @Urls(url = "emp-myMethod")
    public ModelView maMethode(){
        ModelView mv = new ModelView();
        mv.setView("emp-list.jsp");
        return mv;
    }
}
