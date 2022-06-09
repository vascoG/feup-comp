package pt.up.fe.comp.Jasmin.Instructions;

import org.specs.comp.ollir.BinaryOpInstruction;
import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.OperationType;

import pt.up.fe.comp.Jasmin.JasminUtils;

public class JasminBinaryOperInstruction {

    public static String getInstructionCode(BinaryOpInstruction instruction, Method method) {
        StringBuilder sb = new StringBuilder();

        sb.append(JasminUtils.getLoadCode(instruction.getLeftOperand(), method.getVarTable()));
        

        if(instruction.getOperation().getOpType()==OperationType.LTH){
            sb.append(JasminUtils.getLoadCode(instruction.getRightOperand(), method.getVarTable()));
            sb.append(JasminUtils.getOperationCode(instruction.getOperation().getOpType()));
            JasminUtils.conditionsCounter++;
            sb.append(" True").append(JasminUtils.conditionsCounter);
            sb.append("\niconst_0\ngoto FinalCond").append(JasminUtils.conditionsCounter);
            sb.append("\nTrue").append(JasminUtils.conditionsCounter);
            sb.append(":\niconst_1\nFinalCond").append(JasminUtils.conditionsCounter).append(":");
        }
        else if(instruction.getOperation().getOpType()==OperationType.ANDB){
            JasminUtils.conditionsCounter++;
            sb.append(JasminUtils.getOperationCode(instruction.getOperation().getOpType()));
            sb.append(" False").append(JasminUtils.conditionsCounter);
            sb.append("\n");
            sb.append(JasminUtils.getLoadCode(instruction.getRightOperand(), method.getVarTable()));
            sb.append(JasminUtils.getOperationCode(instruction.getOperation().getOpType()));
            sb.append(" False").append(JasminUtils.conditionsCounter);
            sb.append("\niconst_1\ngoto FinalCond").append(JasminUtils.conditionsCounter);
            sb.append("\nFalse").append(JasminUtils.conditionsCounter);
            sb.append(":\niconst_0\nFinalCond").append(JasminUtils.conditionsCounter).append(":");

        }
        else{
        sb.append(JasminUtils.getLoadCode(instruction.getRightOperand(), method.getVarTable()));
        sb.append(JasminUtils.getOperationCode(instruction.getOperation().getOpType()));
        }

        JasminUtils.changeStack(-1);

        sb.append("\n");

        return sb.toString();
    }
    
}
