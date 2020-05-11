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
			listCase.add((StmCASE)stm);

		Iterator<StmCASE> it = listCase.iterator();
		int cnt = 0;
		int nbElmts = listCase.size();
		String caseValue;

		// Table des branchements des cases
		while(it.hasNext()){
			StmCASE s = it.next();
			caseValue = s.getExpr().generateCode();
			label_case.add("_switch_label_case_" + caseValue + "__" + this.getId());
			if (cnt != 0) {
				result += tab() + "{" + NL;
				incIndent();
			}
					result += (cnt == 0) ? tab() + "int " : tab();
					result += var + " = " + expr.generateCode() + " == " + caseValue + ";" + NL;
					result += tab() + "if (" + var + ")" + NL;
					incIndent();
						result += tab() + "goto " + label_case.get(cnt++) + ";" + NL;
					decIndent();
		}

		// Traitement du cas default
		result += tab() + "{" + NL;
		incIndent();
			result += defaultStm.generateCode();
		decIndent();
		result += tab() + "}" + NL;

		for(int i = 0; i < cnt - 1; i++){
			decIndent();
			result += tab() + "}" + NL;
		}

		// Liste des cases et instructions associées
		cnt = 0;
		it = listCase.iterator();
		while(it.hasNext()){
			StmCASE s = it.next();
			result += NL + tab() + label_case.get(cnt++) + ":{" + NL;
			incIndent();
				result += s.generateCode();
				if (cnt != nbElmts)
					result += tab() + "goto " + label_end + ";" + NL;
			decIndent();
			result += tab() + "}" + NL;
		}

		result += tab() + label_end + ":{}" + NL;
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
