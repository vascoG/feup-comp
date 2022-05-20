package pt.up.fe.comp.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

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
            Type type = new Type(typeNode.get("name"), parseBoolean(typeNode.get("isArray")));
            symbolTable.addField(type, fieldNode.get("name"));
        }
        
        return true;
    }

    public Boolean visitImport(JmmNode node, Boolean dummy)
    {
        StringBuilder importString = new StringBuilder(node.get("name"));
        for (JmmNode child : node.getChildren())
        {   
            importString.append(".");
            importString.append(child.get("name"));    
        }
        symbolTable.addImport(importString.toString());

        return true;
    }

    public Boolean visitMainMethod(JmmNode node, Boolean dummy)
    {
        List <Symbol> param = new ArrayList<Symbol>();
        param.add(new Symbol(new Type("String", true), node.get("argument")));

        JmmMethod method = new JmmMethod("main", new Type("void",false), param);

        for (var child:node.getChildren())
        {
            if(child.getKind().equals("MethodBody"))
            { 
                for(var childBody:child.getChildren())
                {
                    if(childBody.getKind().equals("VarDeclaration"))
                        if(!method.addLocalVariable(new Type(childBody.getJmmChild(0).get("name"), parseBoolean(childBody.getJmmChild(0).get("isArray"))), childBody.get("name")))
                            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(childBody.get("line")),Integer.valueOf(childBody.get("col")), "Redeclaration of a variable!"));
                    }
            }
        }

        symbolTable.addMethod(method);
        return true;
    }

    public Boolean visitOtherMethod(JmmNode node, Boolean dummy)
    {
        JmmNode returnNode = node.getJmmChild(0);
        Type returnType;
        if(returnNode.getKind().equals("Type"))
            returnType = new Type(returnNode.get("name"),parseBoolean(returnNode.get("isArray")));
        else
            returnType = new Type("void", false);
        String name = node.get("name");

        List<JmmNode> argumentsNodes = node.getChildren().stream().filter(c->c.getKind().equals("Argument")).collect(Collectors.toList());
        List<Symbol> arguments = new ArrayList<Symbol>();
        for(JmmNode argumentNode : argumentsNodes)
        {
            JmmNode typeNode = argumentNode.getJmmChild(0);
            Type type = new Type(typeNode.get("name"), parseBoolean(typeNode.get("isArray")));
            arguments.add(new Symbol(type, argumentNode.get("name")));
        }

        JmmMethod method = new JmmMethod(name, returnType, arguments);
        for (var child:node.getChildren())
        {
            if(child.getKind().equals("MethodBody"))
            { 
                for(var childBody:child.getChildren())
                {
                    if(childBody.getKind().equals("VarDeclaration")){
                        if(!method.addLocalVariable(new Type(childBody.getJmmChild(0).get("name"), parseBoolean(childBody.getJmmChild(0).get("isArray"))), childBody.get("name")))
                            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(childBody.get("line")),Integer.valueOf(childBody.get("col")), "Redeclaration of a variable!"));
                    }
            
            }
        }
    }

        symbolTable.addMethod(method);
        return true;
    }

    private boolean parseBoolean(String string) {
        if(string.equals("false"))
            return false;
        else
            return true;
    }
    
}
