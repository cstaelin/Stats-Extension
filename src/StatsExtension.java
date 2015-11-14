/*
 * Stats extension, v. 1.4.0, October 12, 2015
*/

/*
 * This is the main class for the stats extension.  It defines the various
 * commands and reporters, and does some NetLogo housekeeping.
 *
 * The stats extension uses two external libraries:
 * Jama-1.0.3 is used for matrix operations.  Copyright Notice: This software
 * is a cooperative product of The MathWorks and the National Institute of 
 * Standards and Technology (NIST) which has been released to the public 
 * domain. Neither The MathWorks nor NIST assumes any responsibility whatsoever
 * for its use by other parties, and makes no guarantees, expressed or implied,
 * about its quality, reliability, or any other characteristic.
 * http://math.nist.gov/javanumerics/jama/#Background
 *
 * colt.jar, version 1.2.0 is used for the calculation of various statistics 
 * and distributions. It is distributed by CERN. Copyright (c) 1999
 * CERN - European Organization for Nuclear Research.  
 * Permission to use, copy, modify, distribute and 
 * sell this software and its documentation for any purpose is hereby granted 
 * without fee, provided that the above copyright notice appear in all copies 
 * and that both that copyright notice and this permission notice appear in 
 * supporting documentation. CERN makes no representations about the 
 * suitability of this software for any purpose. It is provided "as is" 
 * without expressed or implied warranty.
 * http://dst.lbl.gov/ACSSoftware/colt/
*/
package org.nlogo.extensions.stats;

import org.nlogo.api.LogoException;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Argument;

public class StatsExtension
        extends org.nlogo.api.DefaultClassManager {

    @Override
    public java.util.List<String> additionalJars() {
        java.util.List<String> list = new java.util.ArrayList<String>();
        list.add("Jama-1.0.3.jar");
        list.add("colt.jar");
        return list;
    }
    // the WeakHashMap here may seem a bit odd, but it is apparently 
    // the easiest way to handle things.
    // for explanation, see the comment in ArrayExtension.java in the 
    // Array extension.
    public static final java.util.WeakHashMap<LogoStatsTbl, Long> LogoStatsTbls = 
            new java.util.WeakHashMap<LogoStatsTbl, Long>();
    public static long next = 0;

    public static LogoStatsTbl getTblFromArgument(Argument arg)
            throws ExtensionException, LogoException {
        // A convenience method to extract a LogoStatsTbl object from an 
        // Argument. It serves a similar purpose to args[x].getString(), 
        // or args[x].getList() and throws an error if the argument is 
        // not a LogoStatsTbl.
        Object obj = arg.get();
        if (!(obj instanceof LogoStatsTbl)) {
            throw new org.nlogo.api.ExtensionException("A StatsTable was "
                    + "expected but got this instead: "
                    + org.nlogo.api.Dump.logoObject(obj));
        }
        return (LogoStatsTbl) obj;
    }

    /* ********************************************************************** */
    // Define the extension primitives.
    /* ********************************************************************** */
    public void load(org.nlogo.api.PrimitiveManager primManager) {

        primManager.addPrimitive("newtable", 
                new TblPrims.NewTable());
        primManager.addPrimitive("newtable-from-row-list",
                new TblPrims.NewTableFromRowList());
        primManager.addPrimitive("add", 
                new TblPrims.AddNewRow());
        primManager.addPrimitive("get-data-as-list",
                new TblPrims.GetDataAsNestedList());
        primManager.addPrimitive("get-observations",
                new TblPrims.GetColumnAsSimpleList());
        primManager.addPrimitive("get-nobs", 
                new TblPrims.GetNObs());
        primManager.addPrimitive("get-nobs-used", 
                new TblPrims.GetNObsUsed());
        primManager.addPrimitive("set-names", 
                new TblPrims.SetNames());
        primManager.addPrimitive("get-names", 
                new TblPrims.GetNames());
        primManager.addPrimitive("use-most-recent", 
                new TblPrims.SetObsUsed());
        primManager.addPrimitive("trim-data", 
                new TblPrims.TrimDataTable());
        primManager.addPrimitive("use-Bessel?",
                new CorrelPrims.UseBesselCorrection());

        primManager.addPrimitive("correlation",
                new CorrelPrims.CorrelationMatrix());
        primManager.addPrimitive("covariance",
                new CorrelPrims.VarCovarMatrix());

        primManager.addPrimitive("regress-all",
                new RegressionPrims.RegressAll());
        primManager.addPrimitive("regress-on",
                new RegressionPrims.RegressOn());
        primManager.addPrimitive("get-rstats",
                new RegressionPrims.GetRegressionStats());
        primManager.addPrimitive("get-rcstats",
                new RegressionPrims.GetCoefficientStats());

        primManager.addPrimitive("print-data",
                new PrintPrims.ConvertDataToString());
        primManager.addPrimitive("print-covariance",
                new PrintPrims.ConvertCovarToString());
        primManager.addPrimitive("print-correlation",
                new PrintPrims.ConvertCorrelToString());

        primManager.addPrimitive("forecast-linear-growth-at",
                new ForecastPrims.ForecastLinearTrend());
        primManager.addPrimitive("forecast-compound-growth-at",
                new ForecastPrims.ForecastCompoundTrend());
        primManager.addPrimitive("forecast-continuous-growth-at",
                new ForecastPrims.ForecastContinuousTrend());
        primManager.addPrimitive("get-fparameters",
                new ForecastPrims.GetForecastParameters());

        primManager.addPrimitive("means", new DescripPrims.GetMeans());
        primManager.addPrimitive("medians", new DescripPrims.GetMedians());
        primManager.addPrimitive("stddevs", new DescripPrims.GetStdDevs());

        primManager.addPrimitive("quantile", new DescripPrims.Quantile());
        primManager.addPrimitive("quantiles", new DescripPrims.Quantiles());
        primManager.addPrimitive("percentile", new DescripPrims.Percentile());

        primManager.addPrimitive("normal",
                new DescripPrims.NormalDensity());
        primManager.addPrimitive("normal-left",
                new DescripPrims.NormalArea());
        primManager.addPrimitive("normal-inverse",
                new DescripPrims.NormalInverse());

        primManager.addPrimitive("student-left",
                new DescripPrims.StudentArea());
        primManager.addPrimitive("student-inverse",
                new DescripPrims.StudentInverse());

        primManager.addPrimitive("binomial-coefficient",
                new DescripPrims.BinomialCoeff());
        primManager.addPrimitive("binomial-probability",
                new DescripPrims.BinomialProbibility());
        primManager.addPrimitive("binomial-sum-to",
                new DescripPrims.BinomialThroughK());
        primManager.addPrimitive("binomial-sum-above",
                new DescripPrims.BinomialComplemented());

        primManager.addPrimitive("chi-square-left",
                new DescripPrims.ChiSquare());
        primManager.addPrimitive("chi-square-right",
                new DescripPrims.ChiSquareComplemented());
        
        primManager.addPrimitive("gamma", 
                new DescripPrims.GammaFunction());
        primManager.addPrimitive("logGamma", 
                new DescripPrims.LogGammaFunction());
        primManager.addPrimitive("incompleteGamma", 
                new DescripPrims.IncompleteGamma());
        primManager.addPrimitive("incompleteGammaComplement", 
                new DescripPrims.IncompleteGammaComplement());
        primManager.addPrimitive("beta",
                new DescripPrims.BetaFunction());
        primManager.addPrimitive("incompleteBeta",
                new DescripPrims.IncompleteBeta());
        primManager.addPrimitive("bigBeta",
                new DescripPrims.BigBetaFunction());
    }
}