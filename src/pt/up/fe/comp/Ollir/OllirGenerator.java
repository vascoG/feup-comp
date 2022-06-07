package pt.up.fe.comp.Ollir;

import java.util.stream.Collectors;

import jasmin.sym;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;

public class OllirGenerator extends AJmmVisitor<Integer, Code> {
    
    private final StringBuilder code;
    private final SymbolTable symbolTable;
    private int cont;
    private int whileCount;
    private int ifCount;




    public OllirGenerator(SymbolTable symbolTable){
        
        cont=0;
        whileCount=0;
        ifCount=0;
        
        this.code = new StringBuilder();
        this.symbolTable = symbolTable;

        addVisit("Program", this::programVisit);
        addVisit("ClassDeclaration", this::classDeclVisit);
        addVisit("VarDeclaration",this::varDeclarationVisit);
        addVisit("MainMethodDeclaration", this::mainDeclVisit);
        addVisit("OtherMethodDeclaration", this::methodDeclVisit);
        addVisit("Add", this::binOpVisit);
        addVisit("Mul", this::binOpVisit);
        addVisit("Sub", this::binOpVisit);
        addVisit("Div", this::binOpVisit);
        addVisit("AndOp", this::binOpVisit);
        addVisit("LessOp", this::binOpVisit);
        addVisit("Neg", this::uniOpVisit);
        addVisit("FTInt",this::intVisit);
        addVisit("FTFalse",this::falseVisit);
        addVisit("FTIdentifier",this::identifierVisit);
        addVisit("FTTrue",this::trueVisit);
        addVisit("FTThis",this::thisVisit);
        addVisit("FTNew", this::newVisit);
        addVisit("Assignment",this::assignmentVisit);
        addVisit("ReturnExpression",this::returnVisit);
        addVisit("Call", this::callVisit);
        addVisit("CompoundStatement", this::compoundStatementVisit);
        addVisit("WhileBlock", this::whileBlockVisit);
        addVisit("WhileCondition", this::whileConditionVisit);
        addVisit("WhileStatement", this::whileStatementVisit);
        addVisit("ArrayIndex", this::arrayIndexVisit);
        addVisit("IFElseBlock", this::IfElseBlockVisit);
        addVisit("IfCondition", this::IfConditionVisit);
        addVisit("IfStatement", this::IfStatementVisit);
        addVisit("ElseStatement", this::ElseStatementVisit);
    }

    public String getCode(){
        return code.toString();
    }

    private Code programVisit(JmmNode program, Integer dummy){
        for(var importString : symbolTable.getImports()){
            code.append("import ").append(importString).append(";\n");
        }

        for(var child : program.getChildren()){
            visit(child);
        }

        return null;
    }

    private Code classDeclVisit(JmmNode classDecl, Integer dummy){

        code.append("public ").append(symbolTable.getClassName());
        var superClass = symbolTable.getSuper();
        if(superClass != null){
            code.append(" extends ").append(superClass);
        }

        code.append(" {\n");

        for(var child : classDecl.getChildren()){
            if(child.getKind().equals("VarDeclaration")){
                visit(child);
            }
        }
        code.append(".construct ").append(symbolTable.getClassName());
        code.append("().V{\n");
        code.append("invokespecial(this, \"<init>\").V;\n");
        code.append("}\n");

        for(var child : classDecl.getChildren()){
            if(!child.getKind().equals("VarDeclaration")){
                visit(child);
            }
        }
        code.append("\n}");



        return null;
    }
    private Code mainDeclVisit(JmmNode methodDecl, Integer dummy){

        var methodSignature = "main";
        code.append(".method public ");
        code.append("static ");
        code.append("main(");

        var params = symbolTable.getParameters(methodSignature);

        var paramCode = params.stream()
                        .map(symbol -> OllirUtils.getCode(symbol))
                        .collect(Collectors.joining(", "));

        code.append(paramCode);

        code.append(").V");

        code.append(" {\n");

        for(var child: methodDecl.getChildren()){
            if(child.getKind().equals("MethodBody")){
                for(var bodyChild : child.getChildren())
                {
                    Code vis = visit(bodyChild);
                    if(vis!=null)
                        code.append(vis.prefix).append(vis.code).append(";\n");
                }
            }
            else if(child.getKind().equals("ReturnExpression"))
                visit(child);
        }

        code.append("\nret.V;\n}\n");
        return null;
    }

    private Code  varDeclarationVisit(JmmNode varDecl, Integer dummy){
        String name=varDecl.get("name");
        String type= varDecl.getJmmChild(0).get("name");
        if(varDecl.getJmmParent().getKind().equals("ClassDeclaration")){
            code.append(".field ");
            code.append(name).append(".");

            if(varDecl.getJmmChild(0).get("isArray").equals("true")){
                code.append("array.");
                code.append(OllirUtils.getOllirType(type));
            }else{
                code.append(OllirUtils.getOllirType(type));
            }
            code.append(";\n");
        }

        return null;
    }


    private Code methodDeclVisit(JmmNode methodDecl, Integer dummy){

        var methodSignature = methodDecl.get("name");

        code.append(".method public ");


        code.append(methodSignature).append("(");

        var params = symbolTable.getParameters(methodSignature);

        var paramCode = params.stream()
                        .map(symbol -> OllirUtils.getCode(symbol))
                        .collect(Collectors.joining(", "));

        code.append(paramCode);

        code.append(").");

        code.append(OllirUtils.getCode(symbolTable.getReturnType(methodSignature)));

        code.append(" {\n");

        for(var child: methodDecl.getChildren()){
            if(child.getKind().equals("MethodBody")){
                for(var bodyChild : child.getChildren())
                {
                    Code vis = visit(bodyChild);
                    if(vis!=null)
                        code.append(vis.prefix).append(vis.code).append(";\n");
                }
            }
            else if(child.getKind().equals("ReturnExpression"))
                visit(child);
        }


        code.append("\n}\n");

        return null;
    }

    private Code assignmentVisit(JmmNode node, Integer dummy){

        Code thisCode = new Code();
        Code lhs= visit(node.getJmmChild(0));
        Code rhs= visit(node.getJmmChild(1));

        thisCode.prefix = lhs.prefix;
        thisCode.prefix += rhs.prefix;


        //assume its always a variable
        String methodSignature = OllirUtils.getParentMethod(node);
        String type="";

        if(node.getJmmChild(0).getKind().equals("ArrayIndex"))
            type="i32";
        else
        {
        for(Symbol symbol : symbolTable.getFields())
        {
            if(symbol.getName().equals(node.getJmmChild(0).get("name")))
                 type = OllirUtils.getCode(symbol.getType());
        }
 
        for(Symbol symbol : symbolTable.getLocalVariables(methodSignature))
        {
            if(symbol.getName().equals(node.getJmmChild(0).get("name")))
                 type = OllirUtils.getCode(symbol.getType());
        }
        }
        if(!node.getJmmChild(1).getKind().equals("FTNew"))
            thisCode.code = lhs.code +" :=."+ type + " " + rhs.code;
        else if (node.getJmmChild(1).getJmmChild(0).getKind().equals("Object"))
            thisCode.code = lhs.code +" :=."+ type + " " + rhs.code + ";\ninvokespecial("+node.getJmmChild(0).get("name")+"."+type+",\"<init>\").V";
        else
        thisCode.code = lhs.code +" :=.array.i32 " + rhs.code;

        return thisCode;
    }





    private Code binOpVisit(JmmNode node, Integer dummy){
        Code lhs = visit(node.getJmmChild(0));
        Code rhs = visit(node.getJmmChild(1));

        String op = OllirUtils.getOperation(node);
        String typeOp= OllirUtils.getOperationType(node);

        Code thisCode = new Code();
        thisCode.prefix = lhs.prefix;
        thisCode.prefix += rhs.prefix;

        if(!node.getJmmParent().getKind().equals("Assignment")){
            String temp = createTemp(typeOp);
            thisCode.prefix += temp + " :="+typeOp + " " + lhs.code + " " + op + " " + rhs.code+";\n";
            thisCode.code = temp;
        }else{

            thisCode.code = lhs.code + " " + op +  " " + rhs.code;
        }

        return thisCode;

    }

    private Code uniOpVisit(JmmNode node, Integer dummy){
        Code lhs = visit(node.getJmmChild(0));

        String op = OllirUtils.getOperation(node);
        String typeOp= OllirUtils.getOperationType(node);

        Code thisCode = new Code();
        thisCode.prefix = lhs.prefix;

        if(!node.getJmmParent().getKind().equals("Assignment")){
            String temp = createTemp(typeOp);
            thisCode.prefix += temp + " :="+typeOp + op + " " + lhs.code + ";\n";
            thisCode.code = temp;
        }else{

            thisCode.code = op + " " + lhs.code;
        }

        return thisCode;

    }

    private String createTemp(String typeOp) {
        cont++;
        return "temp"+cont+typeOp;

    } 

    private Code identifierVisit(JmmNode node, Integer integer) {
        String identifierName=node.get("name");
        String methodSignature = OllirUtils.getParentMethod(node);
        
       Code code = new Code();
       code.prefix="";
       

       for(Symbol symbol : symbolTable.getFields())
       {
           if(symbol.getName().equals(identifierName))
                code.code = OllirUtils.getCode(symbol);
       }

       for(Symbol symbol : symbolTable.getLocalVariables(methodSignature))
       {
           if(symbol.getName().equals(identifierName))
                code.code = OllirUtils.getCode(symbol);
       }

        return code;
    }

    private Code thisVisit(JmmNode node, Integer integer) {
        Code code = new Code();
        code.prefix="";
        code.code="this";
        return code;
    }

    private Code trueVisit(JmmNode node, Integer integer) {
        Code code = new Code();
        code.prefix="";
        code.code="1.bool";
        return code;
    }

    private Code falseVisit(JmmNode node, Integer integer) {
        Code code = new Code();
        code.prefix="";
        code.code="0.bool";
        return code;
    }

    private Code intVisit(JmmNode node, Integer integer) {
        Code code = new Code();
        if(node.getJmmParent().getKind().equals("ArrayIndex")){
            String temp = createTemp(".i32");
            code.prefix = temp + " :=.i32 " + node.get("value")+".i32;\n";
            code.code = temp;
        }
        else{
        code.prefix="";
        code.code=node.get("value")+".i32";
        }
        return code;
    }

    private Code newVisit(JmmNode node, Integer integer) {
        Code code = new Code();

        if(node.getJmmChild(0).getKind().equals("Object")){ 


        String name = node.getJmmChild(0).get("name");
        if(!node.getJmmParent().getKind().equals("Assignment")){
            String temp = createTemp("."+name);
            code.code=temp;
            code.prefix=temp +" :=." + name+ " new("+name+")."+name+";\n";
            code.prefix+="invokespecial("+temp+",\"<init>\").V;\n";
        }
        else
        {
            code.prefix="";
            code.code=" new("+name+")."+name;
        }
    }
    else
    {
        Code arrayLengthCode = visit(node.getJmmChild(0).getJmmChild(0));
        if(!node.getJmmParent().getKind().equals("Assignment"))
        {
            String temp = createTemp(".array.i32");
            code.prefix = arrayLengthCode.prefix;
            code.code=temp;
            code.prefix+=temp +" :=.array.i32 new(array, "+arrayLengthCode.code+").arrayi32;\n";
        }
        else
        {
            code.prefix=arrayLengthCode.prefix;
            code.code = "new(array, "+arrayLengthCode.code+").arrayi32";
        }
    }
       
        return code;
    }

    private Code returnVisit(JmmNode node, Integer integer) {
        JmmNode child=node.getJmmChild(0);
        Type returnType=symbolTable.getReturnType(node.getJmmParent().get("name"));

      

        Code vis=visit(child);
            if(vis!=null) {
          
                code.append(vis.prefix);
                code.append("ret.");
                code.append(OllirUtils.getCode(returnType));
                code.append(" ");
                code.append(vis.code);
                code.append(";\n");
            }
        

        return null;
    }
    private Code callVisit(JmmNode node, Integer integer) {

        String prefixCode = "";
        Code target = visit(node.getJmmChild(0));
        prefixCode += target.prefix;
        String finalCode;
        Type returnType;


        if(node.getJmmChild(1).getKind().equals("ArrayLength"))
        {
            finalCode = "arraylength(" + target.code ;
            returnType = new Type("int", false);
        }
        else{

        String methodName = node.getJmmChild(1).get("name");
        if(target.code!=null)
            finalCode = "invokevirtual(" + target.code + "," + '"' + methodName + '"'  ;
        else
            finalCode = "invokestatic(" + node.getJmmChild(0).get("name") + "," + '"' + methodName + '"'  ;

        

        for(var child : node.getJmmChild(1).getChildren())
        {
            Code argCode = visit(child);
            prefixCode += argCode.prefix;
            finalCode += "," + argCode.code;
        }

        try {
        returnType = new Type(node.get("typeName"), Boolean.parseBoolean(node.get("isArray")));
        } catch (Exception e) {
            if(symbolTable.getMethods().contains(methodName))
                returnType = symbolTable.getReturnType(methodName);
            else
                returnType = new Type("void", false);
        }
    
    }
        finalCode+= ")." + OllirUtils.getCode(returnType);



        Code thisCode = new Code();

        String parentNodeKind = node.getJmmParent().getKind();

        if(parentNodeKind.equals("MethodBody") || parentNodeKind.equals("CompoundStatement") || (parentNodeKind.equals("Assignment") && node.getJmmChild(1).getKind().equals("ArrayLength"))){
            
            thisCode.code = finalCode;
            thisCode.prefix = prefixCode;
        }else{
            String temp = createTemp("."+OllirUtils.getCode(returnType));
            finalCode = temp + " :=." + OllirUtils.getCode(returnType) + " " + finalCode;
            prefixCode+= finalCode + ";\n";
            thisCode.code = temp;
            thisCode.prefix = prefixCode;
        }


        return thisCode;
    }

    private Code whileBlockVisit(JmmNode node, Integer integer){
        whileCount++;

        code.append("Loop"+whileCount+":\n");

        for(JmmNode child : node.getChildren()){
            Code vis = visit(child);
            if (vis != null)
                code.append(vis.prefix).append(vis.code).append(";\n");
        }

        code.append("EndLoop"+whileCount+":\n");

        return null;
    }


    private Code whileConditionVisit(JmmNode node, Integer integer) {
        Code condition=visit(node.getJmmChild(0));

        code.append(condition.prefix);
        code.append("if (" + condition.code+ ") goto WhileBody"+whileCount+";\n");
        code.append("goto EndLoop"+whileCount+";\n");

       return null;
    }

    private Code whileStatementVisit(JmmNode node, Integer integer) {
        code.append("WhileBody"+whileCount+":\n");

        for (JmmNode child : node.getChildren()){
            Code vis = visit(child);
            if (vis != null)
                code.append(vis.prefix).append(vis.code).append(";\n");
        }

        code.append("goto Loop"+whileCount+";\n");
       return null;
    }
    private Code compoundStatementVisit(JmmNode node, Integer integer) {
        for(JmmNode child:node.getChildren()){
            Code vis = visit(child);
            if (vis != null)
                code.append(vis.prefix).append(vis.code).append(";\n");
        }
        return null;
    }

    private Code arrayIndexVisit(JmmNode node, Integer integer) {
        Code thisCode	= new Code();
        String targetName = node.getJmmChild(0).get("name");
        Code index = visit(node.getJmmChild(1));
        thisCode.prefix = index.prefix;

        if(!node.getJmmParent().getKind().equals("Assignment")){
            String temp = createTemp(".i32");
            thisCode.prefix += temp + " :=.i32 " + targetName + "[" +index.code+"].i32;\n";
            thisCode.code = temp;
        }else{

            thisCode.code = targetName + "[" +index.code+"].i32";
        }

        return thisCode;
    }

    private Code IfElseBlockVisit(JmmNode node, Integer integer){
        ifCount++;

        for(JmmNode child : node.getChildren()){
            Code vis = visit(child);
            if (vis != null)
                code.append(vis.prefix).append(vis.code).append(";\n");
        }

        return null;
    }

    private Code IfConditionVisit(JmmNode node, Integer integer) {
        Code condition=visit(node.getJmmChild(0));

        code.append(condition.prefix);
        code.append("if (" + condition.code+ ") goto IfBody"+ifCount+";\n");
        code.append("goto ElseLoop"+ifCount+";\n");

       return null;
    }

    private Code IfStatementVisit(JmmNode node, Integer integer){
        code.append("IfBody"+ifCount+":\n");

        for (JmmNode child : node.getChildren()){
            Code vis = visit(child);
            if (vis != null)
                code.append(vis.prefix).append(vis.code).append(";\n");
        }

        return null;
    }

    private Code ElseStatementVisit(JmmNode node, Integer integer){
        code.append("ElseLoop"+ifCount+":\n");

        for (JmmNode child : node.getChildren()){
            Code vis = visit(child);
            if (vis != null)
                code.append(vis.prefix).append(vis.code).append(";\n");
        }

        return null;
    }

}
