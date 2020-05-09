package fr.ubordeaux.deptinfo.compilation.lea.abstract_syntax;

import fr.ubordeaux.deptinfo.compilation.lea.type.TypeCode;
import fr.ubordeaux.deptinfo.compilation.lea.type.TypeException;

public class StmFOR extends StmTernary {

	private Expr test;

	public StmFOR(Expr test, Stm stm1, Stm stm2, Stm stm3, int line, int column) {
		super(stm1, stm2, stm3, line, column);
		this.test = test;
	}

	@Override
	public String generateCode() throws CodeException {
		String result = "";
		result += super.generateCode();

		String var = "_for_test__" + this.getId();
		String label_loop = "_for_label_loop__" + this.getId();
		String label_end = "_for_label_end__" + this.getId();

		result += tab() + "int " + var + ";" + NL;
		result += tab() + getFirst().generateCode() + NL;
		result += tab() + label_loop + ":{" + NL;
		incIndent();
			result += tab() + var + " = " + test.generateCode() + ";" + NL;
			result += tab() + "if (!" + var + ")" + NL;
			incIndent();
				result += tab() + "goto " + label_end + ";" + NL;
			decIndent();
			result += tab() + "{" + NL;
			incIndent();
				result += getThird().generateCode();
				result += getSecond().generateCode();
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
		if (this.getFirst() != null)
			this.getFirst().checkType();
		if (this.getSecond() != null)
			this.getSecond().checkType();
		if (this.getThird() != null)
			this.getThird().checkType();
		test.getType().assertType(this, TypeCode.BOOLEAN);
	}

	@Override
	public String toString() {
		return "for (... ; " + test + " ; ...)";
	}

}
