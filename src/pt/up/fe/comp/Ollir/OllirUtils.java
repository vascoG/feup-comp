package pt.up.fe.comp.Ollir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;

public class OllirUtils {

    public static String getCode(Symbol symbol){

        return symbol.getName() + "." + getCode(symbol.getType());
    }

    public static String getCode(Type type){
        StringBuilder code = new StringBuilder();

        if(type.isArray()){
            code.append("array.");
        }

        code.append(getOllirType(type.getName()));
        
        return code.toString();
    }

    public static String getOllirType(String jmmType){
        switch(jmmType){
            case "void":
                return "V";
            case "int":
                return"i32";
            case "boolean":
                return"bool";
            default:
                return jmmType;
        }

    }

    public static String getOperation(JmmNode node){
        switch(node.getKind()){
            case "AndOp": return "&&.bool";
            case "Neg": return "!.bool";
            case "LessOp": return "<.i32";
            case "Add": return "+.i32";
            case "Sub": return "-.i32";
            case "Mul": return "*.i32";
            case "Div": return "/.i32";
            default: return "";
        }
    }

    public static String getOperationType(JmmNode node){
        switch(node.getKind()){
            case "AndOp": return ".bool";
            case "Neg": return ".bool";
            case "LessOp": return ".bool";
            case "Add": return ".i32";
            case "Sub": return ".i32";
            case "Mul": return ".i32";
            case "Div": return ".i32";
            default: return "";
        }
    }

    public static String getParentMethod(JmmNode node) {

        while(!node.getKind().equals("OtherMethodDeclaration") && !node.getKind().equals("MainMethodDeclaration"))
            node = node.getJmmParent();

        if(node.getKind().equals("OtherMethodDeclaration"))
            return node.get("name");
        
        return "main";
    }
}

