package pt.up.fe.comp.SemanticAnalysis;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.SymbolTable.JmmSymbolTable;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;

public class SemanticAnalysisVisitor extends PreorderJmmVisitor<Boolean, Boolean>  {
    private final JmmSymbolTable symbolTable;
    private final List<Report> reports;

    public SemanticAnalysisVisitor(JmmSymbolTable symbolTable)
    {
        this.symbolTable = symbolTable;
        this.reports = new ArrayList<>();

        addVisit("IFElseBlock", this::visitIfStatement);
    }

    public Boolean visitStatement(JmmNode node){
        return true;
    }

    public Boolean visitIfStatement(JmmNode node, Boolean dummy) {

        List<JmmNode> children = node.getChildren();
        for (JmmNode child : children)
        {
            if (child.getKind().equals("IfCondition")){
                List<JmmNode> conditionChildren = node.getChildren();
                for (JmmNode conditionChild : conditionChildren)
                    SemanticUtils.isBoolean(symbolTable, conditionChild, reports);
            }
            else {visitStatement(node);}
        }

        return true;
    }
}
