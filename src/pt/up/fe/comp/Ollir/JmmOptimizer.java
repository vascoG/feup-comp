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

        return new OllirResult(semanticsResult, ollirCode, Collections.emptyList());
    }

    
}
