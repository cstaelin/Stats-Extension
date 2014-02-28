/*
 * Implements the reporters for doing regressions and reporting the various
 * regression statistics.
 */
package org.nlogo.extensions.stats;

import org.nlogo.api.LogoException;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Argument;
import org.nlogo.api.Syntax;
import org.nlogo.api.Context;
import org.nlogo.api.LogoList;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.api.DefaultReporter;

public class RegressionPrims {

    public static class RegressAll extends DefaultReporter {
        /*
         * This reporter sets up and solves a linear OLS regression.
         * The input is LogoStatsTbl, with the first column being the
         * observations on the dependent variable and each subsequent
         * column being the observations on the n independent variables.
         * Each row is thus an observation of the dependent variable
         * followed by the corresponding observations of each independent
         * variable.
         *
         * The returned NetLogo list is composed of the regression 
         * coefficients.  The various statistics for the regression and
         * each coefficient are calculated and saved, and may be retrieved
         * separately.
         */

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.ListType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            // Form the array of variable numbers to use in the regression.
            int v = tbl.getNVars();
            int[] vars = new int[v];
            for (int j = 0; j < v; j++) {
                vars[j] = j;
            }

            double[][] coeffs = tbl.regress(vars);

            return ExtnUtils.convertArrayToSimpleLogoList(coeffs);
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class RegressOn extends DefaultReporter {
        /*
         * This reporter sets up and solves a linear OLS regression.
         * The input is LogoStatsTbl and a simple list of variable 
         * numbers or names.  The first variable in the list is the 
         * dependent variable, and the rest are the independent variables.
         * The order of the variables need not correspond to their order
         * in the data table.
         *
         * The returned NetLogo list is composed of the regression 
         * coefficients.  The various statistics for the regression and
         * each coefficient are calculated and saved, and may be retrieved
         * separately.
         */

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType(),
                        Syntax.ListType()}, Syntax.ListType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            // Extract the variable list and put its elements into
            // the int[] array, vars. If the variable list is a list of
            // numbers, simply put them in the varlist array.  If it is a
            // list of variable names, then we need to convert each name
            // to its index number.
            LogoList LogoVarList = args[1].getList();
            int v = LogoVarList.size();
            int[] vars = new int[v];
            int nv = tbl.getNVars();
            if (v > nv) {
                throw new org.nlogo.api.ExtensionException(
                        "Too many variables in the regress-on list.");
            }
            if (LogoVarList.get(0) instanceof Number) {
                // We assume it is a list of numbers that need to be converted
                // to ints.
                for (int j = 0; j < v; j++) {
                    vars[j] = (int) Math.round(((Number) LogoVarList.get(j)).doubleValue());
                    if (vars[j] < 0 || vars[j] >= nv) {
                        throw new org.nlogo.api.ExtensionException(
                                "Variable number out of range in the regress-on list.");
                    }
                }
            } else {
                // We assume its a list of names.
                for (int j = 0; j < v; j++) {
                    String name = LogoVarList.get(j).toString();
                    vars[j] = tbl.getNameIndex(name);
                    if (vars[j] == -1) {
                        throw new org.nlogo.api.ExtensionException(
                                "No variable with the name " + name + ".");
                    }
                }
            }

            // check vars for duplicates.
            if (ExtnUtils.duplicates(vars, nv)) {
                throw new ExtensionException("Duplicate variables in the "
                        + "regress-on variable list.");
            }

            double[][] coeffs = tbl.regress(vars);
            return ExtnUtils.convertArrayToSimpleLogoList(coeffs);
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class GetRegressionStats extends DefaultReporter {
        // Returns the latest regression statistics.

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.ListType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            LogoListBuilder statslist = new LogoListBuilder();
            double[] rstats = tbl.getRStats();
            for (int i = 0; i < rstats.length; i++) {
                statslist.add(rstats[i]);
            }
            return statslist.toLogoList();
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class GetCoefficientStats extends DefaultReporter {
        // returns the t statistics p values & std errors for the regression
        // coefficients in a nested list, p values first, then t's, then se's.

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.ListType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            double[][] stats = tbl.getRCStats();
            return ExtnUtils.convertArrayToNestedLogoList(stats);
        }
    }
}
