package pt.up.fe.comp.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;

public class SymbolTableVisitor extends PreorderJmmVisitor<Boolean, Boolean>{

    private final JmmSymbolTable symbolTable;
    private final List<Report> reports;

    public SymbolTableVisitor()
    {
        this.symbolTable = new JmmSymbolTable();
        this.reports = new ArrayList<>();
        addVisit("ClassDeclaration", this::visitClass);
        addVisit("ImportDeclaration", this::visitImport);
        addVisit("MainMethodDeclaration", this::visitMainMethod);
        addVisit("OtherMethodDeclaration", this::visitOtherMethod);

    }

    public JmmSymbolTable getSymbolTable() 
    {
        return symbolTable;
    }

    public List<Report> getReports()
    {
        return reports;
    }

    public Boolean visitClass(JmmNode node, Boolean dummy)
    {
        symbolTable.setClassName(node.get("name"));
        JmmNode extNode = node.getJmmChild(0);
        if(extNode.getKind().equals("Extend"))
            symbolTable.setSuperClassName(extNode.get("name"));
        List<JmmNode> fieldsNodes = node.getChildren().stream().filter(c->c.getKind().equals("VarDeclaration")).collect(Collectors.toList());
        for(JmmNode fieldNode : fieldsNodes)
        {
            JmmNode typeNode = fieldNode.getJmmChild(0);
            Type type = new Type(typeNode.get("name"), Boolean.parseBoolean(typeNode.get("isArray")));
            symbolTable.addField(type, fieldNode.get("name"));
        }
        
        return true;
    }

    public Boolean visitImport(JmmNode node, Boolean dummy)
    {
        StringBuilder importString = new StringBuilder(node.get("name"));
        for (JmmNode child : node.getChildren())
            importString.append(child.get("name"));
        symbolTable.addImport(importString.toString());

        return true;
    }

    public Boolean visitMainMethod(JmmNode node, Boolean dummy)
    {
        List <Symbol> param = new ArrayList<Symbol>();
        param.add(new Symbol(new Type("String", true), node.get("argument")));
        symbolTable.addMethod( new Type("void",false), "main", param);

        return true;
    }

    public Boolean visitOtherMethod(JmmNode node, Boolean dummy)
    {
        JmmNode returnNode = node.getJmmChild(0);
        Type returnType = new Type(returnNode.get("name"),Boolean.parseBoolean(returnNode.get("isArray")));
        String name = node.get("name");

        List<JmmNode> argumentsNodes = node.getChildren().stream().filter(c->c.getKind().equals("Argument")).collect(Collectors.toList());
        List<Symbol> arguments = new ArrayList<Symbol>();
        for(JmmNode argumentNode : argumentsNodes)
        {
            JmmNode typeNode = argumentNode.getJmmChild(0);
            Type type = new Type(typeNode.get("name"), Boolean.parseBoolean(typeNode.get("isArray")));
            arguments.add(new Symbol(type, argumentNode.get("name")));
        }
        symbolTable.addMethod(returnType, name, arguments);

        return true;
    }
    
}
