package pt.up.fe.comp.Jasmin.Instructions;

import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.SingleOpInstruction;

import pt.up.fe.comp.Jasmin.JasminUtils;

public class JasminSingleOpInstruction {

    public static String getInstructionCode(SingleOpInstruction instruction, Method method) {
        return JasminUtils.getLoadCode(instruction.getSingleOperand(), method.getVarTable());
    }

}
