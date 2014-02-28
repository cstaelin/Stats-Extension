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

public class Distributions {

    /* ---------------------------------------------------------------------- */
    public static double PvalueForFstat(double fstat, int dfn, int dfd) {
        // Returns the P value of F statistic fstat with numerator degrees
        // of freedom dfn and denominator degress of freedom dfd.
        // Uses the incomplete Beta function.

        double x = dfd / (dfd + dfn * fstat);
        return Gamma.incompleteBeta(dfd / 2.0, dfn / 2.0, x);
    }

    /* ---------------------------------------------------------------------- */
    public static double PvalueForTstat(double tstat, int df) {
        // Returns the P value of the T statistic tstat with df degrees of
        // freedom. This is a two-tailed test so we just double the right
        // tail which is given by studentT of -|tstat|.

        double x = Math.abs(tstat);
        double p = Probability.studentT((double) df, -x);
        return 2.0 * p;
    }

    /* ---------------------------------------------------------------------- */
    public static double StudentArea(double x, int df) {
        // Returns the area to the left of x in the Student T distribution
        // with the given degrees of freedom.
        return Probability.studentT((double) df, x);
    }

    /* ---------------------------------------------------------------------- */
    public static double NormalArea(double x, double mean, double sd) {
        // Returns the area to the left of x in the normal distribution
        // with the given mean and standard deviation.
        return Probability.normal(mean, sd, x);
    }

    /* ---------------------------------------------------------------------- */
    public static double StudentTInverse(double x, int df) {
        // Returns the value, t, for which the area under the Student-t 
        // probability density function (integrated from minus infinity to t) 
        // is equal to x.
        double a = 2.0 * (1.0 - x);
        return Probability.studentTInverse(a, df);
    }

    /* ---------------------------------------------------------------------- */
    public static double NormalInverse(double area, double mean, double sd) {
        // Returns the x in the normal distribution with the given mean and
        // standard deviation, to the left of which lies the given area.
        // normal.Inverse returns the value in terms of standard deviations
        // from the mean, so we need to adjust it for the given mean and 
        // standard deviation.
        double x = Probability.normalInverse(area);
        return (x + mean) * sd;
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
    public static double BinomialCoeff(int n, int k) {
        // Returns "n choose k" as a double. Note the "integerization" of
        // the double return value.
        return Math.rint(Arithmetic.binomial((long) n, (long) k));
    }

    /* ---------------------------------------------------------------------- */
    public static double BinomialSum(int n, int k, double p) {
        // Returns the sum of the terms 0 through k of the Binomial 
        // probability density, where n is the number of trials and p is 
        // the probability of success in the range 0 to 1.
        return Probability.binomial(k, n, p);
    }

    /* ---------------------------------------------------------------------- */
    public static double BinomialComplemented(int n, int k, double p) {
        // Returns the sum of the terms k+1 through n of the Binomial 
        // probability density, where n is the number of trials and P is
        // the probability of success in the range 0 to 1.
        return Probability.binomialComplemented(k, n, p);
    }

    /* ---------------------------------------------------------------------- */
    public static double ChiSquare(double x, double df) {
        // Returns the area under the left hand tail (from 0 to x) of the 
        // Chi square probability density function with df degrees of freedom.
        return Probability.chiSquare(df, x);
    }

    /* ---------------------------------------------------------------------- */
    public static double ChiSquareComplemented(double x, double df) {
        // Returns the area under the right hand tail (from x to infinity) 
        // of the Chi square probability density function with df degrees 
        // of freedom.
        return Probability.chiSquareComplemented(df, x);
    }
    /* ---------------------------------------------------------------------- */
    public static double Gamma(double x) {
        // Returns the value of the Gamma function at x.
        return Gamma.gamma(x);
    }

    /* ---------------------------------------------------------------------- */
    public static double LogGamma(double x) {
        // Returns the log of the value of the Gamma function at x.
        return Gamma.logGamma(x);
    }

    /* ---------------------------------------------------------------------- */
    public static double IncompleteGamma(double a, double x) {
        // Returns the Gamma function of the argument a to the 
        // integration end point x.

        return Gamma.incompleteGamma(a,x);
    }
    
    /* ---------------------------------------------------------------------- */
    public static double IncompleteGammaComplement(double a, double x) {
        // Returns the complemented incomplete Gamma function of the 
        // argument a and integration start point x.

        return Gamma.incompleteGammaComplement(a,x);
    }
    
    /* ---------------------------------------------------------------------- */
    public static double Beta(double a, double b) {
        // Returns the beta function with arguments a, b.
        
        return Gamma.beta(a, b);
        
    }
    
    /* ---------------------------------------------------------------------- */
    public static double IncompleteBeta(double a, double b, double x) {
        // Returns the integral of the beta function with arguments a and b,
        // from zero to x.
        
        return Gamma.incompleteBeta(a, b, x);
        
    }
    
    /* ---------------------------------------------------------------------- */


}
