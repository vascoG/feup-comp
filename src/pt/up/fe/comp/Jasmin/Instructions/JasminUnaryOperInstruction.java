package pt.up.fe.comp.Jasmin.Instructions;

import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.UnaryOpInstruction;

import pt.up.fe.comp.Jasmin.JasminUtils;

public class JasminUnaryOperInstruction {

    public static String getInstructionCode(UnaryOpInstruction instruction, Method method) {
        StringBuilder sb = new StringBuilder();
        

        sb.append(JasminUtils.getLoadCode(instruction.getOperand(), method.getVarTable()));
        sb.append("iconst_0\n");
        JasminUtils.changeStack(1);
        sb.append(JasminUtils.getOperationCode(instruction.getOperation().getOpType()));

         JasminUtils.conditionsCounter++;
        sb.append(" True").append(JasminUtils.conditionsCounter);
        sb.append("\niconst_0\ngoto FinalCond").append(JasminUtils.conditionsCounter);
        sb.append("\nTrue").append(JasminUtils.conditionsCounter);
        sb.append(":\niconst_1\nFinalCond").append(JasminUtils.conditionsCounter).append(":");
        
        JasminUtils.changeStack(-1);


        sb.append("\n");

        return sb.toString();

    }
    
}
