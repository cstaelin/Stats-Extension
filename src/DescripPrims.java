/*
 * Implements the reporters that return descriptive statistics, means, medians,
 * quantiles, etc., and that return statistics from the normal, student,
 * binomial and chi-square distributions.
 */
package org.nlogo.extensions.stats;

import org.nlogo.api.LogoException;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Argument;
import org.nlogo.api.Syntax;
import org.nlogo.api.Context;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.api.DefaultReporter;

import cern.colt.list.DoubleArrayList;
import cern.colt.Sorting;
import cern.jet.stat.Descriptive;

public class DescripPrims {

  public static class GetMeans extends DefaultReporter {
    // Returns a list of the means of the variables in the data table.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
              Syntax.ListType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
      return ExtnUtils.convertArrayToSimpleLogoList(tbl.getMeans());
    }
  }

  public static class GetStdDevs extends DefaultReporter {
    // Returns a list of the means of the variables in the data table.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
              Syntax.ListType());
    }

    @Override
    public Object report(Argument args[], Context context)
            throws ExtensionException, LogoException {
      LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
      return ExtnUtils.convertArrayToSimpleLogoList(tbl.getStdDevs());
    }
  }

  public static class GetMedians extends DefaultReporter {
    // Returns a list of the means of the variables in the data table.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
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

  public static class Quantile extends DefaultReporter {
    // Returns the "pcnt" quantile break of the variable given by "var".

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.WildcardType(),
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
  public static class Quantiles extends DefaultReporter {
    // Returns the n + 1 quantile breaks of the variable given by "var".

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.WildcardType(),
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
        throw new ExtensionException("The number of quantiles must"
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
  public static class Percentile extends DefaultReporter {
    // Returns the "pcnt" quantile break of the variable given by "var".

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.WildcardType(),
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
  public static class StudentArea extends DefaultReporter {
    // Returns the area to the left of x in the Student T distribution
    // with the given degrees of freedom.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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
      return Distributions.StudentArea(x, df);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class NormalArea extends DefaultReporter {
    // Returns the area to the left of x in the Normal distribution.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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
      return Distributions.NormalArea(x, m, s);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class StudentInverse extends DefaultReporter {
    // Returns the value, t, for which the area under the Student-t 
    // probability density function (integrated from minus infinity to t) 
    // is equal to x.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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
      return Distributions.StudentTInverse(x, df);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class NormalInverse extends DefaultReporter {
    // Returns the x in the normal distribution with the given mean and
    // standard deviation, to the left of which lies the given area.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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
      return Distributions.NormalInverse(x, m, s);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class NormalDensity extends DefaultReporter {
    // Returns the probability of x in the normal distribution with the 
    // given mean and standard deviation.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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
      return Distributions.NormalDensity(x, m, s);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class BinomialCoeff extends DefaultReporter {
    // Returns the binomial coefficient: n choose k.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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
      return Distributions.BinomialCoeff(n, k);
    }
  }
  /* ---------------------------------------------------------------------- */

  public static class BinomialProbibility extends DefaultReporter {
    // Returns the binomial probibility of exactly k successes in
    // n trials, each with probability p.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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
      return Distributions.BinomialCoeff(n, k)
              * Math.pow(p, k) * Math.pow((1.0 - p), (n - k));
    }
  }
  /* ---------------------------------------------------------------------- */

  public static class BinomialThroughK extends DefaultReporter {
    // Returns the sum of the terms 0 through k of the Binomial 
    // probability density, where n is the number of trials and p is 
    // the probability of success as a fraction, in the range 0 to 1.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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
      return Distributions.BinomialSum(n, k, p);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class BinomialComplemented extends DefaultReporter {
    // Returns the sum of the terms k+1 through n of the Binomial 
    // probability density, where n is the number of trials and P is
    // the probability of success as a fraction in the range 0 to 1.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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
      return Distributions.BinomialComplemented(n, k, p);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class ChiSquare extends DefaultReporter {
    // Returns the area under the left hand tail (from 0 to x) of the 
    // Chi square probability density function with df degrees of freedom.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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
      if (x < 0.0 | df <= 0) {
        // Check the arguments.
        throw new ExtensionException("The ChiSquare arguments "
                + "must be positive");
      }
      return Distributions.ChiSquare(x, df);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class ChiSquareComplemented extends DefaultReporter {
    // Returns the area under the right hand tail (from x to infinity) 
    // of the Chi square probability density function with df degrees 
    // of freedom.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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
      return Distributions.ChiSquareComplemented(x, df);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class GammaFunction extends DefaultReporter {
    // Returns the gamma function of the argument a.
    // Although gamma can take negative values for a, it is not defined for
    // negative integer values. So, hopefully without any loss of usefullness,
    // we require that a be positive.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType()},
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
      return Distributions.Gamma(a);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class LogGammaFunction extends DefaultReporter {
    // Returns the natural log of the gamma function of the argument a.
    // Although gamma can take negative values for a, it is not defined for
    // negative integer values. So, hopefully without any loss of usefullness,
    // we require that a be positive.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType()},
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
      return Distributions.LogGamma(a);
    }
  }
  /* ---------------------------------------------------------------------- */

  public static class IncompleteGamma extends DefaultReporter {
    // Returns the regularized incomplete gamma function with parameter a to the 
    // integration end point x.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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

      if (a <= 0.0 | x < 0.0) {
        // Arguments must be positive.
        throw new ExtensionException("The arguments to incompleteGamma "
                + "must be positive");
      }
      return Distributions.IncompleteGamma(a, x);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class IncompleteGammaComplement extends DefaultReporter {
    // Returns the complement of the regularized incomplete gamma function with 
    // parameter a integration start point x.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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

      if (a <= 0.0 | x < 0.0) {
        // Arguments must be positive.
        throw new ExtensionException("The arguments to incompleteGamma"
                + "Complement must be positive");
      }
      return Distributions.IncompleteGammaComplement(a, x);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class BetaFunction extends DefaultReporter {
    // Returns the value of the beta function with the arguments a and b.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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

      if (a <= 0.0 | b <= 0.0) {
        // Argument must be positive.
        throw new ExtensionException("The arguments to beta "
                + "must be positive");
      }
      return Distributions.Beta(a, b);
    }
  }

   /* ---------------------------------------------------------------------- */
  public static class BigBetaFunction extends DefaultReporter {
    // Returns the value of the beta function with the arguments a and b.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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

      if (a <= 0.0 | b <= 0.0) {
        // Argument must be positive.
        throw new ExtensionException("The arguments to beta "
                + "must be positive");
      }
      return Math.exp(Distributions.LogGamma(a) + Distributions.LogGamma(b)
              - Distributions.LogGamma(a + b));
    }
  }

  /* ---------------------------------------------------------------------- */
  public static class IncompleteBeta extends DefaultReporter {
    // Returns the regularized integral of the beta function with 
    // arguments a and b, from zero to x.

    @Override
    public Syntax getSyntax() {
      return Syntax.reporterSyntax(new int[]{Syntax.NumberType(),
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

      if (a <= 0.0 | b <= 0.0 | x < 0.0 | x > 1.0) {
        // Argument must be positive.
        throw new ExtensionException("The arguments to incompleteBeta "
                + "must be positive, and the integration point must be "
                + "between 0 and 1 inclusive.");
      }
      return Distributions.IncompleteBeta(a, b, x);
    }
  }

  /* ---------------------------------------------------------------------- */
}
