package helpers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ReflectionHelper {

    public static <T> List<Class<T>> findClassesImpmenenting(final Class<T> interfaceClass) {

        Package fromPackage = interfaceClass.getPackage();

        if (interfaceClass == null) {
            return null;
        }

        if (fromPackage == null) {
            return null;
        }

        final List<Class<T>> rVal = new ArrayList<Class<T>>();
        try {
            final Class<T>[] targets = getAllClassesFromPackage(fromPackage.getName());
            if (targets != null) {
                for (Class<T> aTarget : targets) {
                    if (aTarget == null) {
                        continue;
                    } else if (aTarget.equals(interfaceClass)) {
                        continue;
                    } else if (!interfaceClass.isAssignableFrom(aTarget)) {
                        continue;
                    } else {
                        rVal.add(aTarget);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error reading package name.");
        } catch (IOException e) {
            System.err.println("Error reading classes in package.");
        }

        return rVal;
    }

    /**
     * Load all classes from a package.
     * 
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Class[] getAllClassesFromPackage(final String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Find file in package.
     * 
     * @param <T>
     * 
     * @param directory
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     */
    public static <T> List<Class<T>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<T>> classes = new ArrayList<Class<T>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add((Class<T>) Class
                        .forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}