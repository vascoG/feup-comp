package pt.up.fe.comp.Jasmin.Instructions;

import org.specs.comp.ollir.ElementType;
import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.ReturnInstruction;

import pt.up.fe.comp.Jasmin.JasminUtils;

public class JasminReturnInstruction {

    public static String getInstructionCode(ReturnInstruction instruction, Method method) {
        if(!instruction.hasReturnValue())
            return "return\n";

        ElementType returnType = instruction.getOperand().getType().getTypeOfElement();

        StringBuilder sb = new StringBuilder();

        sb.append(JasminUtils.getLoadCode(instruction.getOperand(), method.getVarTable()));

        if (returnType == ElementType.INT32 || returnType == ElementType.BOOLEAN)
            sb.append("ireturn\n");
        else
            sb.append("areturn\n");

        JasminUtils.changeStack(-1);

        return sb.toString();
    }
    
}
