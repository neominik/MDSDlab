SYNTAXDEF mpl
FOR <http://mdsd.edu/mpl/1.0>
START Program

OPTIONS {
	reloadGeneratorModel = "true";
	defaultTokenName = "IDENTIFIER_TOKEN";
	usePredefinedTokens = "false";
}

TOKENS {
	DEFINE IDENTIFIER_TOKEN $('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*$;
	DEFINE INTEGER_TOKEN $('-')?('0'..'9')+$;
	
	DEFINE SL_COMMENT $'//'(~('\n'|'\r'|'\uffff'))*$;
	DEFINE ML_COMMENT $'/*'.*'*/'$;
	
	DEFINE LINEBREAK $('\r\n'|'\r'|'\n')$;
	DEFINE WHITESPACE $(' '|'\t'|'\f')$;
}

TOKENSTYLES {
	"IDENTIFIER_TOKEN" COLOR #6A3E3E;
	"INTEGER_TOKEN" COLOR #0000C0;
	"SL_COMMENT", "ML_COMMENT" COLOR #3F7F5F;
}

RULES {
	Program ::= "Program" #1 name[] (!1 "Variables" !1 variableDeclarations ("," #1 variableDeclarations)+ ".")? (body)? "End" ".";
	
	VariableDeclaration ::= variable (":=" initialValue)?;
	Variable ::= name[];
	
	Block ::= (!1 statements)*;
	Statement ::= form ".";
	
	@Operator(type="primitive", weight="5", superclass="Expression")
	VariableReference ::= variable[];
	
	@Operator(type="primitive", weight="5", superclass="Expression")
	LiteralValue ::= rawValue[INTEGER_TOKEN];
	
	@Operator(type="binary_left_associative", weight="1", superclass="Expression")
	AddExpression ::= operand1 #1 "+" #1 operand2;
	
	@Operator(type="binary_left_associative", weight="1", superclass="Expression")
	SubtractExpression ::= operand1 #1 "-" #1 operand2;
	
	@Operator(type="binary_left_associative", weight="2", superclass="Expression")
	MultiplyExpression ::= operand1 #1 "*" #1 operand2;
	
	@Operator(type="binary_left_associative", weight="2", superclass="Expression")
	DivisionExpression ::= operand1 #1 "/" #1 operand2;
	
	@Operator(type="unary_prefix", weight="4", superclass="Expression")
	NegateExpression ::= "-" operand;
	
	@Operator(type="primitive", weight="5", superclass="Expression")
	ParenExpression ::= "(" operand ")";
	
	Assignment ::= leftHandSide #1 ":=" #1 rightHandSide;
	
	ExpressionStatement ::= expression;
	
	If ::= "If" condition #1 "Then" !1 then ("Else" !1 else)? "End";
	
	While ::= "While" condition "Do" !1 body "End";
	
	For ::= "For" from downwards["Down" : ""] "To" to "Do" body "End";
		
	Comparison ::= "(" leftHandSide operator rightHandSide ")";
	EQ ::= "=";
	NE ::= "<>";
	GT ::= ">";
	LT ::= "<";
	GE ::= ">=";
	LE ::= "<=";
}
