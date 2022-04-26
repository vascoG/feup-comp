package pt.up.fe.comp.SemanticAnalysis;

import java.util.List;

import pt.up.fe.comp.SymbolTable.JmmSymbolTable;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;

public class SemanticUtils {
    public static boolean isBoolean(JmmSymbolTable symbolTable, JmmNode node, List<Report> reports) {

        switch(node.getKind())
        {   case "Call": //
            case "Neg": //
            case "FTTrue": return true;
            case "FTFalse": return true;
            case "FTIdentifier": //
            case "AndOp": if(isExpressionWithBoolean(symbolTable, node, reports)) return true;
            case "LessOp": if(isExpressionWithInteger(symbolTable, node, reports)) return true;
        }

        return false;
    }

    private static boolean isExpressionWithBoolean(JmmSymbolTable symbolTable, JmmNode node, List<Report> reports) {
        return false;
    }

    public static boolean isInteger(JmmSymbolTable symbolTable, JmmNode node, List<Report> reports) {

        switch(node.getKind())
        {   
            case "Call": //
            case "ArrayIndex": //
            case "FTIdentifier": //
            case "FTInt" : return true;
            case "Add":
            case "Mul":
            case "Div":
            case "Sub": if(isExpressionWithInteger(symbolTable, node, reports)) return true;
        }

        return false;
    }

    private static boolean isExpressionWithInteger(JmmSymbolTable symbolTable, JmmNode node, List<Report> reports) {
        List<JmmNode> children = node.getChildren();
        if(children.size()!=2) return false;

        if(children.get(0).getKind()!="FTInt")//criar função para avaliar inteiros
            return false;
        if(children.get(1).getKind()!="FTInt")
            return false;
        return true;
    }

    

    
}
