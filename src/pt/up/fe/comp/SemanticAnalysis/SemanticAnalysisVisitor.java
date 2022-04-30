package pt.up.fe.comp.SemanticAnalysis;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.SymbolTable.JmmSymbolTable;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

public class SemanticAnalysisVisitor extends PreorderJmmVisitor<Boolean, Boolean>  {
    private final JmmSymbolTable symbolTable;
    private final List<Report> reports;

    public SemanticAnalysisVisitor(JmmSymbolTable symbolTable)
    {
        this.symbolTable = symbolTable;
        this.reports = new ArrayList<>();

        addVisit("IFElseBlock", this::visitIfStatement);
    }

    public Boolean visitIfStatement(JmmNode node, Boolean dummy) {

        List<JmmNode> children = node.getChildren();
        for (JmmNode child : children)
        {
            if (child.getKind().equals("IfCondition")){
                    if(!SemanticUtils.isBoolean(symbolTable, child.getJmmChild(0), reports))
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on If Condition: Must be a boolean!"));
            }
        }

        return true;
    }
}
