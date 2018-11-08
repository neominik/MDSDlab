package edu.mdsd.mpl.validation;

import static edu.mdsd.mpl.validation.ConstraintHelper.filter;
import static edu.mdsd.mpl.validation.ConstraintHelper.parentClosure;

import java.util.stream.Stream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.FunctionalUnit;
import edu.mdsd.mpl.mpl.Operation;
import edu.mdsd.mpl.mpl.Variable;
import edu.mdsd.mpl.mpl.VariableDeclaration;

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

	private Stream<Variable> getParameters(EObject variable) {
		return filter(parentClosure(variable), Operation.class).flatMap(o -> o.getParameters().stream());
	}

	private Stream<Variable> getVariables(EObject variable) {
		return filter(parentClosure(variable), FunctionalUnit.class)
				.flatMap(o -> o.getVariableDeclarations().stream().map(VariableDeclaration::getVariable));
	}

}
