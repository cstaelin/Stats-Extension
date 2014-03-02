/*
 * Defines the data structure (class) for the data table and the methods for
 * interacting with it.  There is some NetLogo housekeeping here as well.
 */
package org.nlogo.extensions.stats;

import org.nlogo.api.ExtensionException;

public class LogoStatsTbl implements org.nlogo.api.ExtensionObject {
    // NetLogo data types defined in extensions must implement
    // the ExtensionObject interface, and thus the methods that come 
    // immediately after the data structure definition.

    private static int nrowsIncrement = 10;
    private int nvars = 0;
    private int nrows = 0;
    private int nobs = 0;
    private int useObs = 0;
    private boolean dataChanged = true;
    private String[] names = null;
    private Jama.Matrix data = null;
    private Jama.Matrix covar = null;
    private Jama.Matrix correl = null;
    private Jama.Matrix means = null;
    private Jama.Matrix stddevs = null;
    private boolean useBessel = true;
    private boolean BesselChanged = false;
    private int[] rVars = null;
    private Jama.Matrix rCoeffs = null;
    private Jama.Matrix rSEBeta = null;
    private Jama.Matrix rTBeta = null;
    private Jama.Matrix rTBetaP = null;
    private double rSST = 0.0;
    private double rSSR = 0.0;
    private double rSSE = 0.0;
    private double rR2stat = 0.0;
    private double rAdjR2stat = 0.0;
    private double rFstat = 0.0;
    private double rFstatP = 0.0;
    private double rStdErrEst = 0.0;
    private int rDFT = 0;
    private int rDFR = 0;
    private int rDFE = 0;
    private Jama.Matrix fCoeffs = null;
    private final long id;


    /* ====================================================================== */
    // Required by implementation of org.nlogo.api.ExtensionObject.
    /* ====================================================================== */
    @Override
    public String getExtensionName() {
        return "stats";
    }

    @Override
    public String getNLTypeName() {
        return "LogoStatsTbl";
    }

    @Override
    public String dump(boolean readable, boolean exporting, boolean reference) {
        // Not yet implemented. Code sample commented out below.
        return null;
    }
//    public String dump(boolean readable, boolean exporting, boolean reference) {
//        StringBuilder buf = new StringBuilder();
//        if (exporting) {
//            buf.append(id);
//            if (!reference) {
//                buf.append(":");
//            }
//        }
//            if (!(reference && exporting)) {
//                double[][] dArray = this.data.getArray();
//                buf.append(" [ ");
//                for (int i = 0; i < dArray.length; i++) {
//                    buf.append("[");
//                    for (int j = 0; j < dArray[i].length; j++) {
//                        buf.append(" ");
//                        buf.append(org.nlogo.api.Dump.number(dArray[i][j]));
//                    }
//                    buf.append(" ]");
//                }
//                buf.append(" ]");
//            }
//            return buf.toString();
//        }

    /* ====================================================================== */
    // The set of constructors.
    /* ====================================================================== */
    // This is the main constructor.
    LogoStatsTbl() {
        this.id = StatsExtension.next;
        StatsExtension.LogoStatsTbls.put(this, id);
        StatsExtension.next++;
    }

    // This constructor takes as its argument a set of data in 
    // matrix format.
    LogoStatsTbl(Jama.Matrix matrixData) {
        data = matrixData;
        nvars = data.getColumnDimension();
        nrows = data.getRowDimension();
        nobs = nrows;
        this.id = StatsExtension.next;
        StatsExtension.LogoStatsTbls.put(this, id);
        StatsExtension.next++;
    }

    // This constructor is used during importWorld.
    LogoStatsTbl(long id) {
        this.id = id;
        StatsExtension.LogoStatsTbls.put(this, id);
        StatsExtension.next = StrictMath.max(StatsExtension.next, id + 1);
    }

    /* ====================================================================== */
    /* ====================================================================== */
    // This is a very shallow "equals" method. see recursivelyEqual()
    // for deep equality.
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    // A "deep" equals method.
    // THIS NEEDS TO BE CONSIDERABLY EXPANDED FOR OUR DATA STRUCTURE
    @Override
    public boolean recursivelyEqual(Object o) {
        if (!(o instanceof LogoStatsTbl)) {
            return false;
        }
        LogoStatsTbl otherTbl = (LogoStatsTbl) o;
        double[][] otherArray = otherTbl.data.getArray();
        return java.util.Arrays.deepEquals(data.getArray(), otherArray);
    }

    /* ====================================================================== */
    /* ====================================================================== */
    //    @Override
//    public void clearAll() {
//        StatsExtension.LogoStatsTbls.clear();
//        StatsExtension.next = 0;
//    }
//    @Override    
//    public StringBuilder exportWorld() {
//        StringBuilder buffer = new StringBuilder();
//        for (LogoStatsTbl mat : LogoStatsTbls.keySet()) {
//            buffer.append(org.nlogo.api.Dump.csv().encode(org.nlogo.api.Dump.extensionObject(mat, true, true, false)) + "\n");
//        }
//        return buffer;
//    }
//    @Override    
//    public void importWorld(java.util.List<String[]> lines, org.nlogo.api.ExtensionManager reader,
//            org.nlogo.api.ImportErrorHandler handler) {
//        for (String[] line : lines) {
//            try {
//                reader.readFromString(line[0]);
//            } catch (CompilerException e) {
//                handler.showError("Error importing StatsTbls", e.getMessage(), "This matrix will be ignored");
//            }
//        }
//    }
//    @Override
//    public org.nlogo.api.ExtensionObject readExtensionObject(org.nlogo.api.ExtensionManager reader,
//            String typeName, String value)
//            throws CompilerException, ExtensionException {
//        String[] s = value.split(":");
//        long id = Long.parseLong(s[0]);
//        LogoStatsTbl mat = getOrCreateMatrixFromId(id);
//        if (s.length > 1) {
//            LogoList nestedL = (LogoList) reader.readFromString(s[1]);
//            double[][] newData = convertNestedLogoListToArray(nestedL);
//            mat.replaceData(newData);
//        }
//        return mat;
//    }
//    
//     Used during import world, to recreate StatsTbls with the
//     correct id numbers, so all the references match up.
//     @param id
//     @return
//    
//    private LogoStatsTbl getOrCreateMatrixFromId(long id) {
//        for (LogoStatsTbl mat : LogoStatsTbls.keySet()) {
//            if (mat.id == id) {
//                return mat;
//            }
//        }
//        return new LogoStatsTbl(id);
//    }
    /* ====================================================================== */
    // Here are the various working methods defined for this class - its 
    // interface to the outside world.
    /* ====================================================================== */
    // This method reports true if a data table exists, or false otherwise.
    public boolean haveData() {
        return (data != null);
    }

    // This method creates an empty data table with ncols columns.
    public void createDataTable(int ncols) {
        data = new Jama.Matrix(nrowsIncrement, ncols);
        nvars = ncols;
        nobs = 0;
        dataChanged = true;
    }

    // This method adds rows to the data table from the 2D array rows.
    public void addRows(double[][] newRows) {
        int numNewRows = newRows.length;
        int newRowLength = newRows[0].length;
        // check to see that a data table exists. If not, create it on the 
        // basis of the new rows.
        if (data == null) {
            nvars = newRowLength;
            nrows = Math.max(numNewRows, nrowsIncrement);
            data = new Jama.Matrix(nrows, nvars);
        }
        nobs += numNewRows;
        // if there are not enough empty rows in the current table, 
        // expand it.
        if (nobs > nrows) {
            Jama.Matrix newdata = new Jama.Matrix(nrows
                    + Math.max(numNewRows, nrowsIncrement), nvars);
            newdata.setMatrix(0, nrows - 1, 0, nvars - 1, data);
            data = newdata;
            nrows += Math.max(numNewRows, nrowsIncrement);
        }
        // finally add the new rows.
        int rowIndex = nobs - 1;
        data.setMatrix(rowIndex, rowIndex + numNewRows - 1, 0,
                newRowLength - 1, new Jama.Matrix(newRows));

        dataChanged = true;
    }

    // Method to trim the number of rows in the data matrix to the last n rows.
    public void trimRows(int n) {
        if (nobs > n) {
            data = data.getMatrix(nobs - n, nobs - 1, 0, nvars - 1);
            nrows = n;
            nobs = n;
        }
        if (useObs != 0 && nobs < useObs) {
            // Some data used in past stats has been trimmed.
            dataChanged = true;
        }
    }

    // Method to replace the data matrix from a Java array.  We do not 
    // check to see if the names array has the same colunm dimension, nor
    // do we "zero-out" the existing names.  This could cause errors if it 
    // is not caught by the calling routine.
    public void replaceData(double[][] dArray) {
        data = new Jama.Matrix(dArray);
        nvars = data.getColumnDimension();
        nobs = data.getRowDimension();
        dataChanged = true;
    }

    // This method dumps the data table into a 2D array.
    public double[][] toArray() {
        return (data.getMatrix(0, nobs - 1, 0, nvars - 1)).getArray();
    }

    // This method reports the number of variables.
    public int getNVars() {
        return nvars;
    }

    // This method reports the current number of observations in the data table.
    public int getNObs() {
        return nobs;
    }

    // This method sets the value of useObs.
    public void setNObsUsed(int n) {
        useObs = n;
        dataChanged = true;
    }

    // This method reports the current value of useObs.
    public int getNObsUsed() {
        return useObs;
    }

    // This method sets the variable names from an array of strings.
    public void setNames(String[] newNames) {
        names = newNames;
    }

    // This method returns the variable names as an array of strings.
    public String[] getNames() {
        return names;
    }

    // This method returns the index of the named variable in the data table.
    public int getNameIndex(String name) {
        // Returns the variable number of the named variable, or -1
        // if there are no names or the name is not found.
        if (names == null) {
            return -1;
        }
        int varIndex = -1;
        for (int i = 0; i < nvars && varIndex == -1; i++) {
            if (name.equals(names[i])) {
                varIndex = i;
            }
        }
        return varIndex;
    }

    // This method returns the observations (column) on variable "index".
    // The whole column is returned if all == true, only the last useObs 
    // if all == false.
    public double[] getColumn(int index, boolean all) {
        int nobsUsed = nobs;
        if (!all) {
            nobsUsed = (useObs == 0) ? nobs : useObs;
            nobsUsed = Math.min(nobsUsed, nobs);
        }
        Jama.Matrix col = data.getMatrix(nobs - nobsUsed, nobs - 1, index, index);
        return col.getRowPackedCopy();
    }

    // This method sets the boolean "useBessel" which determines if variances,
    // etc., are calculated with Bessel's correction or not.  The default
    // is true.
    public void useSample(boolean option) {
        if (useBessel != option) {
            useBessel = option;
            BesselChanged = true;
        }
    }

    // This method returns the means as a Java array.
    public double[][] getMeans() {
        if (nobs == 0) {
            return null;
        }
        if (dataChanged || BesselChanged) {
            correlation(useBessel);
            dataChanged = false;
            BesselChanged = false;
        }
        return means.getArray();
    }

    // This method returns the means as a Java array.
    public double[][] getStdDevs() {
        if (nobs == 0) {
            return null;
        }
        if (dataChanged || BesselChanged) {
            correlation(useBessel);
            dataChanged = false;
            BesselChanged = false;
        }
        return stddevs.getArray();
    }

    // This method returns the correlation matrix as a Java array.
    public double[][] getCorrelations() {
        if (nobs < 2 || useObs == 1 || nvars < 2) {
            return null;
        }
        if (dataChanged || BesselChanged) {
            correlation(useBessel);
            dataChanged = false;
            BesselChanged = false;
        }
        return correl.getArray();
    }

    // This method returns the variance-covariance matrix as a Java array.
    public double[][] getVarCovars() {
        if (nobs < 2 || useObs == 1 || nvars < 2) {
            return null;
        }
        if (dataChanged || BesselChanged) {
            correlation(useBessel);
            dataChanged = false;
            BesselChanged = false;
        }
        return covar.getArray();
    }

    // This method returns the data table in a string for printing.
    public String printData() {
        if (data == null) {
            return null;
        }
        String[] rowLabels = new String[nobs];
        for (int i = 0; i < nobs; i++) {
            rowLabels[i] = Integer.toString(i);
        }
        return convertMatrixToString(data.getMatrix(0, nobs - 1,
                0, nvars - 1), "Obsv #", rowLabels, names);
    }

    // This method returns the correlation matrix as a string for printing.
    public String printCorrelation() {
        if (correl == null) {
            return null;
        }
        return convertMatrixToString(correl, null, names, names);
    }

    // This method returns the variance-covariance matrix as a string for
    // printing.
    public String printCovariance() {
        if (covar == null) {
            return null;
        }
        return convertMatrixToString(covar, null, names, names);
    }

    // This method returns the coefficients in a regression of the variable
    // whose index is the first in the int[] var on all the other variables
    // listed in var.  It creates a matrix of observations to pass to the
    // regression routine, below.
    public double[][] regress(int[] var) throws ExtensionException {
        int v = var.length;
        int nobsUsed = (useObs == 0) ? nobs : useObs;
        nobsUsed = Math.min(nobsUsed, nobs);
        Jama.Matrix X = new Jama.Matrix(nobsUsed, v);
        for (int j = 0; j < v; j++) {
            X.setMatrix(0, nobsUsed - 1, j, j, 
                    data.getMatrix(nobs - nobsUsed, nobs - 1, var[j], var[j]));
        }
        rVars = var;
        regression(X, false);
        return rCoeffs.getArray();
    }

    // This method returns the regression coefficients.
    public double[][] getRCoeffs() {
        return rCoeffs.getArray();
    }

    // This method returns the regression statistics.
    public double[] getRStats() {
        double[] rstats = new double[11];
        rstats[0] = rR2stat;
        rstats[1] = rAdjR2stat;
        rstats[2] = rFstat;
        rstats[3] = rFstatP;
        rstats[4] = rStdErrEst;
        rstats[5] = rDFT;
        rstats[6] = rDFR;
        rstats[7] = rDFE;
        rstats[8] = rSST;
        rstats[9] = rSSR;
        rstats[10] = rSSE;
        return rstats;
    }

    public double[][] getRCStats() {
        double[][] rcstats = new double[3][rVars.length];
        rcstats[0] = (rTBetaP.getArray())[0];
        rcstats[1] = (rTBeta.getArray())[0];
        rcstats[2] = (rSEBeta.getArray())[0];
        return rcstats;
    }

    // This method returns the coefficients in a regression that regresses 
    // the variable whose index in the data table is given by var, against
    // a constructed time variable.  If type = 0, linear forecast, type = 1,
    // compound forecast, type = 2, continuous forecast.
    // If only one observation is used, set the constant to the observation
    // and the slope or growth rate to zero.
    public double[][] forecast(int var, int type) throws ExtensionException {
        int nobsUsed = (useObs == 0) ? nobs : useObs;
        nobsUsed = Math.min(nobsUsed, nobs);
        if (nobsUsed == 1) {
            if (fCoeffs == null) {
                fCoeffs = new Jama.Matrix(1, 2);
            }
            fCoeffs.set(0, 0, data.get(nobs - 1, var));
            fCoeffs.set(0, 1, 0.0);
            return fCoeffs.getArray();
        }
        Jama.Matrix X = new Jama.Matrix(nobsUsed, 2);
        // Put var or ln(var) in the first column and time in the second.
        for (int i = 0, ii = (nobs - nobsUsed); i < nobsUsed; i++, ii++) {
            if (type == 0) {
                X.set(i, 0, data.get(ii, var));
            } else {
                X.set(i, 0, Math.log(data.get(ii, var)));
            }
            X.set(i, 1, (double) i);
        }
        regression(X, true);
        // Transform the coefficients as appropriate to the type of forecast,
        // with the first being the constant and the second the slope or the 
        // growth rate, r, as appropriate to the type of forecast.
        if (type == 1) {
            fCoeffs.set(0, 0, Math.exp(fCoeffs.get(0, 0)));
            fCoeffs.set(0, 1, (Math.exp(fCoeffs.get(0, 1)) - 1.0));
        } else if (type == 2) {
            fCoeffs.set(0, 0, Math.exp(fCoeffs.get(0, 0)));
        }
        return fCoeffs.getArray();
    }

    // This method returns the forecast coefficients.
    public double[][] getFCoeffs() {
        return fCoeffs.getArray();
    }

    /* ====================================================================== */
    // These are routines that operate on the data in the LogoStatsTbl.
    /* ====================================================================== */
    private void correlation(boolean Bessel) {
        // Does the actual variance-covariance and correlation calculations.
        int nobsUsed = (useObs == 0) ? nobs : useObs;
        nobsUsed = Math.min(nobsUsed, nobs);
        Jama.Matrix X = data.getMatrix(nobs - nobsUsed, nobs - 1,
                0, nvars - 1);

        /*
         * Find the means of each variable and then form a
         * (v x v) matrix of the cross-products of the means times the
         * number of observations being used.
         * Next form the (v x v) cross-products matrix for the data,
         * subtract the means cross-product matrix to get numerator
         * of the covariance matrix, and divide by n-1 to get
         * covariance matrix. NOTE that we use Bessel's correction here 
         * so as to be consistent with the way NetLogo calculates the variance.
         * Calculate the standard deviation of each variable from
         * the diagonal of the covariance matrix.
         * Form the cross-product matrix of standard deviations
         * and use that and the covariance matrix to calculate
         * the correlations matrix.
         */

        Jama.Matrix ones = new Jama.Matrix(1, nobsUsed, 1.0);
        means = (ones.times(X)).times(1.0 / nobsUsed);
        Jama.Matrix meansMatrix =
                (means.transpose().times(means)).times(nobsUsed);

        Jama.Matrix cov = X.transpose().times(X);
        double divisor = (Bessel) ? (nobsUsed - 1) : nobsUsed;
        cov = (cov.minusEquals(meansMatrix)).timesEquals(1.0 / divisor);
        covar = cov;

        stddevs = new Jama.Matrix(1, nvars);
        for (int i = 0; i < nvars; i++) {
            stddevs.set(0, i, Math.sqrt(cov.get(i, i)));
        }
        Jama.Matrix stdMatrix = stddevs.transpose().times(stddevs);
        Jama.Matrix cor = cov.arrayRightDivide(stdMatrix);
        correl = cor;
    }

    /* ---------------------------------------------------------------------- */
    private void regression(Jama.Matrix dat, boolean forecast) 
    throws ExtensionException {
        // This does the actual work of performing the regression and 
        // calculating the various regression and coefficient statistics.


        // pull out the variables to be regressed into the Y and X
        // matrices. Note that the X matrix will have 1's in the first
        // column for the constant.
        int n = dat.getRowDimension();
        int v = dat.getColumnDimension();
        Jama.Matrix Y = new Jama.Matrix(n, 1);
        Y.setMatrix(0, n - 1, 0, 0, dat.getMatrix(0, n - 1, 0, 0));
        Jama.Matrix X = new Jama.Matrix(n, v, 1);
        X.setMatrix(0, n - 1, 1, v - 1, dat.getMatrix(0, n - 1, 1, v - 1));

        // Let Jama do the regression.
        Jama.Matrix A = X.solve(Y);

        // A is now a v x 1 matrix of coefficients
        // a(0) ... a(v).  Save it.
        if (forecast) {
            fCoeffs = A.transpose();
            return;
        } else {
            rCoeffs = A.transpose();
        }

        // Find the various statistics for the regression itself.
        // Their names are fairly self-explanitory.
        Jama.Matrix Ysum = new Jama.Matrix(1, n, 1.0).times(Y);
        double Ybar = Ysum.get(0, 0) / n;
        Jama.Matrix Ydiff = Y.minus(new Jama.Matrix(n, 1, Ybar));
        rSST = ((Ydiff.transpose()).times(Ydiff)).get(0, 0);
        Jama.Matrix Resid = (X.times(A)).minus(Y);
        rSSE = ((Resid.transpose()).times(Resid)).get(0, 0);
        rSSR = rSST - rSSE;
        rDFT = n - 1;
        rDFR = v - 1;
        rDFE = rDFT - rDFR;

        rR2stat = 1.0 - (rSSE / rSST);
        rAdjR2stat = 1.0 - ((1.0 - rR2stat)
                * ((double) rDFT / (double) rDFE));
        rFstat = (rSSR / rDFR) / (rSSE / rDFE);
        rFstatP = Distributions.PvalueForFstat(rFstat, rDFR, rDFE);
        rStdErrEst = Math.sqrt(rSSE / rDFE);

        // Now go after the standard errors, T's and P's of the coefficients.
        Jama.Matrix XX = (X.transpose()).times(X);
        Jama.Matrix XXInv = XX.inverse();
        rSEBeta = new Jama.Matrix(1, v);
        rTBeta = new Jama.Matrix(1, v);
        rTBetaP = new Jama.Matrix(1, v);
        double temp = rSSE / rDFE;
        for (int i = 0; i < v; i++) {
            double se = Math.sqrt(temp * XXInv.get(i, i));
            rSEBeta.set(0, i, se);
            rTBeta.set(0, i, (rCoeffs.get(0, i) / se));
            rTBetaP.set(0, i, Distributions.PvalueForTstat(rTBeta.get(0, i), rDFE));
        }
    }

    /* ---------------------------------------------------------------------- */
    private String convertMatrixToString(Jama.Matrix mat, String corner,
            String[] rowLabels, String[] colLabels) {

        String ncformat = "%12.8g";
        String scformat = "%12s";

        double[][] dArray = mat.getArray();
        StringBuilder buf = new StringBuilder();

        // Find the longest row label, if any.
        int maxLen = (corner == null) ? 0 : corner.length();
        if (rowLabels != null) {
            for (String label : rowLabels) {
                maxLen = Math.max(maxLen, label.length());
            }
        }
        String srformat = "%-" + maxLen + "s";

        // Begin with column labels, if any.
        if (corner != null) {
            buf.append(String.format(srformat, corner));
            buf.append(" ");
        } else if (colLabels != null) {
            buf.append(String.format(srformat, " "));
            buf.append(" ");
        }
        if (colLabels != null) {
            for (String label : colLabels) {
                buf.append(String.format(scformat, label));
                buf.append(" ");
            }
        }
        buf.append("\n");

        // Now build a row at a time.
        for (int i = 0; i < dArray.length; i++) {
            if (rowLabels != null) {
                buf.append(String.format(srformat, rowLabels[i]));
                buf.append(" ");
            }
            for (int j = 0; j < dArray[i].length; j++) {
                buf.append(String.format(ncformat, dArray[i][j]));
                buf.append(" ");
            }
            buf.append("\n");
        }
        return buf.toString();
    }
}
