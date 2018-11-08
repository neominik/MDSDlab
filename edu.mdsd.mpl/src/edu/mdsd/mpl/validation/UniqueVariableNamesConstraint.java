package edu.mdsd.mpl.validation;

import static edu.mdsd.mpl.validation.ConstraintHelper.getParameters;
import static edu.mdsd.mpl.validation.ConstraintHelper.getVariables;

import java.util.stream.Stream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.Variable;

public class UniqueVariableNamesConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		Variable variable = (Variable) ctx.getTarget();
		String varName = variable.getName();

		Stream<Variable> parameters = getParameters(variable);
		Stream<Variable> variables = getVariables(variable);

		return Stream.concat(parameters, variables).filter(otherVar -> {
			String otherVarName = otherVar.getName();
			return variable != otherVar && varName.equals(otherVarName);
		}).findAny().map(v -> ctx.createFailureStatus(varName)).orElseGet(() -> ctx.createSuccessStatus());
	}

}
