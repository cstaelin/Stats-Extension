/*
 * Contains several useful "utilities", inlcuding some for converting 
 * arrays to lists and vice versa.
 */
package org.nlogo.extensions.stats;

import org.nlogo.api.*;
import org.nlogo.core.LogoList;

import Jama.Matrix;

public class ExtnUtils {

    public static void writeToNetLogo(String mssg, Boolean toOutputArea,
        Context context) throws ExtensionException, LogoException {
        try {
            context.workspace().outputObject(mssg, null, true, true,
                (toOutputArea)
                    ? org.nlogo.api.OutputDestinationJ.OUTPUT_AREA()
                    : org.nlogo.api.OutputDestinationJ.NORMAL());
        } catch (LogoException e) {
            throw new ExtensionException(e);
        }
    }

    public static void matPrint(String lbl, Matrix M, Context context)
        throws ExtensionException, LogoException {
        int m = M.getRowDimension();
        int n = M.getColumnDimension();
        writeToNetLogo(lbl + " " + m + " " + n,
            false, context);

        for (int i = 0; i < m; i++) {
            StringBuilder buf = new StringBuilder();
            for (int j = 0; j < n; j++) {
                buf.append(M.get(i, j));
                buf.append(" ");
            }
            writeToNetLogo(buf.toString(), false, context);
        }
    }

    public static void valPrint(String lbl, double val, Context context)
        throws ExtensionException, LogoException {
        writeToNetLogo(lbl + " " + val, false, context);
    }

    public static boolean duplicates(int[] intArray, int maxInt) {
    // checks for duplicates in an array of integers.  The integers
        // themselves must be positive and in the range of 0 to maxInt,
        // inclusive.
        boolean[] bitmap = new boolean[maxInt + 1]; // Java inits to false
        for (int item : intArray) {
      // XOR the corresponding position in the bitmap with true.  If it was 
            // false, it will be set to true as that interger exists in intArray.
            // If it was true, will be set back to false, indicating that this 
            // integer is a duplicate.  Return "true".
            bitmap[item] ^= true;
            if (!bitmap[item]) {
                return true;
            }
        }
        return false;
    }

    public static double[][] convertNestedLogoListToArray(LogoList nestedLogoList)
        throws ExtensionException {
        int numRows = nestedLogoList.size();
        if (numRows == 0) {
            throw new ExtensionException("input list was empty");
        }
        int numCols = -1;
        // find out the maximum column size of any of the rows,
        // in case we have a "ragged" right edge, where some rows
        // have more columns than others.
        for (Object obj : nestedLogoList.toJava()) {
            if (obj instanceof LogoList) {
                LogoList rowList = (LogoList) obj;
                if (numCols == -1) {
                    numCols = rowList.size();
                } else if (numCols != rowList.size()) {
                    throw new ExtensionException("To convert a nested list "
                        + "into a matrix, all nested lists must be the "
                        + "same length -- e.g. [[1 2 3 4] [1 2 3]] is "
                        + "invalid, because row 1 has one more entry.");
                }
            } else {
                throw new ExtensionException("To convert a nested list into "
                    + "a matrix, there must be exactly two levels of "
                    + "nesting -- e.g. [[1 2 3] [4 5 6]] creates a good "
                    + "2x3 matrix.");
            }
        }
        if (numCols == 0) {
            throw new ExtensionException("input list contained only empty lists");
        }
        double[][] array = new double[numRows][numCols];
        int row = 0;
        for (Object obj : nestedLogoList.toJava()) {
            int col = 0;
            LogoList rowList = (LogoList) obj;
            for (Object obj2 : rowList.toJava()) {
                if (obj2 instanceof Number) {
                    array[row][col] = ((Number) obj2).doubleValue();
                    col++;
                }
            }
            // pad with zeros if we have a "ragged" right edge
            for (; col < numCols; col++) {
                array[row][col] = 0.0;
            }
            row++;
        }

        return array;
    }

    public static double[][] convertSimpleLogoListToArray(LogoList SimpleLogoList)
        throws ExtensionException {
        int numRows = 1;
        int numCols = SimpleLogoList.size();

        double[][] array = new double[numRows][numCols];
        int row = 0;
        for (int i = 0; i < numCols; i++) {
            array[row][i] = ((Number) SimpleLogoList.get(i)).doubleValue();
        }

        return array;
    }

    public static String[] convertLogoListOfStringsToStringArray(LogoList stringList)
        throws ExtensionException {
        // Converts a LogoList of strings to a String array. This could
        // simply be done with the .toArray() method, but this routine
        // checks to be sure that all the elements of the list are in
        // fact strings.
        int nstr = stringList.size();
        String[] stringArray = new String[nstr];
        for (int i = 0; i < nstr; i++) {
            if (stringList.get(i) instanceof String) {
                stringArray[i] = stringList.get(i).toString();
            } else {
                throw new ExtensionException("Expected a list of strings."
                    + " Found " + stringList.get(i).toString()
                    + " instead");
            }
        }
        return stringArray;
    }

    public static LogoList convertArrayToNestedLogoList(double[][] dArray) {
        LogoListBuilder lst = new LogoListBuilder();
        for (double[] dArray1 : dArray) {
            LogoListBuilder rowLst = new LogoListBuilder();
            for (int j = 0; j < dArray1.length; j++) {
                rowLst.add(dArray1[j]);
            }
            lst.add(rowLst.toLogoList());
        }
        return lst.toLogoList();
    }

    public static LogoList convertArrayToSimpleLogoList(double[][] dArray) {
        LogoListBuilder lst = new LogoListBuilder();
        for (double[] dArray1 : dArray) {
            for (int j = 0; j < dArray1.length; j++) {
                lst.add(dArray1[j]);
            }
        }
        return lst.toLogoList();
    }

    public static LogoList convertVectorToSimpleLogoList(double[] dArray) {
        LogoListBuilder lst = new LogoListBuilder();
        for (int i = 0; i < dArray.length; i++) {
            lst.add(dArray[i]);
        }
        return lst.toLogoList();
    }

    public static LogoList convertStringArrayToLogoListOfStrings(String[] stringArray) {
        LogoListBuilder lst = new LogoListBuilder();
        for (String s : stringArray) {
            lst.add(s);
        }
        return lst.toLogoList();
    }

    public static String[] convertLogoListOfNamesToStringArray(LogoList stringList)
        throws ExtensionException {
        // Converts a LogoList of strings and numbers to a String array of
        // variable names.  Strings are put into the array as is.  Numbers
        // are converted to integers, then to strings, and then prepended
        // by "Var".
        int nstr = stringList.size();
        String[] stringArray = new String[nstr];
        for (int i = 0; i < nstr; i++) {
            if (stringList.get(i) instanceof String) {
                stringArray[i] = stringList.get(i).toString();
            } else if (stringList.get(i) instanceof Double) {
                stringArray[i] = "Var"
                    + Long.toString(Math.round((Double) stringList.get(i)));
            } else {
                throw new ExtensionException("name list contained an invalid entry.");
            }
        }
        return stringArray;
    }

    /* ---------------------------------------------------------------------- */
    public static int getVarNumberFromArg(LogoStatsTbl tbl, Argument arg)
        throws ExtensionException, LogoException {
        int varNumber;
        if (arg.get() instanceof Double) {
            varNumber = arg.getIntValue();
        } else if (arg.get() instanceof String) {
            String name = arg.getString();
            varNumber = tbl.getNameIndex(name);
            if (varNumber == -1) {
                throw new ExtensionException("Variable name " + name
                    + " not found.");
            }
        } else {
            throw new org.nlogo.api.ExtensionException(
                "Expected a variable number or name but found "
                + arg.getString() + " instead.");
        }
        return varNumber;
    }
}
