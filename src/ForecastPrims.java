/*
 * Implements the forecast reporters.
 */
package org.nlogo.extensions.stats;

import org.nlogo.api.LogoException;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Argument;
import org.nlogo.api.Syntax;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;

public class ForecastPrims {

    /* ---------------------------------------------------------------------- */
    public static class ForecastLinearTrend extends DefaultReporter {
        /*
         * Computes the linear growth equation for a single variable against 
         * time, and then returns a forecast of that variable T periods
         * beyond the last observation. (T may be negative.) The equation 
         * being fit is
         *      Y = constant + slope * t
         * where t is "time". If n is the number of observations in the
         * input list, the forecast is thus given by
         *      forecast = constant + slope * (n+T)
         * (As is normal in NetLogo, time begins with zero.)
         * The regression statistics are available separately.
         * 
         * If there is only one observation in the table, that observation 
         * is returned as the forecast.  If there are no observations an error
         * is thrown.
         */

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType(),
                        Syntax.WildcardType(), Syntax.NumberType()},
                    Syntax.NumberType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            int var = ExtnUtils.getVarNumberFromArg(tbl, args[1]);
            int forecastAt = args[2].getIntValue();
            if (tbl.getNObs() == 0) {
                throw new org.nlogo.api.ExtensionException(
                     "There must be at least one observation for a forecast.");
            }
            double[] coeffs = (tbl.forecast(var, 0))[0];
            return coeffs[0]
                    + coeffs[1] * (tbl.getNObsUsed() + forecastAt - 1);
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class ForecastCompoundTrend extends DefaultReporter {
        /*
         * Computes a compound growth rate for a single variable against 
         * time, and then returns a forecast of that variable T periods
         * beyond the last observation. (T may be negative.) The equation 
         * being fit is
         *      Y = constant * (1 + rate)^t
         * where t is "time". If n is the number of observations in the
         * input list, the forecast is thus given by
         *      forecast = constant * (1 + rate)^(n+T)
         * (As is normal in NetLogo, time begins with zero.)
         * Note that (1 + rate) can be less than one, indicating a 
         * negative compound growth rate.
         * The regression coefficients are available separately.
         * 
         * Because of the use of the log function, the variable can 
         * not contain zero or negative values.  An error is thrown 
         * if they are encountered.
         * 
         * If there is only one observation in the table, that observation 
         * is returned as the forecast.  If there are no observations an error
         * is thrown.
         */

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType(),
                        Syntax.WildcardType(), Syntax.NumberType()},
                    Syntax.NumberType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            int var = ExtnUtils.getVarNumberFromArg(tbl, args[1]);
            int forecastAt = args[2].getIntValue();
            if (tbl.getNObs() == 0) {
                throw new org.nlogo.api.ExtensionException(
                     "There must be at least one observation for a forecast.");
            }
            double[] coeffs = (tbl.forecast(var, 1))[0];
            return coeffs[0] * Math.pow((1.0 + coeffs[1]),
                    (double) (tbl.getNObsUsed() + forecastAt - 1));
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class ForecastContinuousTrend extends DefaultReporter {
        /*
         * Computes a continuous growth rate for a single variable against 
         * time, and then returns a forecast of that variable T periods
         * beyond the last observation. (T may be negative.) The equation 
         * being fit is
         *      Y = constant * e^(rate * t)
         * where t is "time". If n is the number of observations in the
         * input list, the forecast is thus given by
         *      forecast = constant * e^(rate * (n+T))
         * (As is normal in NetLogo, time begins with zero.)
         * Note that rate can be negative, indicating a negative growth rate.
         * Note too that continuous growth is the continuous analog of 
         * compound growth and the two procedures will usually give 
         * comparable results.
         * The regression coefficients are available separately.
         * 
         * Because of the use of the log function, the input string can not
         * contain zero or negative values.  An error is thrown if they 
         * are encountered.
         * 
         * If there is only one observation in the table, that observation 
         * is returned as the forecast.  If there are no observations an error
         * is thrown.
         */

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType(),
                        Syntax.WildcardType(), Syntax.NumberType()},
                    Syntax.NumberType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            int var = ExtnUtils.getVarNumberFromArg(tbl, args[1]);
            int forecastAt = args[2].getIntValue();
            if (tbl.getNObs() == 0) {
                throw new org.nlogo.api.ExtensionException(
                     "There must be at least one observation for a forecast.");
            }
            double[] coeffs = (tbl.forecast(var, 2))[0];
            return coeffs[0]
                    * Math.exp(coeffs[1] * (tbl.getNObsUsed() + forecastAt - 1));
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class GetForecastParameters extends DefaultReporter {
        // Returns the parameters of the most recent forecast as a 
        // 2-element list. The first element is the constant and the second
        // the slope (for linear) or the rate of growth, as appropriate to 
        // the type of forecast that was called for.

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.ListType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            return ExtnUtils.convertArrayToSimpleLogoList(tbl.getFCoeffs());
        }
    }
}
