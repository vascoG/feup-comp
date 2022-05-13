package pt.up.fe.comp.Ollir;

import java.util.Collections;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;

public class JmmOptimizer implements JmmOptimization{

    @Override
    public OllirResult toOllir(JmmSemanticsResult semanticsResult) {
        var OllirGenerator = new OllirGenerator(semanticsResult.getSymbolTable());
        OllirGenerator.visit(semanticsResult.getRootNode());
        var ollirCode = OllirGenerator.getCode();

        System.out.println("OLLIR CODE : \n" + ollirCode);


/*        String ollirCode = "import ioPlus;\r\n"
        + "import BoardBase;\r\n"
        + "import java.io.File;\r\n"
        + "\r\n"        
        + "class HelloWorld  extends BoardBase{\r\n"
        + "\r\n"
        + "\r\n"
        +"}";
*/
        return new OllirResult(semanticsResult, ollirCode, Collections.emptyList());
    }

    
}
