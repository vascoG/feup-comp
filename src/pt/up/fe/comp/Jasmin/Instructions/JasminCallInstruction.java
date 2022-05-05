package pt.up.fe.comp.Jasmin.Instructions;

import java.util.ArrayList;

import org.specs.comp.ollir.CallInstruction;
import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.LiteralElement;
import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.Operand;
import org.specs.comp.ollir.Type;

import pt.up.fe.comp.Jasmin.JasminUtils;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

public class JasminCallInstruction {

    public static String getInstructionCode(CallInstruction instruction, Method method) {
        switch (instruction.getInvocationType()) {
            case arraylength: return arrayLength(instruction,method);
            case invokestatic: return invokeStatic(instruction,method);
            case invokespecial: return invokeEspecial(instruction,method);
            default:
                throw new NotImplementedException(instruction.getInvocationType());
        }
    }

    private static String invokeEspecial(CallInstruction instruction, Method method) {
        return null;
    }

    private static String invokeStatic(CallInstruction instruction, Method method) {
        ArrayList<Element> params = instruction.getListOfOperands();
        Type returnType = instruction.getReturnType();

        StringBuilder sb = new StringBuilder();

        for(Element param : params)
            sb.append(JasminUtils.getLoadCode(param, method.getVarTable()));
        
        sb.append("invokestatic ");
        sb.append(((Operand)instruction.getFirstArg()).getName()).append("/");

        String callMethodName = ((LiteralElement)instruction.getSecondArg()).getLiteral();
        sb.append(callMethodName.replace("\"", ""));

        sb.append("(");
        for(Element param : params)
            sb.append(JasminUtils.getJasminType(param.getType()));
        sb.append(")");
        
        sb.append(JasminUtils.getJasminType(returnType));
        sb.append("\n");

        
        return sb.toString();
    }

    private static String arrayLength(CallInstruction instruction, Method method) {
        Element array = instruction.getFirstArg();
        int arrayReg = method.getVarTable().get(((Operand)array).getName()).getVirtualReg();
        return JasminUtils.getArrayLengthCode(arrayReg);
    }
    
}
