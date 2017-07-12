/*
 * Implements the reporters for finding the variance-covariance and 
 * correlation matrices.
 */
package org.nlogo.extensions.stats;

import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.core.LogoList;

public class CorrelPrims {

    public static class UseBesselCorrection implements Command {
        // Specify whether to use the Bessel correction for a sample.

        @Override
        public Syntax getSyntax() {
            return SyntaxJ.commandSyntax(new int[]{Syntax.WildcardType(),
                        Syntax.BooleanType()});
        }

      /**
       *
       * @param args
       * @param context
       * @throws ExtensionException
       * @throws LogoException
       */
      @Override
        public void perform(Argument args[], Context context)
                throws ExtensionException, LogoException {
            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            tbl.useSample(args[1].getBoolean());
        }
    }

    public static class CorrelationMatrix implements Reporter {
        // Find the variance-covariance matrix for the data in the data
        // matrix over the most recent tbl.useObs periods.

        @Override
        public Syntax getSyntax() {
            return SyntaxJ.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.ListType());
        }

      /**
       *
       * @param args
       * @param context
       * @return
       * @throws ExtensionException
       * @throws LogoException
       */
      @Override
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
    public static class VarCovarMatrix implements Reporter {
        // Find the variance-covariance matrix for the data in the data
        // matrix over the most recent tbl.useObs periods.

        @Override
        public Syntax getSyntax() {
            return SyntaxJ.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.ListType());
        }

      /**
       *
       * @param args
       * @param context
       * @return
       * @throws ExtensionException
       * @throws LogoException
       */
      @Override
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