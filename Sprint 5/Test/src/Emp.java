package emp;

import etu1870.annotation.Urls;
import etu1870.framework.ModelView;

public class Emp {
    public void embaucher(){}

    @Urls(url = "fire-employee")
    public ModelView virer(){
        ModelView mv = new ModelView();
        mv.setView("List.jsp");
        return mv;
    }
}
