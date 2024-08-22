package io.github.gdrfgdrf.multimodulemediator.utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author gdrfgdrf
 */
public class ClassUtils {
    public static void search(
            File searchRoot,
            String packageName,
            Predicate<Class<?>> predicate,
            ClassLoader classLoader,
            Set<Class<?>> result
    ) {
        searchInternal(searchRoot, packageName, predicate, result, classLoader, true);
    }

    private static void searchInternal(
            File searchRoot,
            String packageName,
            Predicate<Class<?>> predicate,
            Set<Class<?>> result,
            ClassLoader classLoader,
            boolean flag
    ) {
        if (searchRoot.isDirectory()) {
            File[] files = searchRoot.listFiles();
            if (files == null) {
                return;
            }
            if (!flag) {
                packageName = packageName + "." + searchRoot.getName();
            }

            String finalPackageName = packageName;
            Arrays.stream(files).forEach(file -> searchInternal(file, finalPackageName, predicate, result, classLoader, false));

            return;
        }
        if (searchRoot.getName().endsWith(".class")) {
            try {
                Class<?> clazz = Class.forName(
                        packageName + "." + searchRoot
                                .getName()
                                .substring(0, searchRoot.getName().lastIndexOf(".")),
                        true,
                        classLoader
                );
                if (predicate == null || predicate.test(clazz)) {
                    result.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void searchJar(
            ClassLoader classLoader,
            String packageName,
            Predicate<Class<?>> predicate,
            Set<Class<?>> result
    ) {
        try {
            Enumeration<URL> urlEnumeration = classLoader
                    .getResources(packageName.replace(".", "/"));

            while (urlEnumeration.hasMoreElements()) {
                URL url = urlEnumeration.nextElement();
                String protocol = url.getProtocol();

                if (!"jar".equalsIgnoreCase(protocol)) {
                    if ("file".equalsIgnoreCase(protocol)) {
                        String classpath = url.getPath().replace(
                                packageName.replace(".", "/"),
                                ""
                        );
                        String packagePath = packageName.replace(".", "/");
                        File searchRoot = new File(classpath + packagePath);

                        search(searchRoot, packageName, predicate, classLoader, result);
                    }
                    continue;
                }
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                if (connection == null) {
                    continue;
                }
                JarFile jarFile = connection.getJarFile();
                if (jarFile == null) {
                    continue;
                }

                Enumeration<JarEntry> entryEnumeration = jarFile.entries();
                while (entryEnumeration.hasMoreElements()) {
                    JarEntry entry = entryEnumeration.nextElement();
                    String entryName = entry.getName();
                    if (!entryName.contains(".class") ||
                            !entryName.replaceAll("/", ".").startsWith(packageName)) {
                        continue;
                    }
                    String className = entryName.substring(0, entryName.lastIndexOf("."))
                            .replace("/", ".");
                    Class<?> clazz = Class.forName(className, true, classLoader);

                    if (predicate == null || predicate.test(clazz)) {
                        result.add(clazz);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
