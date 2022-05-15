package pt.up.fe.comp.Ollir;

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
            case "void" : 
                return "V";
            default: 
                return jmmType;
        }
    }

    //funçao para devolver o tipo através da expressao
    public static String TypeExpress(JmmNode exprStmt){
        return "0";
    }
}

