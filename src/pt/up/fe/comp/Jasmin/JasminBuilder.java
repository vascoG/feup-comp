package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.AccessModifiers;
import org.specs.comp.ollir.ClassUnit;
import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.Field;
import org.specs.comp.ollir.Instruction;
import org.specs.comp.ollir.Method;

public class JasminBuilder {
    private StringBuilder jasminCode;
    public static ClassUnit classUnit;

    public JasminBuilder( ClassUnit classUnit) {
        this.jasminCode = new StringBuilder();
        JasminBuilder.classUnit = classUnit;
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
            jasminCode.append(".method public ");
            if(method.isConstructMethod())
                jasminCode.append("<init> ");
            else
            {
                if(method.isStaticMethod()) jasminCode.append("static ");
                if(method.isFinalMethod()) jasminCode.append("final ");
                jasminCode.append(method.getMethodName());
            }

            jasminCode.append("(");

            for(Element param : method.getParams())
                JasminUtils.getJasminType(param.getType());

            jasminCode.append(")");
            jasminCode.append(JasminUtils.getJasminType(method.getReturnType()));
            jasminCode.append("\n");

            if(!method.isConstructMethod())
                {
                    jasminCode.append("limit locals 99\n");
                    jasminCode.append("limit stack 99\n");
                }

            for(Instruction instruction : method.getInstructions())
            { 
                for (String label : method.getLabels(instruction))
                    jasminCode.append(label).append("\n");

                jasminCode.append(JasminUtils.getInstructionCode(instruction, method));
            } 
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
        jasminCode.append("\n");
    }

    private void addClassName() {
        jasminCode.append(".class public ");
        jasminCode.append(classUnit.getClassName());
        jasminCode.append("\n");
    }
    
}
