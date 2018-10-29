package edu.mdsd.mpl.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.Program;
import edu.mdsd.mpl.mpl.Variable;
import edu.mdsd.mpl.mpl.VariableDeclaration;

public class UniqueVariableNamesConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		Variable variable = (Variable) ctx.getTarget();
		String varName = variable.getName();

		VariableDeclaration variableDeclaration = (VariableDeclaration) variable.eContainer();
		Program program = (Program) variableDeclaration.eContainer();

		return program.getVariableDeclarations().stream().filter(otherVarDef -> {
			Variable otherVar = otherVarDef.getVariable();
			String otherVarName = otherVar.getName();
			return variable != otherVar && varName.equals(otherVarName);
		}).findAny().map(v -> ctx.createFailureStatus(varName)).orElseGet(() -> ctx.createSuccessStatus());
	}

}
