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
            sb.append(" True1\niconst_0\ngoto Store1\nTrue1:\niconst_1\nStore1:");
            JasminUtils.changeStack(-1);

        }

        JasminUtils.changeStack(-1);

        sb.append("\n");

        return sb.toString();
    }
    
}
