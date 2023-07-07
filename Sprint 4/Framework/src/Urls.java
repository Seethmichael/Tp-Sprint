package etu1870.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) // durée de vie ( execution )
@Target(ElementType.METHOD)  //ne peut être utilisée que sur des methodes de classe.
public @interface Urls{
    String url() default "";
}
  