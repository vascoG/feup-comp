package pt.up.fe.comp.Jasmin;

import java.util.ArrayList;
import java.util.List;

import org.specs.comp.ollir.ClassUnit;
import org.specs.comp.ollir.OllirErrorException;

import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;

public class JmmBackend implements JasminBackend{

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        
        
        ClassUnit classUnit = ollirResult.getOllirClass();

        try {
            classUnit.checkMethodLabels();
        } catch (OllirErrorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        classUnit.buildCFGs();
        classUnit.buildVarTables();
        classUnit.show();

        String jasminCode = new JasminBuilder(classUnit).generateJasminCode();

        System.out.println(jasminCode);

        List<Report> reports = new ArrayList<>();

        return new JasminResult(ollirResult, jasminCode, reports);
    }
    
}
