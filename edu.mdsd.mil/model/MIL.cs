SYNTAXDEF mil
FOR <http://mdsd.edu/mil/1.0>
START MILModel

OPTIONS {
	reloadGeneratorModel = "true";
	defaultTokenName = "IDENTIFIER_TOKEN";
	usePredefinedTokens = "false";
	overrideLaunchConfigurationDelegate = "false";
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
	MILModel ::= (instructions !0)*;
	
	LoadInstruction ::= "lod" #1 value;
	StoreInstruction ::= "sto" #1 registerReference?;
	
	AddInstruction ::= "add";
	
	ConstantInteger ::= rawValue[INTEGER_TOKEN];
	RegisterReference ::= address[];	
}
