/*
 * Implements the reporters for finding the variance-covariance and 
 * correlation matrices.
 */
package org.nlogo.extensions.stats;

import org.nlogo.api.LogoException;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Argument;
import org.nlogo.api.Syntax;
import org.nlogo.api.Context;
import org.nlogo.api.LogoList;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.DefaultCommand;

public class CorrelPrims {

    public static class UseBesselCorrection extends DefaultCommand {
        // Specify whether to use the Bessel correction for a sample.

        @Override
        public Syntax getSyntax() {
            return Syntax.commandSyntax(new int[]{Syntax.WildcardType(),
                        Syntax.BooleanType()});
        }

        public void perform(Argument args[], Context context)
                throws ExtensionException, LogoException {
            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            tbl.useSample(args[1].getBoolean());
        }
    }

    public static class CorrelationMatrix extends DefaultReporter {
        // Find the variance-covariance matrix for the data in the data
        // matrix over the most recent tbl.useObs periods.

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.ListType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            double[][] mat = tbl.getCorrelations();
            if (mat == null) {
                throw new org.nlogo.api.ExtensionException(
                        "Less than two variables or observations.");
            }
            LogoList result = ExtnUtils.convertArrayToNestedLogoList(mat);
            return result;
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class VarCovarMatrix extends DefaultReporter {
        // Find the variance-covariance matrix for the data in the data
        // matrix over the most recent tbl.useObs periods.

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.ListType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            double[][] mat = tbl.getVarCovars();
            if (mat == null) {
                throw new org.nlogo.api.ExtensionException(
                        "Less than two variables or observations.");
            }
            LogoList result = ExtnUtils.convertArrayToNestedLogoList(mat);
            return result;
        }
    }
}