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

		// TODO
		//result += tab() + "_WHILE_" + this.getId() + ":" + NL;
		//this.incIndent();
		// result += tab() + "// Code WHILE ici..." + NL;
		// result += tab() + "printf(\"--- Manque WHILE...\\n\");" + NL;
		// this.decIndent();



		//******************************* TO TEST et TO VALIDER PAR LA STREET *******************************
		String var = "_WHILE__" + this.getId();
		String label_loop = "_while_label_loop__" + this.getId();
		String label_end = "_while_label_end__" + this.getId();
		result += tab() + "int " + var + " = " + test.generateCode() + ";" + NL;
		result += tab() + label_while + ":{" + NL;

		incIndent();
			result += tab() + "if (!" + var + ")" + NL;
			incIndent();
				result += tab() + "goto " + label_end + ";" + NL;
			decIndent();
			result += "{" + NL;
			incIndent();
				result += getRight().generateCode();
			decIndent();
			result += "}" + NL;
			result += tab() + "goto" + label_loop + ";" + NL
		decIndent();

		result += tab() + "}" + NL;
		result += tab() + label_end + ":{}" + NL;
		return result;

		//Le code edevrait donner ça:
		//  int var = test;
		//	label_while:{
		// 		if(!var) 
		//			goto label_end;
		// 		{
		// 			YYY
		//		}
		// 		goto label_while;
		//	}
		// 	label_end:{}
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
