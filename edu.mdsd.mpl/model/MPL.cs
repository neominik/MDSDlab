SYNTAXDEF mpl
FOR <http://mdsd.edu/mpl/1.0>
START Program

OPTIONS {
	reloadGeneratorModel = "true";
	defaultTokenName = "IDENTIFIER_TOKEN";
}

TOKENS {
	DEFINE IDENTIFIER_TOKEN $('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*$;
	DEFINE INTEGER_TOKEN $('-')?('0'..'9')+$;
	
	DEFINE SL_COMMENT $'//'(~('\n'|'\r'|'\uffff'))*$;
	DEFINE ML_COMMENT $'/*'.*'*/'$;
}

TOKENSTYLES {
	"IDENTIFIER_TOKEN" COLOR #6A3E3E;
	"INTEGER_TOKEN" COLOR #0000C0;
	"SL_COMMENT", "ML_COMMENT" COLOR #3F7F5F;
}

RULES {
	Program ::= "Program" #1 name[] (!1 "Variables" !1 variableDeclarations ("," #1 variableDeclarations)+ ".")? (!1 statements)* "End" ".";
	
	VariableDeclaration ::= variable;
	Variable ::= name[];
	
	@Operator(type="primitive", weight="5", superclass="Expression")
	VariableReference ::= variable[];
	
	@Operator(type="primitive", weight="5", superclass="Expression")
	LiteralValue ::= rawValue[INTEGER_TOKEN];
	
	@Operator(type="binary_left_associative", weight="1", superclass="Expression")
	AddExpression ::= operand1 #1 "+" #1 operand2;
	
	Assignment ::= leftHandSide #1 ":=" #1 rightHandSide ".";
	
	ExpressionStatement ::= expression ".";
}
