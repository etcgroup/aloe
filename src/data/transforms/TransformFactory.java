/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.transforms;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class TransformFactory {

    private static class Specification {

        private final Class transformClass;
        //Arguments?
        
        private Specification(Class transformClass) {
            this.transformClass = transformClass;
        }

        public DataSetTransform construct() {
            Object object = null;
            try {
                object = transformClass.newInstance();
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Could not access " + transformClass.getName() + ".");
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("Could not construct " + transformClass.getName() + ".");
            }

            DataSetTransform transformObject = null;
            if (object instanceof DataSetTransform) {
                transformObject = (DataSetTransform) object;
            } else {
                throw new IllegalArgumentException(transformClass.getName() + " does not implement DataSetTransform.");
            }

            //Deal with arguments?

            return transformObject;
        }
    }
    private Map<String, Specification> shortcuts = new HashMap<String, Specification>();

    public void loadShortcuts(File shortcutsFile) throws FileNotFoundException {
        Scanner scan = new Scanner(shortcutsFile);
        int lineNum = 0;
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            lineNum++;

            if (!line.startsWith("#") && !line.isEmpty()) {
                line = line.trim();
                String[] parts = line.split("\t", 2);

                if (parts.length != 2) {
                    System.err.println("Line " + lineNum + " too short in shortcuts file.");
                    continue;
                }

                String shortcut = parts[0];
                String specification = parts[1];

                if (shortcut.isEmpty()) {
                    System.err.println("Missing shortcut name on line " + lineNum + " in shortcuts file.");
                    continue;
                }

                if (specification.isEmpty()) {
                    System.err.println("Missing specification on line " + lineNum + " in shortcuts file.");
                    continue;
                }

                try {
                    Specification parsed = parseSpecification(specification);
                    shortcuts.put(shortcut, parsed);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid specification on line " + lineNum + " in shortcuts file.");
                    e.printStackTrace();
                }
            }
        }
    }

    private Specification parseSpecification(String specificationString)
            throws IllegalArgumentException {
        if (specificationString.isEmpty()) {
            throw new IllegalArgumentException("Empty specification string");
        }

        String[] parts = specificationString.split("\\s+");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Error parsing specification string");
        }

        String className = parts[0];
        Class transformClass = null;
        try {
            transformClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class " + className + " could not be located.");
        }

        Specification spec = new Specification(transformClass);
        return spec;
    }

    public DataSetTransform construct(String specificationString) {
        Specification specification = expandShortcut(specificationString);
        if (specification == null) {
            try {
                specification = parseSpecification(specificationString);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid specification " + specificationString);
                e.printStackTrace();
                System.exit(1);
            }
        }

        try {
            DataSetTransform transform = specification.construct();
            return transform;
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid specification " + specificationString);
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    private Specification expandShortcut(String shortcutString) {
        if (shortcuts.containsKey(shortcutString)) {
            return shortcuts.get(shortcutString);
        } else {
            return null;
        }
    }
}
