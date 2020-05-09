package fr.ubordeaux.deptinfo.compilation.lea.abstract_syntax;

import fr.ubordeaux.deptinfo.compilation.lea.type.TypeCode;
import fr.ubordeaux.deptinfo.compilation.lea.type.TypeException;

public class StmWHILE extends StmUnary {

	private Expr test;

	public StmWHILE(Expr test, Stm stm, int line, int column) {
		super(stm, line, column);
		this.test = test;
	}

	@Override
	public String generateCode() throws CodeException {
		String result = "";
		result += super.generateCode();

		String var = "_while_test__" + this.getId();
		String label_loop = "_while_label_loop__" + this.getId();
		String label_end = "_while_label_end__" + this.getId();

		result += tab() + "int " + var + ";" + NL;
		result += tab() + label_loop + ":{" + NL;
		incIndent();
			result += tab() + var + " = " + test.generateCode() + ";" + NL;
			result += tab() + "if (!" + var + ")" + NL;
			incIndent();
				result += tab() + "goto " + label_end + ";" + NL;
			decIndent();
			result += tab() + "{" + NL;
			incIndent();
				result += getSon().generateCode();
			decIndent();
			result += tab() + "}" + NL;
			result += tab() + "goto " + label_loop + ";" + NL;
		decIndent();
		result += tab() + "}" + NL;
		result += tab() + label_end + ":{}" + NL;
		return result;
	}

	@Override
	public void checkType() throws TypeException {
		test.checkType();
		if (this.getSon()!=null)
			getSon().checkType();
		test.getType().assertType(this, TypeCode.BOOLEAN);
	}

	@Override
	public String toString() {
		return "while ("+ test + ")";
	}

}
