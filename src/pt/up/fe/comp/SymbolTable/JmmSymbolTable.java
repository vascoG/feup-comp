package pt.up.fe.comp.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class JmmSymbolTable implements SymbolTable {
    private final List<String> imports = new ArrayList<>();
    private String className;
    private String superClassName;
    private final Map<String, Symbol> fields = new HashMap<>();
    private final Map<String, JmmMethod> methods = new HashMap<>();

    public void addImport(String name)
    {
        imports.add(name);
    }

    public void setClassName(String name)
    {
        this.className = name;
    }

    public void setSuperClassName(String name)
    {
        this.superClassName = name;
    }

    public void addField(Type type, String name)
    {
        fields.put(name, new Symbol(type, name));
    }

    public void addMethod(Type returnType, String name, List<Symbol> parameters)
    {
        methods.put(name, new JmmMethod(name, returnType, parameters));
    }

    @Override
    public List<String> getImports() {
        return imports;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getSuper() {
        return superClassName;
    }

    @Override
    public List<Symbol> getFields() {
        return new ArrayList<>(fields.values());
    }

    @Override
    public List<String> getMethods() {
        return new ArrayList<>(methods.keySet());
    }

    @Override
    public Type getReturnType(String methodSignature) {
        return methods.get(methodSignature).getReturnType();
    }

    @Override
    public List<Symbol> getParameters(String methodSignature) {
        return methods.get(methodSignature).getParameters();
    }

    @Override
    public List<Symbol> getLocalVariables(String methodSignature) {
        return methods.get(methodSignature).getLocalVariables();
    }

    public void addMethod(JmmMethod method) {
        methods.put(method.getName(), method);
    }
    
}
