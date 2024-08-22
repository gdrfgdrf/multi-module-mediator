package io.github.gdrfgdrf.multimodulemediator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author gdrfgdrf
 */
public class bb {
    public static void main(String[] args) {
        for (Method declaredMethod : AAA.class.getDeclaredMethods()) {
            System.out.println(declaredMethod);
        }


    }
}
