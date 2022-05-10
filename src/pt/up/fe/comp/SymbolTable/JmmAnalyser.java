package pt.up.fe.comp.SymbolTable; 

import java.util.Collections;

import pt.up.fe.comp.SemanticAnalysis.SemanticAnalysisVisitor;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis; 

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.parser.JmmParserResult; 

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
       

        return new JmmSemanticsResult(parserResult, symbolTable, Collections.emptyList()); 
    } 

}