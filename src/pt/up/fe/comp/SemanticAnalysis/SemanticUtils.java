package pt.up.fe.comp.SemanticAnalysis;

import static org.junit.Assert.fail;

import java.util.List;

import pt.up.fe.comp.SymbolTable.JmmSymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

public class SemanticUtils {
    public static boolean isBoolean(JmmSymbolTable symbolTable, JmmNode node, List<Report> reports) {

        switch(node.getKind())
        {   case "Call": if(sameType(getReturnCallType(symbolTable, node, reports), new Type("boolean", false))) return true; break;
            case "Neg": if(isBoolean(symbolTable, node.getJmmChild(0), reports)) return true; break;
            case "FTTrue": return true;
            case "FTFalse": return true;
            case "FTIdentifier": if (sameType(getVariableType(symbolTable, node, reports), new Type("boolean", false))) return true; break;
            case "AndOp": if(isExpressionWithBoolean(symbolTable, node, reports)) return true; break;
            case "LessOp": if(isExpressionWithInteger(symbolTable, node, reports)) return true; break;
        }

        return false;
    }

    private static boolean isExpressionWithBoolean(JmmSymbolTable symbolTable, JmmNode node, List<Report> reports) {
        List<JmmNode> children = node.getChildren();
        if(children.size()!=2) return false;

        if(!isBoolean(symbolTable, node.getJmmChild(0), reports))
            return false;
        if(!isBoolean(symbolTable, node.getJmmChild(1), reports))
            return false;
        return true;
    }

    public static boolean isInteger(JmmSymbolTable symbolTable, JmmNode node, List<Report> reports) {

        switch(node.getKind())
        {   
            case "Call": if(sameType(getReturnCallType(symbolTable, node, reports), new Type("int", false))) return true; break;
            case "FTIdentifier": if (sameType(getVariableType(symbolTable, node, reports), new Type("int", false))) return true; break;
            case "FTInt" : return true;
            case "Add":
            case "Mul":
            case "Div":
            case "Sub": if(isExpressionWithInteger(symbolTable, node, reports)) return true; break;
        }
        return false;
    }



    public static boolean sameType(Type type, Type type2) {

        if(type==null || type2==null)
            return false;
        if (type.getName().equals(type2.getName()) && type.isArray()==type2.isArray())
            return true;
            
        return false;
    }

    private static boolean isExpressionWithInteger(JmmSymbolTable symbolTable, JmmNode node, List<Report> reports) {
        List<JmmNode> children = node.getChildren();
        if(children.size()!=2) return false;

        if(!isInteger(symbolTable, node.getJmmChild(0), reports))
            return false;
        if(!isInteger(symbolTable, node.getJmmChild(1), reports))
            return false;
        return true;
    }

    public static Type getVariableType(JmmSymbolTable symbolTable, JmmNode node, List<Report> reports) {

        if(node.getKind().equals("FTInt")|| node.getKind().equals("Mul")|| node.getKind().equals("Add")|| node.getKind().equals("Sub")|| node.getKind().equals("Div")) return new Type("int", false);
        else if (node.getKind().equals("FTTrue") || node.getKind().equals("FTFalse")|| node.getKind().equals("AndOp")|| node.getKind().equals("LessOp")|| node.getKind().equals("Neg")) return new Type("boolean", false);
        else if (node.getKind().equals("FTThis")) return new Type(symbolTable.getClassName(), false);
        else if (node.getKind().equals("FTNew"))
        { 
            if(node.getJmmChild(0).getKind().equals("Object")) return new Type(node.getJmmChild(0).get("name"), false);
            else return new Type("int", true);
        }
        else if (node.getKind().equals("Call"))
        { 
            return getReturnCallType(symbolTable, node, reports);
        }

        String parentMethod = getParentMethod(node);


        List<Symbol> localVariables = symbolTable.getLocalVariables(parentMethod);
        List<Symbol> fields = symbolTable.getFields();


        for(Symbol symbol : localVariables)
        { 
            if(symbol.getName().equals(node.get("name")))
                    return symbol.getType();
        }

        for(Symbol symbol : fields)
        { 
            if(symbol.getName().equals(node.get("name")))
                    return symbol.getType();
        }
        if(!symbolTable.getImports().contains(node.get("name")))
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC,Integer.valueOf(node.get("line")),Integer.valueOf(node.get("col")), "Error: Variable not declared!"));
        return null;
}

    public static String getParentMethod(JmmNode node) {

        while(!node.getKind().equals("OtherMethodDeclaration") && !node.getKind().equals("MainMethodDeclaration"))
            node = node.getJmmParent();

        if(node.getKind().equals("OtherMethodDeclaration"))
            return node.get("name");
        
        return "main";
    }

    private static Type getReturnCallType(JmmSymbolTable symbolTable, JmmNode node, List<Report> reports) {
        JmmNode beforeDotNode = node.getJmmChild(0);
        JmmNode afterDotNode = node.getJmmChild(1);

        if(afterDotNode.getKind().equals("ArrayLength")) return new Type("int", false);

        Boolean methodOnSymbolTable = symbolTable.getMethods().contains(afterDotNode.get("name"));
        Type typeBefore = getNodeType(symbolTable, beforeDotNode, reports);

        if(typeBefore==null)
            return null;

        if((beforeDotNode.getKind().equals("FTThis")||typeBefore.getName().equals(symbolTable.getClassName()))&& methodOnSymbolTable)
            return symbolTable.getReturnType(afterDotNode.get("name"));

        return null;
    }

    public static Type getNodeType(JmmSymbolTable symbolTable, JmmNode node, List<Report> reports) {
        String kind = node.getKind();
        switch (kind)
        {
            case "Add":
            case "Mul":
            case "Div":
            case "ArrayIndex":
            case "Sub": return new Type("int", false);
            case "Neg":
            case "AndOp": 
            case "LessOp": return new Type("boolean", false);
            case "Call": return getReturnCallType(symbolTable, node, reports);
            default: return getVariableType(symbolTable, node, reports);
        }
    }

}