package pt.up.fe.comp.Jasmin.Instructions;
 
import java.util.HashMap;

import org.specs.comp.ollir.Descriptor;
import org.specs.comp.ollir.GetFieldInstruction;
import org.specs.comp.ollir.Operand;
import org.specs.comp.ollir.Method;

import pt.up.fe.comp.Jasmin.JasminUtils;

public class JasminGetFieldInstruction {

    public static String getInstructionCode(GetFieldInstruction instruction, Method method) {
        HashMap<String, Descriptor> varTable = method.getVarTable();

        StringBuilder sb = new StringBuilder();
        
        sb.append(JasminUtils.getLoadCode(instruction.getFirstOperand(), varTable));
        sb.append("getfield ").append(JasminUtils.getName(((Operand)instruction.getFirstOperand()).getName(), method));
        sb.append("/").append(((Operand)instruction.getSecondOperand()).getName()).append(" ");
        sb.append(JasminUtils.getJasminType(instruction.getFieldType()));
        sb.append("\n");

        return sb.toString();
    }
    
}
