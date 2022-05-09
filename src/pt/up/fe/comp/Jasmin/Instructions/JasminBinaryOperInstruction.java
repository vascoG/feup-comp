package pt.up.fe.comp.Jasmin.Instructions;

import org.specs.comp.ollir.BinaryOpInstruction;
import org.specs.comp.ollir.Method;

import pt.up.fe.comp.Jasmin.JasminUtils;

public class JasminBinaryOperInstruction {

    public static String getInstructionCode(BinaryOpInstruction instruction, Method method) {
        StringBuilder sb = new StringBuilder();

        sb.append(JasminUtils.getLoadCode(instruction.getLeftOperand(), method.getVarTable()));
        sb.append(JasminUtils.getLoadCode(instruction.getRightOperand(), method.getVarTable()));
        sb.append(JasminUtils.getOperationCode(instruction.getOperation().getOpType()));

        sb.append("\n");

        return sb.toString();
    }
    
}
