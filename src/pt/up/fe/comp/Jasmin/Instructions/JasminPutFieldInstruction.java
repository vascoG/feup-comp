package pt.up.fe.comp.Jasmin.Instructions;

import java.util.HashMap;

import org.specs.comp.ollir.Descriptor;
import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.Operand;
import org.specs.comp.ollir.PutFieldInstruction;

import pt.up.fe.comp.Jasmin.JasminUtils;

public class JasminPutFieldInstruction {

    public static String getInstructionCode(PutFieldInstruction instruction, Method method) {

        HashMap<String, Descriptor> varTable = method.getVarTable();

        StringBuilder sb = new StringBuilder();
        
        sb.append(JasminUtils.getLoadCode(instruction.getFirstOperand(), varTable));
        sb.append(JasminUtils.getLoadCode(instruction.getThirdOperand(), varTable));
        sb.append("putfield ").append(JasminUtils.getName(((Operand)instruction.getFirstOperand()).getName(), method));
        sb.append("/").append(((Operand)instruction.getSecondOperand()).getName()).append(" ");
        sb.append(JasminUtils.getJasminType(instruction.getSecondOperand().getType()));
        sb.append("\n");

        JasminUtils.changeStack(-2);


        return sb.toString();
    }
    
}
