package pt.up.fe.comp.Jasmin.Instructions;

import org.specs.comp.ollir.BinaryOpInstruction;
import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.OperationType;

import pt.up.fe.comp.Jasmin.JasminUtils;

public class JasminBinaryOperInstruction {

    public static String getInstructionCode(BinaryOpInstruction instruction, Method method) {
        StringBuilder sb = new StringBuilder();

        sb.append(JasminUtils.getLoadCode(instruction.getLeftOperand(), method.getVarTable()));
        sb.append(JasminUtils.getLoadCode(instruction.getRightOperand(), method.getVarTable()));
        sb.append(JasminUtils.getOperationCode(instruction.getOperation().getOpType()));

        if(instruction.getOperation().getOpType()==OperationType.LTH){
            JasminUtils.conditionsCounter++;
            sb.append(" True").append(JasminUtils.conditionsCounter);
            sb.append("\niconst_0\ngoto Store").append(JasminUtils.conditionsCounter);
            sb.append("\nTrue").append(JasminUtils.conditionsCounter);
            sb.append(":\niconst_1\nStore").append(JasminUtils.conditionsCounter).append(":");
            JasminUtils.changeStack(1);

            JasminUtils.changeStack(-1);
        }

        JasminUtils.changeStack(-1);

        sb.append("\n");

        return sb.toString();
    }
    
}
