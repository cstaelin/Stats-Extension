/*
 * Implements the commands and reporters that create and manipulate the 
 * data table.
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

public class TblPrims {

        public static class NewTable extends DefaultReporter {
        // Constructs and returns an empty LogoStatsTbl
        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{}, Syntax.WildcardType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {
            return new LogoStatsTbl();
        }
    }

    /* ---------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------- */
    public static class NewTableFromRowList extends DefaultReporter {
        // Constructs and returns a new LogoStatsTbl with data loaded row by 
        // row from a nested LogoList
        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.ListType()},
                    Syntax.WildcardType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {
            return new LogoStatsTbl(
                    new Jama.Matrix(ExtnUtils.convertNestedLogoListToArray(args[0].getList())));
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class AddNewRow extends DefaultCommand {
        // Adds a new row of data to the data table from a simple LogoList.
        @Override
        public Syntax getSyntax() {
            return Syntax.commandSyntax(new int[]{Syntax.WildcardType(),
                        Syntax.ListType()});
        }

        public void perform(Argument args[], Context context)
                throws ExtensionException, LogoException {
            // get the StatsTbl and the rows to be added.
            // Note that although this is a single row, the row is put in
            // in a 2D array by convertSimpleLogoListToArray.
            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            double [][] newRows = 
                    ExtnUtils.convertSimpleLogoListToArray(args[1].getList());
            // check to see that new rows match the existing row length.
            if (tbl.haveData() && tbl.getNVars() != newRows[0].length) {
                throw new org.nlogo.api.ExtensionException(
                        "Number of variables in observation to be added "
                        + "does not match the dimension of the StatsTbl.");
            }
            tbl.addRows(newRows);
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class TrimDataTable extends DefaultCommand {
        // Trims the data table to the specified number of observations only
        // if the number specified is less than the number of observations.

        @Override
        public Syntax getSyntax() {
            return Syntax.commandSyntax(new int[]{Syntax.WildcardType(),
                        Syntax.NumberType()});
        }

        public void perform(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            tbl.trimRows(args[1].getIntValue());
        }
    }
    
    /* ---------------------------------------------------------------------- */
    public static class GetDataAsNestedList extends DefaultReporter {
        // Get the data matrix and convert it by row to a nested LogoList.

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.ListType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            return ExtnUtils.convertArrayToNestedLogoList(tbl.toArray());
        }
    }

    /* ---------------------------------------------------------------------- */
//
//    public static class GetDataAsMatrix extends DefaultReporter {
//        // Take the data matrix, truncate it to the number of
//        // observations, then return it as a LogoMatrix.
//        @Override
//        public Syntax getSyntax() {
//            return Syntax.reporterSyntax(new int[] {Syntax.WildcardType()},
//                    Syntax.WildcardType());
//        }
//
//        public Object report(Argument args[], Context context)
//                throws ExtensionException, LogoException {
//
//            LogoStatsTbl tbl = getTblFromArgument(args[0]);
//
//            return new org.nlogo.extensions.matrix.LogoMatrix(
//                    tbl.data.getMatrix(0, tbl.nobs - 1, 0, tbl.nvars - 1));
//        }
//    }

    public static class GetColumnAsSimpleList extends DefaultReporter {
        // Get a specified column of the data matrix and convert it 
        // to a simple LogoList.

        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType(),
                        Syntax.WildcardType()}, Syntax.ListType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            int index = ExtnUtils.getVarNumberFromArg(tbl, args[1]);
            return ExtnUtils.convertVectorToSimpleLogoList(tbl.getColumn(index, false));
        }
    }
    
    /* ---------------------------------------------------------------------- */
    public static class SetNames extends DefaultCommand {
        // Set the variable names. Note that this may be called before any
        // data has been gathered.  In that case, tbl.haveData() will be false
        // and the number of names supplied will set the number of variables
        // to be expected. On the other hand, if we already have data,
        // then the number of names must match the number of variables.
        // In either case, the LogoStatsTable must have already have been
        // created.

        @Override
        public Syntax getSyntax() {
            return Syntax.commandSyntax(new int[]{Syntax.WildcardType(),
                        Syntax.ListType()});
        }

        public void perform(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            LogoList nameList = args[1].getList();
            int nvar = nameList.size();
            
            if (!tbl.haveData()) {
                tbl.createDataTable(nvar);
            }
            if (tbl.getNVars() != nvar) {
                throw new org.nlogo.api.ExtensionException(
                        "Number of variables in set-names "
                        + "does not match the dimension of the StatsTbl.");
            }

            // Convert the LogoList of names to a string array and "set" them.
            tbl.setNames(ExtnUtils.convertLogoListOfStringsToStringArray(nameList));
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class GetNames extends DefaultReporter {
        // Returns the list of variable names saved in the LogoStatsTbl.
        
        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.ListType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            return ExtnUtils.convertStringArrayToLogoListOfStrings(tbl.getNames());
        }
    }

    /* ---------------------------------------------------------------------- */
    public static class SetObsUsed extends DefaultCommand {
        // Allows the user to specify that only the last n observations in the
        // data set should be used in calculating regressions, forecasts, 
        // correlations, etc.  n is passed as an argument.
        
        @Override
        public Syntax getSyntax() {
            return Syntax.commandSyntax(new int[]{Syntax.WildcardType(),
                        Syntax.NumberType()});
        }

        public void perform(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            tbl.setNObsUsed(args[1].getIntValue());
        }
    }
    
    /* ---------------------------------------------------------------------- */
    public static class GetNObs extends DefaultReporter {
        // Returns the number of observations in the stats table.
        
        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.NumberType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            return (double)tbl.getNObs();
        }
    }

    /* ---------------------------------------------------------------------- */

    public static class GetNObsUsed extends DefaultReporter {
        // Returns the value of NObsUsed.
        
        @Override
        public Syntax getSyntax() {
            return Syntax.reporterSyntax(new int[]{Syntax.WildcardType()},
                    Syntax.NumberType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, LogoException {

            LogoStatsTbl tbl = StatsExtension.getTblFromArgument(args[0]);
            return (double)tbl.getNObsUsed();
        }
    }

    /* ---------------------------------------------------------------------- */

}