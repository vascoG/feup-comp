package pt.up.fe.comp.Jasmin;

import java.util.HashMap;

import org.specs.comp.ollir.AccessModifiers;
import org.specs.comp.ollir.ArrayOperand;
import org.specs.comp.ollir.ArrayType;
import org.specs.comp.ollir.AssignInstruction;
import org.specs.comp.ollir.CallInstruction;
import org.specs.comp.ollir.ClassType;
import org.specs.comp.ollir.Descriptor;
import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.ElementType;
import org.specs.comp.ollir.Instruction;
import org.specs.comp.ollir.LiteralElement;
import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.Operand;
import org.specs.comp.ollir.PutFieldInstruction;
import org.specs.comp.ollir.SingleOpInstruction;
import org.specs.comp.ollir.Type;

import pt.up.fe.comp.Jasmin.Instructions.JasminAssignInstruction;
import pt.up.fe.comp.Jasmin.Instructions.JasminPutFieldInstruction;
import pt.up.fe.comp.Jasmin.Instructions.JasminCallInstruction;
import pt.up.fe.comp.Jasmin.Instructions.JasminSingleOpInstruction;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

public class JasminUtils {

    public static String modifierToString(AccessModifiers accessModifier) {
        switch (accessModifier) {
            case PUBLIC:
                return "public";
            case PRIVATE:
                return "private";
            case DEFAULT:
                return "private";
            case PROTECTED:
                return "protected";
            default:
                return "";
        }
    }
    
    public static String getJasminType(Type type){
        switch(type.getTypeOfElement())
        {
            case ARRAYREF: return getJasminArrayType((ArrayType)type);
            case THIS: return JasminBuilder.classUnit.getClassName();
            case CLASS: return ((ClassType)type).getName();
            case OBJECTREF: return ((ClassType)type).getName();
            default: return getJasminType(type.getTypeOfElement());
        }

    }

    private static String getJasminArrayType(ArrayType type) {
        StringBuilder sb = new StringBuilder();

        sb.append("[".repeat(type.getNumDimensions()));
        sb.append(getJasminType(type.getTypeOfElements()));

        return sb.toString();
    }

    public static String getJasminType(ElementType type){
        switch (type) {
            case INT32: return "I";
            case BOOLEAN: return "Z";
            case STRING: return "Ljava/lang/String;";
            case VOID: return "V";
            default: return "";
        }

    }

    public static String getInstructionCode(Instruction instruction, Method method)
    { 
        switch(instruction.getInstType()){ 
        case CALL:
            return JasminCallInstruction.getInstructionCode((CallInstruction)instruction, method);
        case ASSIGN:
            return JasminAssignInstruction.getInstructionCode((AssignInstruction) instruction, method);
        case NOPER:
            return JasminSingleOpInstruction.getInstructionCode((SingleOpInstruction)instruction, method);
        case PUTFIELD:
            return JasminPutFieldInstruction.getInstructionCode((PutFieldInstruction)instruction,method);
        default: throw new NotImplementedException(instruction.getInstType());
    }
    }

    public static String getArrayLengthCode(int reg){
        return aload(reg) + "arraylength \n";
    }

    private static String aload(int reg) {
        StringBuilder sb = new StringBuilder();
        sb.append("aload ").append(reg).append("\n");
        return sb.toString();
    }

    public static String getLoadCode(Element param, HashMap<String, Descriptor> varTable) {
        ElementType type = param.getType().getTypeOfElement();
        
        if(param.isLiteral())
            return ldc(((LiteralElement)param).getLiteral());
        else if(param instanceof ArrayOperand)
            return getArrayAccessCode(param, varTable);
        else if (type == ElementType.INT32 || type == ElementType.BOOLEAN)
            return iload(varTable.get(((Operand)param).getName()).getVirtualReg());
        else
            return aload(varTable.get(((Operand)param).getName()).getVirtualReg());
    }

    private static String getArrayAccessCode(Element param, HashMap<String, Descriptor> varTable) {
        int arrayReg = varTable.get(((Operand)param).getName()).getVirtualReg();
        Element index = ((ArrayOperand) param).getIndexOperands().get(0);
        int indexReg = varTable.get(((Operand)index).getName()).getVirtualReg();

        StringBuilder sb = new StringBuilder();
        sb.append(aload(arrayReg)).append(iload(indexReg)).append("iaload\n");

        return sb.toString();
    }

    private static String iload(int reg) {
        StringBuilder sb = new StringBuilder();
        sb.append("iload ").append(reg).append("\n");
        return sb.toString();
    }

    private static String ldc(String literal) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("ldc ").append(literal).append("\n");

        return sb.toString();
    }

    public static String getStoreCode(Element param, HashMap<String, Descriptor> varTable) {
        ElementType type = param.getType().getTypeOfElement();

        if(param instanceof ArrayOperand)
            return getStoreArrayAccessCode(param, varTable);
        else if (type == ElementType.INT32 || type == ElementType.BOOLEAN)
            return istore(varTable.get(((Operand)param).getName()).getVirtualReg());
        else
            return astore(varTable.get(((Operand)param).getName()).getVirtualReg());
    }

    private static String astore(int reg) {
        StringBuilder sb = new StringBuilder();
        sb.append("astore ").append(reg).append("\n");

        return sb.toString();
    }

    private static String istore(int reg) {
        StringBuilder sb = new StringBuilder();
        sb.append("istore ").append(reg).append("\n");

        return sb.toString();
    }

    private static String getStoreArrayAccessCode(Element param, HashMap<String, Descriptor> varTable) {
        int arrayReg = varTable.get(((Operand)param).getName()).getVirtualReg();
        Element index = ((ArrayOperand) param).getIndexOperands().get(0);
        int indexReg = varTable.get(((Operand)index).getName()).getVirtualReg();

        StringBuilder sb = new StringBuilder();
        sb.append(aload(arrayReg)).append(iload(indexReg)).append("iastore\n");

        return sb.toString();
    }

    
}
