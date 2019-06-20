/*
 * Contains the CERN implementations of useful statistical distributions.
 * The calls to CERN's colt.jar routines are collected here so that they can 
 * be swapped out if a better stats package is found.
 * 
 * CERN's colt.jar distribution includes the following license information.
 * Copyright (c) 1999 CERN - European Organization for Nuclear Research.  
 * Permission to use, copy, modify, distribute and sell this software and 
 * its documentation for any purpose is hereby granted without fee, provided 
 * that the above copyright notice appear in all copies and that both that 
 * copyright notice and this permission notice appear in supporting 
 * documentation. CERN makes no representations about the suitability of 
 * this software for any purpose. It is provided "as is" without expressed 
 * or implied warranty.
 */
package org.nlogo.extensions.stats;

import cern.jet.stat.Gamma;
import cern.jet.stat.Probability;
import cern.jet.math.Arithmetic;

import org.nlogo.api.*;

public class Distributions {

  /* ---------------------------------------------------------------------- */
  public static double pValueForFstat(double fstat, int dfn, int dfd)
          throws ExtensionException {
    // Returns the P value of F statistic fstat with numerator degrees
    // of freedom dfn and denominator degress of freedom dfd.
    // Uses the incomplete getBeta function.

    double x = dfd / (dfd + dfn * fstat);
    try {
      return Gamma.incompleteBeta(dfd / 2.0, dfn / 2.0, x);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .incompleteBeta reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double pValueForTstat(double tstat, int df)
          throws ExtensionException {
    // Returns the P value of the T statistic tstat with df degrees of
    // freedom. This is a two-tailed test so we just double the right
    // tail which is given by studentT of -|tstat|.

    double x = Math.abs(tstat);
    try {
      double p = Probability.studentT((double) df, -x);
      return 2.0 * p;
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .studentT reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getStudentArea(double x, int df)
          throws ExtensionException {
    // Returns the area to the left of x in the Student T distribution
    // with the given degrees of freedom.
    try {
      return Probability.studentT((double) df, x);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .studentT reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getNormalArea(double x, double mean, double sd)
          throws ExtensionException {
    // Returns the area to the left of x in the normal distribution
    // with the given mean and standard deviation.
    try {
      return Probability.normal(mean, sd, x);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .normal reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getStudentTInverse(double area, int df)
          throws ExtensionException {
    // Returns the value, t, for which the area under the Student-t 
    // probability density function (integrated from minus infinity to t) 
    // is equal to area.
    if (area <= 0.0 || area >= 1.0) {
      throw new ExtensionException("The area parameter in student-inverse "
              + " must be greater than 0.0 and less than 1.0.");
    }
    double a = 2.0 * (1.0 - area);

    try {
      return Probability.studentTInverse(a, df);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .studentTInverse reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getNormalInverse(double area, double mean, double sd)
          throws ExtensionException {
    // Returns the x in the normal distribution with the given mean and
    // standard deviation, to the left of which lies the given area.
    // normal.Inverse returns the value in terms of standard deviations
    // from the mean, so we need to adjust it for the given mean and 
    // standard deviation. Note that the area must be strictly greater than
    // zero and strictly less than 1.0.
    if (area <= 0.0 || area >= 1.0) {
      throw new ExtensionException("The area parameter in normal-inverse "
      + " must be greater than 0.0 and less than 1.0.");
    }
    try {
      double x = Probability.normalInverse(area);
      return (x + mean) * sd;
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .normalInverse reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getNormalDensity(double x, double mean, double sd) {
    // Returns the probability of x in the normal distribution with the 
    // given mean and standard deviation.
    double var = sd * sd;
    double c = 1.0 / Math.sqrt(2.0 * Math.PI * var);
    double b = ((x - mean) * (x - mean)) / (2.0 * var);
    return c * Math.exp(-b);
  }

  /* ---------------------------------------------------------------------- */
  public static double getBinomialCoeff(int n, int k) throws ExtensionException {
    // Returns "n choose k" as a double. Note the "integerization" of
    // the double return value.
    try {
    return Math.rint(Arithmetic.binomial((long) n, (long) k));
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .Arithmetic.binomial reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getBinomialSum(int n, int k, double p)
          throws ExtensionException {
    // Returns the sum of the terms 0 through k of the Binomial 
    // probability density, where n is the number of trials and p is 
    // the probability of success in the range 0 to 1.
    try {
      return Probability.binomial(k, n, p);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt Probability.binomial reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt Probability.normal reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getBinomialComplemented(int n, int k, double p)
          throws ExtensionException {
    // Returns the sum of the terms k+1 through n of the Binomial 
    // probability density, where n is the number of trials and P is
    // the probability of success in the range 0 to 1.
    try {
      return Probability.binomialComplemented(k, n, p);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .binomialComplement reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getChiSquare(double x, double df)
          throws ExtensionException {
    // Returns the area under the left hand tail (from 0 to x) of the 
    // Chi square probability density function with df degrees of freedom.
    try {
      return Probability.chiSquare(df, x);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .chiSquare reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getChiSquareComplemented(double x, double df)
          throws ExtensionException {
    // Returns the area under the right hand tail (from x to infinity) 
    // of the Chi square probability density function with df degrees 
    // of freedom.
    try {
      return Probability.chiSquareComplemented(df, x);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .chiSquareComplemented reports: " + ex);
    }
  }
  /* ---------------------------------------------------------------------- */
  public static double getGamma(double x) throws ExtensionException {
    // Returns the value of the getGamma function at x.
    try {
      return Gamma.gamma(x);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .gamma reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getLogGamma(double x) throws ExtensionException {
    // Returns the log of the value of the getGamma function at x.
    try {
      return Gamma.logGamma(x);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .logGamma reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getIncompleteGamma(double a, double x)
          throws ExtensionException {
    // Returns the regularized integral of the getGamma function with argument
    // a to the integration end point x.
    try {
      return Gamma.incompleteGamma(a, x);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .incompleteGamma reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getIncompleteGammaComplement(double a, double x)
          throws ExtensionException {
    // Returns the complemented regularized incomplete getGamma function of the 
    // argument a and integration start point x.
    try {
      return Gamma.incompleteGammaComplement(a, x);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .incompleteGammaComplement reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getBeta(double a, double b) throws ExtensionException {
    // Returns the beta function with arguments a, b.
    try {
      return Gamma.beta(a, b);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .beta reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double getIncompleteBeta(double a, double b, double x)
          throws ExtensionException {
    // Returns the regularized integral of the beta function with arguments
    // a and b, from zero to x.
    try {
      return Gamma.incompleteBeta(a, b, x);
    } catch (IllegalArgumentException | ArithmeticException ex) {
      throw new ExtensionException("colt .incompleteBeta reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
}
