package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.ClassUnit;

public class JasminBuilder {
    private StringBuilder jasminCode;
    private ClassUnit classUnit;

    public JasminBuilder( ClassUnit classUnit) {
        this.jasminCode = new StringBuilder();
        this.classUnit = classUnit;
    }

    public String generateJasminCode()
    {
        addClassName();
        addSuperClassName();

        //fields

        //methods
        return jasminCode.toString();
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
        jasminCode.append(".class ");
        jasminCode.append(classUnit.getClassName());
        jasminCode.append("\n");
    }
    
}
