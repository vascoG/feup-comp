package pt.up.fe.comp.SymbolTable; 

import java.util.Collections;
import java.util.List;

import pt.up.fe.comp.SemanticAnalysis.SemanticAnalysisVisitor;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis; 

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.parser.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report; 

public class JmmAnalyser implements JmmAnalysis { 
    
    @Override 
    public JmmSemanticsResult semanticAnalysis(JmmParserResult parserResult) { 

        JmmNode node = parserResult.getRootNode();

        SymbolTableVisitor visitor = new SymbolTableVisitor();
        visitor.visit(node);
        
        JmmSymbolTable symbolTable = visitor.getSymbolTable();

        SemanticAnalysisVisitor semanticVisitor = new SemanticAnalysisVisitor(symbolTable);
        semanticVisitor.visit(node);

        System.out.println( symbolTable.print());
       
        List<Report> reports = visitor.getReports();
        reports.addAll(semanticVisitor.getReports());
        
        System.out.println(reports);

        return new JmmSemanticsResult(parserResult, symbolTable, reports); 
    } 

}