/*
 * Implements the reporters that return descriptive statistics, means, medians,
 * quantiles, etc., and that return statistics from the normal, student,
 * binomial and chi-square distributions.
 */
package org.nlogo.extensions.stats;

import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import cern.colt.list.DoubleArrayList;
import cern.colt.Sorting;
import cern.jet.stat.Descriptive;

public class DescripPrims {

  public static class GetMeans implements Reporter {
    // Returns a list of the means of the variables in the data table.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.WildcardType()},
              Syntax.ListType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
      return ExtnUtils.convertArrayToSimpleLogoList(tbl.getMeans());
    }
  }

  public static class GetStdDevs implements Reporter {
    // Returns a list of the means of the variables in the data table.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.WildcardType()},
              Syntax.ListType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
      return ExtnUtils.convertArrayToSimpleLogoList(tbl.getStdDevs());
    }
  }

  public static class GetMedians implements Reporter {
    // Returns a list of the means of the variables in the data table.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.WildcardType()},
              Syntax.ListType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
      LogoListBuilder lst = new LogoListBuilder();
      for (int i = 0; i < tbl.getNVars(); i++) {
        double[] X = tbl.getColumn(i, true);
        Sorting.mergeSort(X, 0, X.length);
        lst.add(Descriptive.median(new DoubleArrayList(X)));
      }
      return lst.toLogoList();
    }
  }

  public static class Quantile implements Reporter {
    // Returns the "pcnt" quantile break of the variable given by "var".

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.WildcardType(),
        Syntax.WildcardType(), Syntax.NumberType()},
              Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
      int varNumber = ExtnUtils.getVarNumberFromArg(tbl, args[1]);
      double pcnt;
      try {
        pcnt = args[2].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      pcnt /= 100.0;
      if (pcnt < 0.0 || pcnt > 100.0) {
        throw new ExtensionException("The percent must be between"
                + " 0.0 and 100.0, inclusive.");
      }
      double[] X = tbl.getColumn(varNumber, true);
      Sorting.mergeSort(X, 0, X.length);
      return Descriptive.quantile(new DoubleArrayList(X), pcnt);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class Quantiles implements Reporter {
    // Returns the n + 1 quantile breaks of the variable given by "var".

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.WildcardType(),
        Syntax.WildcardType(), Syntax.NumberType()},
              Syntax.ListType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
      int varNumber = ExtnUtils.getVarNumberFromArg(tbl, args[1]);
      int n;
      try {
        n = args[2].getIntValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      if (n < 0) {
        throw new ExtensionException("The number of quantiles must be"
                + " greater or equal to zero.");
      }
      double incr = 1.0 / n;
      double[] breaks = new double[n + 1];
      for (int i = 0; i < n; i++) {
        breaks[i] = incr * i;
      }
      breaks[n] = 1.0;
      double[] X = tbl.getColumn(varNumber, true);
      Sorting.mergeSort(X, 0, X.length);
      DoubleArrayList results
              = Descriptive.quantiles(new DoubleArrayList(X),
                      new DoubleArrayList(breaks));
      return ExtnUtils.convertVectorToSimpleLogoList(results.elements());
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class Percentile implements Reporter {
    // Returns the "pcnt" quantile break of the variable given by "var".

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.WildcardType(),
        Syntax.WildcardType(), Syntax.NumberType()},
              Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
      int varNumber = ExtnUtils.getVarNumberFromArg(tbl, args[1]);
      double nmbr;
      try {
        nmbr = args[2].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      double[] X = tbl.getColumn(varNumber, true);
      Sorting.mergeSort(X, 0, X.length);
      return Descriptive.quantileInverse(new DoubleArrayList(X), nmbr) * 100.0;
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class StudentArea implements Reporter {
    // Returns the area to the left of x in the Student T distribution
    // with the given degrees of freedom.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType()}, Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double x;
      int df;
      try {
        x = args[0].getDoubleValue();
        df = args[1].getIntValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      return Distributions.getStudentArea(x, df);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class NormalArea implements Reporter {
    // Returns the area to the left of x in the Normal distribution.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType(), Syntax.NumberType()},
              Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double x;
      double m;
      double s;
      try {
        x = args[0].getDoubleValue();
        m = args[1].getDoubleValue();
        s = args[2].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      return Distributions.getNormalArea(x, m, s);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class StudentInverse implements Reporter {
    // Returns the value, t, for which the area under the Student-t 
    // probability density function (integrated from minus infinity to t) 
    // is equal to x.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType()}, Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double x;
      int df;
      try {
        x = args[0].getDoubleValue();
        df = args[1].getIntValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      return Distributions.getStudentTInverse(x, df);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class NormalInverse implements Reporter {
    // Returns the x in the normal distribution with the given mean and
    // standard deviation, to the left of which lies the given area.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType(), Syntax.NumberType()},
              Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double x;
      double m;
      double s;
      try {
        x = args[0].getDoubleValue();
        m = args[1].getDoubleValue();
        s = args[2].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      return Distributions.getNormalInverse(x, m, s);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class NormalDensity implements Reporter {
    // Returns the probability of x in the normal distribution with the 
    // given mean and standard deviation.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType(), Syntax.NumberType()},
              Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double x;
      double m;
      double s;
      try {
        x = args[0].getDoubleValue();
        m = args[1].getDoubleValue();
        s = args[2].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      return Distributions.getNormalDensity(x, m, s);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class BinomialCoeff implements Reporter {
    // Returns the binomial coefficient: n choose k.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType()}, Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      int n;
      int k;
      try {
        n = args[0].getIntValue();
        k = args[1].getIntValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      return Distributions.getBinomialCoeff(n, k);
    }
  }
  /* ---------------------------------------------------------------------- */

  public static class BinomialProbibility implements Reporter {
    // Returns the binomial probibility of exactly k successes in
    // n trials, each with probability p.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType(), Syntax.NumberType()},
              Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      int n;
      int k;
      double p;
      try {
        n = args[0].getIntValue();
        k = args[1].getIntValue();
        p = args[2].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      return Distributions.getBinomialCoeff(n, k)
              * Math.pow(p, k) * Math.pow((1.0 - p), (n - k));
    }
  }
  /* ---------------------------------------------------------------------- */

  public static class BinomialThroughK implements Reporter {
    // Returns the sum of the terms 0 through k of the Binomial 
    // probability density, where n is the number of trials and p is 
    // the probability of success as a fraction, in the range 0 to 1.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType(), Syntax.NumberType()},
              Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      int n;
      int k;
      try {
        n = args[0].getIntValue();
        k = args[1].getIntValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      double p = args[2].getDoubleValue();
      return Distributions.getBinomialSum(n, k, p);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class BinomialComplemented implements Reporter {
    // Returns the sum of the terms k+1 through n of the Binomial 
    // probability density, where n is the number of trials and P is
    // the probability of success as a fraction in the range 0 to 1.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType(), Syntax.NumberType()},
              Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      int n;
      int k;
      try {
        n = args[0].getIntValue();
        k = args[1].getIntValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      double p = args[2].getDoubleValue();
      return Distributions.getBinomialComplemented(n, k, p);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class ChiSquare implements Reporter {
    // Returns the area under the left hand tail (from 0 to x) of the 
    // Chi square probability density function with df degrees of freedom.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType()}, Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double x;
      double df;
      try {
        x = args[0].getDoubleValue();
        df = args[1].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      if (x < 0.0 || df <= 0) {
        // Check the arguments.
        throw new ExtensionException("The ChiSquare arguments "
                + "must be positive");
      }
      return Distributions.getChiSquare(x, df);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class ChiSquareComplemented implements Reporter {
    // Returns the area under the right hand tail (from x to infinity) 
    // of the Chi square probability density function with df degrees 
    // of freedom.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType()}, Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double x;
      double df;
      try {
        x = args[0].getDoubleValue();
        df = args[1].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      return Distributions.getChiSquareComplemented(x, df);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class GammaFunction implements Reporter {
    // Returns the gamma function of the argument a.
    // Although gamma can take negative values for a, it is not defined for
    // negative integer values. So, hopefully without any loss of usefullness,
    // we require that a be positive.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType()},
              Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double a;
      try {
        a = args[0].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }

      if (a <= 0.0) {
        // Argument must be positive.
        throw new ExtensionException("The argument to gamma "
                + "must be positive");
      }
      return Distributions.getGamma(a);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class LogGammaFunction implements Reporter {
    // Returns the natural log of the gamma function of the argument a.
    // Although gamma can take negative values for a, it is not defined for
    // negative integer values. So, hopefully without any loss of usefullness,
    // we require that a be positive.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType()},
              Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double a;
      try {
        a = args[0].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }

      if (a <= 0.0) {
        // Argument must be positive.
        throw new ExtensionException("The argument to logGamma "
                + "must be positive");
      }
      return Distributions.getLogGamma(a);
    }
  }
  /* ---------------------------------------------------------------------- */

  public static class IncompleteGamma implements Reporter {
    // Returns the regularized incomplete gamma function with parameter a to the 
    // integration end point x.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType()}, Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double a;
      double x;
      try {
        a = args[0].getDoubleValue();
        x = args[1].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }

      if (a <= 0.0 || x < 0.0) {
        // Arguments must be positive.
        throw new ExtensionException("The arguments to incompleteGamma "
                + "must be positive");
      }
      return Distributions.getIncompleteGamma(a, x);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class IncompleteGammaComplement implements Reporter {
    // Returns the complement of the regularized incomplete gamma function with 
    // parameter a integration start point x.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType()}, Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double a;
      double x;
      try {
        a = args[0].getDoubleValue();
        x = args[1].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }

      if (a <= 0.0 || x < 0.0) {
        // Arguments must be positive.
        throw new ExtensionException("The arguments to incompleteGamma"
                + "Complement must be positive");
      }
      return Distributions.getIncompleteGammaComplement(a, x);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class BetaFunction implements Reporter {
    // Returns the value of the beta function with the arguments a and b.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType()}, Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double a;
      double b;
      try {
        a = args[0].getDoubleValue();
        b = args[1].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }

      if (a <= 0.0 || b <= 0.0) {
        // Argument must be positive.
        throw new ExtensionException("The arguments to beta "
                + "must be positive");
      }
      return Distributions.getBeta(a, b);
    }
  }

   /* ---------------------------------------------------------------------- */
  public static class BigBetaFunction implements Reporter {
    // Returns the value of the beta function with the arguments a and b.
    // Because this is done in logs, a and b can be very large numbers.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType()}, Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double a;
      double b;
      try {
        a = args[0].getDoubleValue();
        b = args[1].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }

      if (a <= 0.0 || b <= 0.0) {
        // Argument must be positive.
        throw new ExtensionException("The arguments to beta "
                + "must be positive");
      }
      return Math.exp(Distributions.getLogGamma(a) + Distributions.getLogGamma(b)
              - Distributions.getLogGamma(a + b));
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class IncompleteBeta implements Reporter {
    // Returns the regularized integral of the beta function with 
    // arguments a and b, from zero to x.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType(), Syntax.NumberType()}, Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double a;
      double b;
      double x;
      try {
        a = args[0].getDoubleValue();
        b = args[1].getDoubleValue();
        x = args[2].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }

      if (a <= 0.0 || b <= 0.0 || x < 0.0 || x > 1.0) {
        // Argument must be positive.
        throw new ExtensionException("The arguments to incompleteBeta "
                + "must be positive, and the integration point must be "
                + "between 0 and 1 inclusive.");
      }
      return Distributions.getIncompleteBeta(a, b, x);
    }
  }

  /* ---------------------------------------------------------------------- */
  
  public static class CDFLogNormal implements Reporter {
  // Returns the area to the left of x in the LogNormal distribution
  // with the given location and size parameters.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType(), Syntax.NumberType()},
              Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double x;
      double m;
      double s;
      try {
        x = args[0].getDoubleValue();
        m = args[1].getDoubleValue();
        s = args[2].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      double y = (Math.log(x) - m) / s;
      return Distributions.getNormalArea(y, 0.0, 1.0);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class PDFLogNormal implements Reporter {
    // Returns the probability of x in the LogNormal distribution with the 
    // given location and size parameters.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType(), Syntax.NumberType()},
              Syntax.NumberType());
    }

    public final static double C = Math.sqrt(2 * Math.PI);

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double x;
      double m;
      double s;
      try {
        x = args[0].getDoubleValue();
        m = args[1].getDoubleValue();
        s = args[2].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      double y = (Math.log(x) - m) / s;
      return Math.exp(-y * y / 2) / (x * C * s);

    }
  }

  /* ---------------------------------------------------------------------- */
    
  public static class CDFInverseLogNormal implements Reporter {
    // Returns the x in the LogNormal distribution with the given location and
    // size, to the left of which lies the given area.

    @Override
    public Syntax getSyntax() {
      return SyntaxJ.reporterSyntax(new int[]{Syntax.NumberType(),
        Syntax.NumberType(), Syntax.NumberType()},
              Syntax.NumberType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      double a;
      double m;
      double s;
      try {
        a = args[0].getDoubleValue();
        m = args[1].getDoubleValue();
        s = args[2].getDoubleValue();
      } catch (LogoException e) {
        throw new ExtensionException(e.getMessage());
      }
      if (a <= 0.0 ) {
        return 0.0;
      }
      if (a >= 1.0) {
        throw new ExtensionException("The area parameter in lognormal-inverse "
                + " must be less than 1.0.");
      }
      return Math.exp(m + Distributions.getNormalInverse(a, 0, 1) * s);
    }
  }

  /* ---------------------------------------------------------------------- */
}
