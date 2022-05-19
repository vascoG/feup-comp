package pt.up.fe.comp.SymbolTable;



import pt.up.fe.comp.Parser.BaseNode;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class LineColAnotator extends PreorderJmmVisitor<Boolean, Boolean> {

    public LineColAnotator(){
        setDefaultVisit(this::annotateLineCol);
    }

    private Boolean annotateLineCol(JmmNode node, Boolean dummy){
        var baseNode = (BaseNode) node;


        node.put("line", Integer.toString(baseNode.getBeginLine()));
        node.put("col", Integer.toString(baseNode.getBeginColumn()));

        return true;
    }
}