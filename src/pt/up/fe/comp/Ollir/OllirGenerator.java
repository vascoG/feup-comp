package pt.up.fe.comp.Ollir;

import java.util.stream.Collectors;

import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;

public class OllirGenerator extends AJmmVisitor<Integer, Integer> {
    
    private final StringBuilder code;
    private final SymbolTable symbolTable;

    public OllirGenerator(SymbolTable symbolTable){
        this.code = new StringBuilder();
        this.symbolTable = symbolTable;

        addVisit(AstNode.PROGRAM, this::programVisit);
        addVisit(AstNode.CLASS_DECL, this::classDeclVisit);
        addVisit(AstNode.METHOD_DECL, this::methodDeclVisit);
        addVisit(AstNode.EXPR_STMT, this::exprStmtVisit); //2:29:55
        addVisit(AstNode.MEMBER_CALL, this::memberCallVisit); //2:39:24
    }

    public String getCode(){
        return code.toString();
    }

    private Integer programVisit(JmmNode program, Integer dummy){
        for(var importString : symbolTable.getImports()){
            code.append("import ").append(importString).append(";\n");
        }

        for(var child : program.getChildren()){
            visit(child);
        }

        return 0;
    }

    private Integer classDeclVisit(JmmNode classDecl, Integer dummy){

        code.append("public ").append(symbolTable.getClassName());
        var superClass = symbolTable.getSuper();
        if(superClass != null){
            code.append(" extends ").append(superClass);
        }

        code.append(" {\n");

        for(var child : classDecl.getChildren()){
            visit(child);
        }

        code.append("}\n");

        return 0;
    }

    private Integer methodDeclVisit(JmmNode methodDecl, Integer dummy){

        var methodSignature = methodDecl.getJmmChild(1).get("name");
        var isStatic = Boolean.valueOf(methodDecl.get("isStatic"));

        code.append(".method public ");
        if(isStatic){
            code.append("static ");
        }

        code.append("main(");

        var params = symbolTable.getParameters(methodSignature);

        var paramCode = params.stream()
                        .map(symbol -> OllirUtils.getCode(symbol))
                        .collect(Collectors.joining(", "));

        code.append(paramCode);

        code.append(")");

        code.append(OllirUtils.getCode(symbolTable.getReturnType(methodSignature)));

        code.append(" {");

        int lastParamIndex = -1;
        for(int i=0; i < methodDecl.getNumChildren(); i++){
            if(methodDecl.getJmmChild(i).getKind().equals("Param")){
                lastParamIndex = i;
            }
        }

        var stmts = methodDecl.getChildren().subList(lastParamIndex + 1, methodDecl.getNumChildren());

        for(var stmt : stmts){
            visit(stmt);
        }

        code.append("}\n");

        //.method public static main(args.array.String).V{

        return 0;
    }

    private Integer exprStmtVisit(JmmNode exprStmt, Integer dummy){
        visit(exprStmt.getJmmChild(0));
        code.append(";\n");

        return 0;
    }

    private Integer memberCallVisit(JmmNode memberCall, Integer dummy){
        visit(memberCall.getJmmChild(0));
        code.append(".").append(memberCall.getJmmChild(1)).append("(");
        visit(memberCall.getJmmChild(2));
        code.append(")");
        return 0;
    }
}
