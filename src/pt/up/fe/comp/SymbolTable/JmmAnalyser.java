package pt.up.fe.comp.SymbolTable; 

import java.util.Collections; 

import pt.up.fe.comp.jmm.analysis.JmmAnalysis; 

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult; 

import pt.up.fe.comp.jmm.analysis.table.SymbolTable; 

import pt.up.fe.comp.jmm.parser.JmmParserResult; 

public class JmmAnalyser implements JmmAnalysis { 
    
    @Override 
    public JmmSemanticsResult semanticAnalysis(JmmParserResult parserResult) { 
        SymbolTable symbolTable = null;
        return new JmmSemanticsResult(parserResult, symbolTable, Collections.emptyList()); 
    } 

}