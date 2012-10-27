/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analysis;

import daisy.io.CSV;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public abstract class PrintUtils {

    static final String separator = "\t";

    public static String printMatrix(double[][] squareMatrix, List<String> names) {
        return printMatrix(squareMatrix, names, names);
    }

    public static String printArray(double[] array, List<String> rowNames, String columnName) {
        int height = array.length;

        if (rowNames.size() != height) {
            throw new IllegalArgumentException("Row names do not match array size.");
        }

        StringBuilder output = new StringBuilder();

        //Print the column name
        output.append(separator); //leading tab
        output.append(columnName);
        output.append("\n");

        for (int i = 0; i < height; i++) {
            //Print the row name
            String name = rowNames.get(i);
            output.append(name).append(separator);

            double value = array[i];
            output.append(value);

            output.append("\n");
        }

        return output.toString();
    }

    public static String printMatrix(double[][] matrix, List<String> rowNames, List<String> columnNames) {

        int height = matrix.length;
        int width = 0;
        if (height > 0) {
            width = matrix[0].length;
        }

        if (height == 0 || width == 0) {
            return "<empty matrix>";
        }

        if (rowNames.size() != height || columnNames.size() != width) {
            throw new IllegalArgumentException("Row names or column names do not match matrix size.");
        }

        StringBuilder output = new StringBuilder();

        //print the column names
        output.append(separator); //leading tab
        for (int i = 0; i < width; i++) {
            String name = columnNames.get(i);
            output.append(name);
            if (i < width - 1) {
                output.append(separator);
            }
        }
        output.append("\n");

        for (int i = 0; i < height; i++) {
            //Print the row name
            String name = rowNames.get(i);
            output.append(name).append(separator);

            for (int j = 0; j < width; j++) {
                double value = matrix[i][j];
                output.append(value);
                if (j < width - 1) {
                    output.append(separator);
                }
            }

            output.append("\n");
        }

        return output.toString();
    }

    public static void writeMatrixToCSV(CSV csv, double[][] squareMatrix, List<String> names) {
        writeMatrixToCSV(csv, squareMatrix, names, names);
    }

    public static void writeMatrixToCSV(CSV csv, double[][] matrix, List<String> rowNames, List<String> columnNames) {
        int height = matrix.length;
        int width = 0;
        if (height > 0) {
            width = matrix[0].length;
        }

        if (height == 0 || width == 0) {
            return;
        }

        if (rowNames == null) {
            rowNames = new ArrayList<String>();
            for (int i = 0; i < height; i++) {
                rowNames.add(Integer.toString(i + 1));
            }
        }

        if (rowNames.size() != height || columnNames.size() != width) {
            throw new IllegalArgumentException("Row names or column names do not match matrix size.");
        }

        try {
            //print the column names
            csv.print("");
            for (int i = 0; i < width; i++) {
                String name = columnNames.get(i);
                csv.print(name);
            }
            csv.println();

            for (int i = 0; i < height; i++) {
                //Print the row name
                String name = rowNames.get(i);
                csv.print(name);

                for (int j = 0; j < width; j++) {
                    double value = matrix[i][j];
                    csv.print(Double.toString(value));
                }
                csv.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void writeArrayToCSV(CSV csv, double[] array, List<String> rowNames, String columnName) {
        int height = array.length;

        if (rowNames.size() != height) {
            throw new IllegalArgumentException("Row names do not match array size.");
        }

        try {
            //Print the column name
            csv.println("", columnName);

            for (int i = 0; i < height; i++) {
                //Print the row name
                String name = rowNames.get(i);
                double value = array[i];
                csv.println(name, Double.toString(value));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
