package pt.up.fe.comp.Jasmin.Instructions;

import java.util.HashMap;

import org.specs.comp.ollir.ArrayOperand;
import org.specs.comp.ollir.AssignInstruction;
import org.specs.comp.ollir.Descriptor;
import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.Instruction;
import org.specs.comp.ollir.Method;

import pt.up.fe.comp.Jasmin.JasminUtils;

public class JasminAssignInstruction {
    public static String getInstructionCode(AssignInstruction instruction, Method method) {
        Instruction rhs = instruction.getRhs();
        Element lhs = instruction.getDest();
        HashMap<String, Descriptor> varTable = method.getVarTable();

        StringBuilder sb = new StringBuilder();

        if (lhs instanceof ArrayOperand)
            sb.append(JasminUtils.getStoreArrayAccessCode(lhs, method,rhs));

        else{
            sb.append(JasminUtils.getInstructionCode(rhs, method));

            sb.append(JasminUtils.getStoreCode(lhs, varTable));
        }

      

        return sb.toString();
    }
}
