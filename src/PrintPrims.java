/*
 * Implements the reporters for placing the data table, the variance-
 * covariance matrix and the correlation matrix into strings for printing.
 */
package org.nlogo.extensions.stats;

import org.nlogo.api.LogoException;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Argument;
import org.nlogo.api.Syntax;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;

public class PrintPrims {

    public static class ConvertDataToString extends DefaultReporter {
        // Converts the data matrix to a string for printing. Rows and 
        // columns are labeled with the observation number and variable
        // names if the names have been defined.

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.StringType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            if (!tbl.haveData()) {
                throw new org.nlogo.api.ExtensionException(
                        "Attempt to print a data table "
                        + "before one has been created.");
            }
            return tbl.printData();
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class ConvertCovarToString extends DefaultReporter {
        // Converts the variance-covariance matrix to a string for printing.
        // Rows and columns are labeled with the variable names if they 
        // have been defined.

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.StringType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            String mat = tbl.printCovariance();
            if (mat == null) {
                throw new org.nlogo.api.ExtensionException(
                        "Attempt to print a variance-covariance matrix "
                        + "before one has been calculated.");
            }
            return mat;
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class ConvertCorrelToString extends DefaultReporter {
        // Converts the correlation matrix to a string for printing.
        // Rows and columns are labeled with the variable names if they 
        // have been defined.

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.StringType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            String mat = tbl.printCorrelation();
            if (mat == null) {
                throw new org.nlogo.api.ExtensionException(
                        "Attempt to print a correlation matrix "
                        + "before one has been calculated.");
            }
            return mat;
        }
    }
}