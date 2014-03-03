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

import org.nlogo.api.ExtensionException;

public class Distributions {

  /* ---------------------------------------------------------------------- */
  public static double PvalueForFstat(double fstat, int dfn, int dfd)
          throws ExtensionException {
    // Returns the P value of F statistic fstat with numerator degrees
    // of freedom dfn and denominator degress of freedom dfd.
    // Uses the incomplete Beta function.

    double x = dfd / (dfd + dfn * fstat);
    try {
      return Gamma.incompleteBeta(dfd / 2.0, dfn / 2.0, x);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .incompleteBeta reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .incompleteBeta reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double PvalueForTstat(double tstat, int df)
          throws ExtensionException {
    // Returns the P value of the T statistic tstat with df degrees of
    // freedom. This is a two-tailed test so we just double the right
    // tail which is given by studentT of -|tstat|.

    double x = Math.abs(tstat);
    try {
      double p = Probability.studentT((double) df, -x);
      return 2.0 * p;
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .studentT reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .studentT reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double StudentArea(double x, int df)
          throws ExtensionException {
    // Returns the area to the left of x in the Student T distribution
    // with the given degrees of freedom.
    try {
      return Probability.studentT((double) df, x);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .studentT reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .studentT reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double NormalArea(double x, double mean, double sd)
          throws ExtensionException {
    // Returns the area to the left of x in the normal distribution
    // with the given mean and standard deviation.
    try {
      return Probability.normal(mean, sd, x);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .normal reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .normal reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double StudentTInverse(double x, int df)
          throws ExtensionException {
    // Returns the value, t, for which the area under the Student-t 
    // probability density function (integrated from minus infinity to t) 
    // is equal to x.
    double a = 2.0 * (1.0 - x);
    try {
      return Probability.studentTInverse(a, df);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .studentTInverse reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .studentTInverse reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double NormalInverse(double area, double mean, double sd)
          throws ExtensionException {
    // Returns the x in the normal distribution with the given mean and
    // standard deviation, to the left of which lies the given area.
    // normal.Inverse returns the value in terms of standard deviations
    // from the mean, so we need to adjust it for the given mean and 
    // standard deviation.
    try {
      double x = Probability.normalInverse(area);
      return (x + mean) * sd;
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .normalInverse reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .normalInverse reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double NormalDensity(double x, double mean, double sd) {
    // Returns the probability of x in the normal distribution with the 
    // given mean and standard deviation.
    double var = sd * sd;
    double c = 1.0 / Math.sqrt(2.0 * Math.PI * var);
    double b = ((x - mean) * (x - mean)) / (2.0 * var);
    return c * Math.exp(-b);
  }

  /* ---------------------------------------------------------------------- */
  public static double BinomialCoeff(int n, int k) throws ExtensionException {
    // Returns "n choose k" as a double. Note the "integerization" of
    // the double return value.
    try {
    return Math.rint(Arithmetic.binomial((long) n, (long) k));
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .Arithmetic.binomial reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .Arithmetic.binomial reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double BinomialSum(int n, int k, double p)
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
  public static double BinomialComplemented(int n, int k, double p)
          throws ExtensionException {
    // Returns the sum of the terms k+1 through n of the Binomial 
    // probability density, where n is the number of trials and P is
    // the probability of success in the range 0 to 1.
    try {
      return Probability.binomialComplemented(k, n, p);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .binomialComplement reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .binomialComplement reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double ChiSquare(double x, double df)
          throws ExtensionException {
    // Returns the area under the left hand tail (from 0 to x) of the 
    // Chi square probability density function with df degrees of freedom.
    try {
      return Probability.chiSquare(df, x);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .chiSquare reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .chiSquare reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double ChiSquareComplemented(double x, double df)
          throws ExtensionException {
    // Returns the area under the right hand tail (from x to infinity) 
    // of the Chi square probability density function with df degrees 
    // of freedom.
    try {
      return Probability.chiSquareComplemented(df, x);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .chiSquareComplemented reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .chiSquareComplemented reports: " + ex);
    }
  }
  /* ---------------------------------------------------------------------- */
  public static double Gamma(double x) throws ExtensionException {
    // Returns the value of the Gamma function at x.
    try {
      return Gamma.gamma(x);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .gamma reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .gamma reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double LogGamma(double x) throws ExtensionException {
    // Returns the log of the value of the Gamma function at x.
    try {
      return Gamma.logGamma(x);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .logGamma reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .logGamma reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double IncompleteGamma(double a, double x)
          throws ExtensionException {
    // Returns the regularized integral of the Gamma function with argument
    // a to the integration end point x.
    try {
      return Gamma.incompleteGamma(a, x);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .incompleteGamma reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .incompleteGamma reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double IncompleteGammaComplement(double a, double x)
          throws ExtensionException {
    // Returns the complemented regularized incomplete Gamma function of the 
    // argument a and integration start point x.
    try {
      return Gamma.incompleteGammaComplement(a, x);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .incompleteGammaComplement reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .incompleteGammaComplement reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double Beta(double a, double b) throws ExtensionException {
    // Returns the beta function with arguments a, b.
    try {
      return Gamma.beta(a, b);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .beta reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .beta reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
  public static double IncompleteBeta(double a, double b, double x)
          throws ExtensionException {
    // Returns the regularized integral of the beta function with arguments
    // a and b, from zero to x.
    try {
      return Gamma.incompleteBeta(a, b, x);
    } catch (IllegalArgumentException ex) {
      throw new ExtensionException("colt .incompleteBeta reports: " + ex);
    } catch (ArithmeticException ex) {
      throw new ExtensionException("colt .incompleteBeta reports: " + ex);
    }
  }

  /* ---------------------------------------------------------------------- */
}
