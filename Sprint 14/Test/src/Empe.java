package emp2;

import etu1870.annotation.Urls;
import etu1870.framework.ModelView;
import model.societe.*;
import java.util.Vector;

public class Empe {
    static int appels = 0;

    public Empe(){
        Empe.appels++;
    }

    @Urls(url = "emp-myMethod")
    public ModelView maMethode(){
        ModelView mv = new ModelView();
        System.out.println("INSTANCES PRESENTES  DE EMPE: "+Empe.appels);

        // vue destinatrice
        mv.setView("emp-list.jsp");

        //elements de donnees a passer a la vue
        Vector<Emp> liste = new Vector<Emp>();
        Emp e = new Emp();
        
        e.setNom("Lili");
        liste.add(e);
        
        // ajout de la liste aux donnees a passer
        mv.addItem("liste", liste);
        

        return mv;
    }
}
