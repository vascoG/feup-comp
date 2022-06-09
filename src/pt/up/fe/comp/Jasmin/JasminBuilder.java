package pt.up.fe.comp.Jasmin;

import java.util.ArrayList;

import org.specs.comp.ollir.AccessModifiers;
import org.specs.comp.ollir.ClassUnit;
import org.specs.comp.ollir.Descriptor;
import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.ElementType;
import org.specs.comp.ollir.Field;
import org.specs.comp.ollir.Instruction;
import org.specs.comp.ollir.CallInstruction;
import org.specs.comp.ollir.InstructionType;
import org.specs.comp.ollir.Method;

import pt.up.fe.specs.util.exceptions.NotImplementedException;

public class JasminBuilder {
    private StringBuilder jasminCode;
    public static ClassUnit classUnit;

    public JasminBuilder( ClassUnit classUnit) {
        this.jasminCode = new StringBuilder();
        JasminBuilder.classUnit = classUnit;
        JasminUtils.conditionsCounter=0;
    }

    public String generateJasminCode()
    {
        addClassName();
        addSuperClassName();
        addFields();
        addMethods();

        return jasminCode.toString();
    }

    private void addMethods() {
        for(Method method : classUnit.getMethods())
        { 
            jasminCode.append("\n.method public ");
            if(method.isConstructMethod())
                jasminCode.append("<init>");
            else
            {
                if(method.isStaticMethod()) jasminCode.append("static ");
                if(method.isFinalMethod()) jasminCode.append("final ");
                jasminCode.append(method.getMethodName());
            }

            jasminCode.append("(");

            for(Element param : method.getParams())
                jasminCode.append(JasminUtils.getJasminType(param.getType()));

            jasminCode.append(")");
            jasminCode.append(JasminUtils.getJasminType(method.getReturnType()));
            jasminCode.append("\n");

            JasminUtils.currentStack=0;
            JasminUtils.maxStack=0;

            StringBuilder instructionsCode = new StringBuilder();

            for(Instruction instruction : method.getInstructions())
            { 
                for (String label : method.getLabels(instruction))
                    instructionsCode.append(label).append(":\n");

                instructionsCode.append(JasminUtils.getInstructionCode(instruction, method));
                if(instruction.getInstType()==InstructionType.CALL)
                { 
                    if (((CallInstruction) instruction).getReturnType().getTypeOfElement() != ElementType.VOID){ 
                        instructionsCode.append("pop\n");
                        JasminUtils.changeStack(-1);
                    } 
                }
            } 

            ArrayList<Integer> variables = new ArrayList<>();
            for (Descriptor var : method.getVarTable().values()) {
                if (!variables.contains(var.getVirtualReg()))
                    variables.add(var.getVirtualReg());
            }
            if (!variables.contains(0) &&  !method.isStaticMethod())
                variables.add(0);

            jasminCode.append(".limit locals ").append(variables.size()).append("\n");
            jasminCode.append(".limit stack ").append(JasminUtils.maxStack).append("\n");

            if(JasminUtils.currentStack!=0)
                System.err.println("current stack is not empty!: "+JasminUtils.currentStack+"\nmaxstack: "+JasminUtils.maxStack);
            
            jasminCode.append(instructionsCode.toString());

            if(method.isConstructMethod())
                jasminCode.append("return\n");

            jasminCode.append(".end method\n");
        }
    }

    private void addFields() {
        for(Field field : classUnit.getFields())
        {
            jasminCode.append(".field ");
            jasminCode.append(JasminUtils.modifierToString(field.getFieldAccessModifier()));

            if(field.isStaticField())
                jasminCode.append(" static");
            if(field.isFinalField())
                jasminCode.append(" final");
            jasminCode.append(" ");

            jasminCode.append(field.getFieldName()).append(" ");

            jasminCode.append(JasminUtils.getJasminType(field.getFieldType())).append(" ");

            if(field.isInitialized())
                { 
                    jasminCode.append("= ");
                    jasminCode.append(field.getInitialValue());
                }
            jasminCode.append("\n");
        }
    }

    

    private void addSuperClassName() {
        String superClassName = classUnit.getSuperClass();
        if(superClassName==null)
            superClassName="java/lang/Object";

        jasminCode.append(".super ");
        jasminCode.append(superClassName);
        jasminCode.append("\n\n");
    }

    private void addClassName() {
        jasminCode.append(".class public ");
        jasminCode.append(classUnit.getClassName());
        jasminCode.append("\n");
    }
    
}
