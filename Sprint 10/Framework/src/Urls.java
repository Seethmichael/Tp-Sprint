package etu1870.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) // durée de vie ( execution )
@Target(ElementType.METHOD)  //peut être utilisée sur des methodes de classe.
public @interface Urls{
    String url() default "";
}
