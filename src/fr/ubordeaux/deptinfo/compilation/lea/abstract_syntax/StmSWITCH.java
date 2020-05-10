package fr.ubordeaux.deptinfo.compilation.lea.abstract_syntax;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import fr.ubordeaux.deptinfo.compilation.lea.type.TypeCode;
import fr.ubordeaux.deptinfo.compilation.lea.type.TypeException;

public class StmSWITCH extends StmList {

	private Expr expr;
	private Stm defaultStm; // default

	public StmSWITCH(Expr expr, List<Stm> stms, Stm stm, int line, int column) {
		super(stms, line, column);
		this.expr = expr;
		this.defaultStm = stm;
	}

	@Override
	public String generateCode() throws CodeException {
		String result = "";
		result += super.generateCode();
		
		String var = "_switch_test__" + this.getId();
		List<String> label_case = new ArrayList<String>();
		String label_end = "_switch_label_end__" + this.getId();
		
		List<StmCASE> listCase = new ArrayList<StmCASE>();
		List<Stm> listStm = getStms();

		for (Stm stm : listStm) 
		{
			listCase.add((StmCASE)stm);
		}

		Iterator<StmCASE> it = listCase.iterator();
		int cpt=0;

		if(it.hasNext()){
			StmCASE s = it.next();
			label_case.add("_label_case_" + cpt + "__" + this.getId());
			result += tab() + "int " + var + " = " + s.getExpr().generateCode() + " == " + expr.generateCode() + ";" + NL;
			result += tab() + "if (" + var + ")" + NL;
			incIndent();
				result += tab() + "goto " + label_case.get(cpt++) + ";" + NL;
			decIndent();
		}

		while(it.hasNext()){
			StmCASE s = it.next();
			label_case.add("_label_case_" + cpt + "__" + this.getId());
			result += tab() + "{" + NL;
			incIndent();
				result += tab() + var + " = " + s.getExpr().generateCode() + " == " + expr.generateCode() + ";" + NL;
				result += tab() + "if (" + var + ")" + NL;
				incIndent();
					result += tab() + "goto " + label_case.get(cpt++) + ";" + NL;
				decIndent();
				result += tab() + "{" + NL;
				incIndent();
		}

		result += defaultStm.generateCode();

		for(int i=0; i<cpt; i++){
			decIndent();
			result += tab() + "}" + NL;
		}

		cpt = 0;
		it = listCase.iterator();
		while(it.hasNext()){
			StmCASE s = it.next();
			result += tab() + label_case.get(cpt) + ":{}" + NL;
			result += s.getSon().generateCode();
			result += tab() + "goto " + label_end + ";" + NL;
		}


		return result;

	}

	@Override
	public void checkType() throws TypeException {
		expr.checkType();
		TypeCode[] typeCodes = {TypeCode.INTEGER, TypeCode.ENUM};
		expr.getType().assertType(this, typeCodes);

		if (defaultStm != null)
			defaultStm.checkType();
		Iterator<Stm> iterator = getStms().iterator();
		while (iterator.hasNext()) {
			StmCASE stmCASE = (StmCASE)iterator.next();
			stmCASE.checkType();
			stmCASE.getExpr().getType().assertType(stmCASE, expr.getType());
			
		}
	}

	@Override
	public void indent() {
		Iterator<Stm> iterator = getStms().iterator();
		while (iterator.hasNext()) {
			Stm stm = iterator.next();
			stm.setIndent(getIndent());
			stm.indent();
		}
		if (defaultStm != null) {
			defaultStm.setIndent(getIndent());
			defaultStm.indent();
		}
	}

	@Override
	public String toString() {
		return "switch (" + expr + ")";
	}

}
