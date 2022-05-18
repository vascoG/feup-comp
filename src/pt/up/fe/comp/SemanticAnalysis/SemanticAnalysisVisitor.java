package pt.up.fe.comp.SemanticAnalysis;

import java.util.ArrayList;
import java.util.List;

import freemarker.core.builtins.sourceBI;
import pt.up.fe.comp.SymbolTable.JmmSymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
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
        addVisit("WhileBlock", this::visitWhileStatement);
        addVisit("Assignment", this::visitAssignment);
        addVisit("Add", this::visitMathOperations);
        addVisit("Sub", this::visitMathOperations);
        addVisit("Mul", this::visitMathOperations);
        addVisit("Div", this::visitMathOperations);
        addVisit("LessOp", this::visitBooleanOperations);
        addVisit("AndOp", this::visitBooleanOperations);
        addVisit("Neg", this::visitNotOperations);
        addVisit("ReturnExpression",this::visitReturn);
        addVisit("Call", this::visitCall);
        addVisit("Array", this::visitNewArray);
        addVisit("ArrayIndex", this::visitArrayIndex);    
    }

    public List<Report> getReports() {
        return reports;
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

    public Boolean visitWhileStatement(JmmNode node, Boolean dummy) {

        List<JmmNode> children = node.getChildren();
        for (JmmNode child : children)
        {
            if (child.getKind().equals("WhileCondition")){
                    if(!SemanticUtils.isBoolean(symbolTable, child.getJmmChild(0), reports))
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on While Condition: Must be a boolean!"));
                    
            }
        }

        return true;
    }

    public Boolean visitMathOperations(JmmNode node, Boolean dummy) {
        if(node.getNumChildren()!=2)
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Math Operation: Wrong number of operands!"));
    
        if(!SemanticUtils.isInteger(symbolTable, node.getJmmChild(0), reports))
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Math Operation: Left Operand is not a Integer!"));

        if(!SemanticUtils.isInteger(symbolTable, node.getJmmChild(1), reports))
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Math Operation: Right Operand is not a Integer!"));

        return true;
    }

    public Boolean visitBooleanOperations(JmmNode node, Boolean dummy) {
        if(node.getNumChildren()!=2)
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Boolean Binary Operation: Wrong number of operands!"));
    
        if(!SemanticUtils.isBoolean(symbolTable, node.getJmmChild(0), reports))
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Boolean Operation: Left Operand is not a Boolean!"));

        if(!SemanticUtils.isBoolean(symbolTable, node.getJmmChild(1), reports))
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Boolean Operation: Right Operand is not a Boolean!"));

        return true;
    }

    public Boolean visitNotOperations(JmmNode node, Boolean dummy) {
        if(node.getNumChildren()!=1)
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Boolean Unary Operation: Wrong number of operands!"));
    
        if(!SemanticUtils.isBoolean(symbolTable, node.getJmmChild(0), reports))
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Boolean Operation: Unary Operand is not a Boolean!"));

        

        return true;
    }
    public Boolean visitAssignment(JmmNode node, Boolean dummy) {
        if(node.getNumChildren()!=2)
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Assignment: Wrong number of operands!"));
    
        if(!node.getJmmChild(0).getKind().equals("FTIdentifier") && !node.getJmmChild(0).getKind().equals("ArrayIndex"))
        { 
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Assignment: Wrong left type"));
            return true;
        }

        Type left = SemanticUtils.getNodeType(symbolTable, node.getJmmChild(0), reports);
        Type right = SemanticUtils.getNodeType(symbolTable, node.getJmmChild(1), reports);

        if(left==null || right == null)
            return true;

        if(right.getName().equals(symbolTable.getClassName()) && left.getName().equals(symbolTable.getSuper()))
            return true;

        if(symbolTable.getImports().contains(left.getName()) && symbolTable.getImports().contains(right.getName()))
            return true;

        if(!SemanticUtils.sameType(left, right))
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Assignment: Different types: "+left + " and "+ right));


        return true;
    }
    
    public Boolean visitReturn(JmmNode node, Boolean dummy){

       Type type = SemanticUtils.getNodeType(symbolTable, node.getJmmChild(0), reports);

        if(type == null)
            return true;
        String method = SemanticUtils.getParentMethod(node);
        Type realType = symbolTable.getReturnType(method);

        if(type.getName().equals(symbolTable.getClassName()) && realType.getName().equals(symbolTable.getSuper()))
            return true;

        if(symbolTable.getImports().contains(type.getName()) && symbolTable.getImports().contains(realType.getName()))
            return true;

        if(!SemanticUtils.sameType(type, realType))
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Return: Different types: "+type + " and "+ realType));


        return true;
        }

        public Boolean visitCall(JmmNode node, Boolean dummy) {
            if(node.getNumChildren()!=2)
                    reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on MethodCall: Wrong number of operands!"));
        
            var methodCall = node.getJmmChild(1);
            if(methodCall.getKind().equals("MethodCall"))
            { 
                //caso exista na tabela, confirmar argumentos, senao assumir que argumentos estao bem
                if(symbolTable.getMethods().contains(methodCall.get("name"))){
                    List<Symbol> params = symbolTable.getParameters(methodCall.get("name"));
                    if(params.size()!=methodCall.getNumChildren())
                    {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1, -1, "Error on Method: wrong number of parameters!"));
                        return true;
                    }
                    int index = 0;
                    for(Symbol param : params)
                    {  
                        Type typeParam = SemanticUtils.getNodeType(symbolTable, methodCall.getJmmChild(index), reports);
                        if(!SemanticUtils.sameType(param.getType(), typeParam) )
                            {
                                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1, -1, "Error on Method: wrong type of parameter!"));
                                return true;
                            }
                    }
                    return true;
                }

                Type type = SemanticUtils.getNodeType(symbolTable, node.getJmmChild(0), reports);
                if(type!=null){

                    //verificar se a classe extende alguma coisa e se foi chamado um método num objeto da classe
                    if(type.getName().equals(symbolTable.getClassName()) && symbolTable.getSuper()!=null)
                        return true;
                        
                    //verificar se há imports que sejam igual ao objeto onde está a ser chamado o método
                    else if(symbolTable.getImports().contains(type.getName()))
                        return true;
                }
                
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1, -1, "Error on Method: unexisting method"));
    
            }
    
            //length only on array
    
            return true;
        }
        public Boolean visitArrayIndex(JmmNode node, Boolean dummy) {
            if(node.getNumChildren()!=2)
                    reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on ArrayIndex: Wrong number of operands!"));
        
            
            Type left = SemanticUtils.getNodeType(symbolTable, node.getJmmChild(0), reports);
            if(left == null)
                 return true;
            if(!SemanticUtils.sameType(left, new Type("int", true)))
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on ArrayIndex: Not accessing an array!"));
            if(!SemanticUtils.isInteger(symbolTable, node.getJmmChild(1), reports))
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on ArrayIndex: Index must be a Integer!"));
    
            return true;
        }
    
        public Boolean visitNewArray(JmmNode node, Boolean dummy) {
            if(node.getNumChildren()!=1)
                    reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Array: Wrong number of operands!"));
        
            if(!SemanticUtils.isInteger(symbolTable, node.getJmmChild(0), reports))
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, -1,-1, "Error on Array: Index must be a Integer!"));
    
            return true;
        }
}
