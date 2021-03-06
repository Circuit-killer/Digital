package de.neemann.digital.analyse.format;

import de.neemann.digital.analyse.TruthTable;
import junit.framework.TestCase;

/**
 * @author hneemann
 */
public class TruthTableFormatterLaTeXTest extends TestCase {

    public void testFormat() throws Exception {
        TruthTable tt = new TruthTable(3);
        tt.addResult("Y_0");
        tt.addResult("Y_1");

        assertEquals("\\begin{center}\n" +
                "\\begin{tabular}{ccc|cc}\n" +
                "$A$&$B$&$C$&$Y_{0}$&$Y_{1}$\\\\\n" +
                "\\hline\n" +
                "$0$&$0$&$0$&$0$&$0$\\\\\n" +
                "$0$&$0$&$1$&$0$&$0$\\\\\n" +
                "$0$&$1$&$0$&$0$&$0$\\\\\n" +
                "$0$&$1$&$1$&$0$&$0$\\\\\n" +
                "$1$&$0$&$0$&$0$&$0$\\\\\n" +
                "$1$&$0$&$1$&$0$&$0$\\\\\n" +
                "$1$&$1$&$0$&$0$&$0$\\\\\n" +
                "$1$&$1$&$1$&$0$&$0$\\\\\n" +
                "\\end{tabular}\n" +
                "\\end{center}\n", new TruthTableFormatterLaTeX().format(tt));
    }

}