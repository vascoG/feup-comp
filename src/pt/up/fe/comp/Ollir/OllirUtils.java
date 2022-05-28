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
            case "Not": return "!.bool";
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
            case "Not": return ".bool";
            case "LessOp": return ".bool";
            case "Add": return ".i32";
            case "Sub": return ".i32";
            case "Mul": return ".i32";
            case "Div": return ".i32";
            default: return "";
        }
    }

    //funçao para devolver o tipo através da expressao
    public static String TypeExpress(String exprStmt){
        String aux = "";
        int index = exprStmt.lastIndexOf('.');

        if(exprStmt.equals("")) return "";

        if(exprStmt.length() > 6 && index > 5f){
            if(exprStmt.startsWith("array", index-5)) aux += ".array";
        }

        return aux + "." + exprStmt.substring(index+1).trim().replaceAll("[() ;+<*/&\\-]", "");
    }

    public static String IdentifierExpress(String exprStmt) { 
        String[] values = exprStmt.split("\\.");

        if(values.length == 2) return values[0].trim();

        if(values.length > 2){
            int index = 1;
            String removelast = values[values.length-2];

            if(removelast.equals("array")) index = 2;

            List<String> aux = new ArrayList<>(Arrays.asList(values).subList(0, values.length-index));
            return String.join(".", aux);
        }
        return exprStmt;
    }

    public static String getParentMethod(JmmNode node) {

        while(!node.getKind().equals("OtherMethodDeclaration") && !node.getKind().equals("MainMethodDeclaration"))
            node = node.getJmmParent();

        if(node.getKind().equals("OtherMethodDeclaration"))
            return node.get("name");
        
        return "main";
    }
}

