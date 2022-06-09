package pt.up.fe.comp.Jasmin.Instructions;

import java.util.ArrayList;

import org.specs.comp.ollir.CallInstruction;
import org.specs.comp.ollir.ClassType;
import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.ElementType;
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
            case invokevirtual: return invokeVirtual(instruction,method);
            case NEW: return newCall(instruction,method);
            case ldc: return ldc(instruction,method);
            default:
                throw new NotImplementedException(instruction.getInvocationType());
        }
    }

    private static String ldc(CallInstruction instruction, Method method) {

        return JasminUtils.getLoadCode(instruction.getFirstArg(), method.getVarTable());
    }

    private static String newCall(CallInstruction instruction, Method method) {
        ArrayList<Element> ops = instruction.getListOfOperands();
        Type returnType = instruction.getFirstArg().getType();
        
        StringBuilder sb = new StringBuilder();


        for(Element element : ops)
                sb.append(JasminUtils.getLoadCode(element, method.getVarTable()));

        if(returnType.getTypeOfElement()==ElementType.OBJECTREF)
        { 
            sb.append("new ");
            sb.append(((Operand)instruction.getFirstArg()).getName()).append("\n");
            JasminUtils.changeStack(1);
        }

        else if (returnType.getTypeOfElement()==ElementType.ARRAYREF)
        { 
            sb.append("newarray int\n");
        }
        else
            throw new NotImplementedException(returnType.getTypeOfElement());

        return sb.toString();
    }

    private static String invokeVirtual(CallInstruction instruction, Method method) {
        ArrayList<Element> params = instruction.getListOfOperands();
        Type returnType = instruction.getReturnType();

        StringBuilder sb = new StringBuilder();

        sb.append(JasminUtils.getLoadCode(instruction.getFirstArg(), method.getVarTable()));

        for(Element param : params)
            sb.append(JasminUtils.getLoadCode(param, method.getVarTable()));
        
        sb.append("invokevirtual ");
        sb.append(JasminUtils.getName(((ClassType)instruction.getFirstArg().getType()).getName(),method)).append("/");

        String callMethodName = ((LiteralElement)instruction.getSecondArg()).getLiteral();
        sb.append(callMethodName.replace("\"", ""));

        sb.append("(");
        for(Element param : params){
            sb.append(JasminUtils.getJasminType(param.getType()));
            JasminUtils.changeStack(-1);
        }
        sb.append(")");
        
        sb.append(JasminUtils.getJasminType(returnType));
        sb.append("\n");

        if(returnType.getTypeOfElement()== ElementType.VOID)
            JasminUtils.changeStack(-1);


        return sb.toString();
    }

    private static String invokeEspecial(CallInstruction instruction, Method method) {
        ArrayList<Element> params = instruction.getListOfOperands();
        Type returnType = instruction.getReturnType();

        StringBuilder sb = new StringBuilder();

        sb.append(JasminUtils.getLoadCode(instruction.getFirstArg(), method.getVarTable()));

        sb.append("invokespecial ");
        if(instruction.getFirstArg().getType().getTypeOfElement()==ElementType.THIS)
            { 
                if(method.getOllirClass().getSuperClass()==null)
                    sb.append("java/lang/Object");
                else
                    sb.append(method.getOllirClass().getSuperClass());
            }
        else
            sb.append(JasminUtils.getJasminEspecialType(instruction.getFirstArg().getType()));
        sb.append(".<init>(");

        for (Element param : params){ 
            JasminUtils.changeStack(-1);
            sb.append(JasminUtils.getJasminType(param.getType()));
        }

        sb.append(")");

        sb.append(JasminUtils.getJasminType(returnType));

        sb.append("\n");

        JasminUtils.changeStack(-1);


        return sb.toString();
    }

    private static String invokeStatic(CallInstruction instruction, Method method) {
        ArrayList<Element> params = instruction.getListOfOperands();
        Type returnType = instruction.getReturnType();

        StringBuilder sb = new StringBuilder();

        for(Element param : params)
            sb.append(JasminUtils.getLoadCode(param, method.getVarTable()));
        
        sb.append("invokestatic ");
        sb.append(JasminUtils.getName(((Operand)instruction.getFirstArg()).getName(),method)).append("/");

        String callMethodName = ((LiteralElement)instruction.getSecondArg()).getLiteral();
        sb.append(callMethodName.replace("\"", ""));

        sb.append("(");
        for(Element param : params){ 
            JasminUtils.changeStack(-1);
            sb.append(JasminUtils.getJasminType(param.getType()));
        } 
        sb.append(")");
        
        sb.append(JasminUtils.getJasminType(returnType));
        if(returnType.getTypeOfElement()!= ElementType.VOID)
            JasminUtils.changeStack(1);
        sb.append("\n");

        return sb.toString();
    }

    private static String arrayLength(CallInstruction instruction, Method method) {
        Element array = instruction.getFirstArg();
        int arrayReg = method.getVarTable().get(((Operand)array).getName()).getVirtualReg();
        return JasminUtils.getArrayLengthCode(arrayReg);
    }
    
}
