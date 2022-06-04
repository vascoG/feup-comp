package pt.up.fe.comp.Jasmin.Instructions;


import org.specs.comp.ollir.CondBranchInstruction;
import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.Method;

import pt.up.fe.comp.Jasmin.JasminUtils;

public class JasminBranchInstruction {

    public static String getInstructionCode(CondBranchInstruction instruction, Method method) {
        
        Element first = instruction.getOperands().get(0);

        StringBuilder sb = new StringBuilder();
        sb.append(JasminUtils.getLoadCode(first, method.getVarTable()));
        sb.append("ifne ").append(instruction.getLabel());
        sb.append("\n");
        
        JasminUtils.changeStack(-1);


        return sb.toString();
    }
    
}
