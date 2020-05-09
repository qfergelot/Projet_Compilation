package fr.ubordeaux.deptinfo.compilation.lea.abstract_syntax;

import fr.ubordeaux.deptinfo.compilation.lea.type.TypeCode;
import fr.ubordeaux.deptinfo.compilation.lea.type.TypeException;

public class StmIF extends StmBinary {

	private Expr test;

	public StmIF(Expr test, Stm succeed, Stm failed, int line, int column) {
		super(succeed, failed, line, column);
		this.test = test;
	}

	@Override
	public String generateCode() throws CodeException {
		String result = "";
		result += super.generateCode();

		String var = "_if_test__" + this.getId();
		String label_then = "_if_label_then__" + this.getId();
		String label_end = "_if_label_end__" + this.getId();

		result += tab() + "int " + var + " = " + test.generateCode() + ";" + NL;
		result += tab() + "if (" + var + ")" + NL;
		incIndent();
			result += tab() + "goto " + label_then + ";" + NL;
		decIndent();

		if(this.getRight() != null) {	// Partie "else" existante
			result += tab() + "{" + NL;
			incIndent();
				result += getRight().generateCode();
			decIndent();
			result += tab() + "}" + NL;
		}

		result += tab() + "goto " + label_end + ";" + NL;
		result += tab() + label_then + ":{" + NL;
		incIndent();
			result += getLeft().generateCode();
		decIndent();
		result += tab() + "}" + NL;
		result += tab() + label_end + ":{}" + NL;
		return result;
	}

	@Override
	public void checkType() throws TypeException {
		test.checkType();
		if (this.getLeft()!=null)
			getLeft().checkType();
		if (this.getRight()!=null)
			getRight().checkType();
		test.getType().assertType(this, TypeCode.BOOLEAN);
	}

	@Override
	public String toString() {
		return "if (" + test + ")";
	}

}
