package org.ssh.util;

import java.lang.annotation.*; 

@Target (value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Alias {
    String value();
}
