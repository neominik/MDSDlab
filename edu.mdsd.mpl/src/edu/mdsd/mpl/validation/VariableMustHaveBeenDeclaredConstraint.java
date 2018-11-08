package edu.mdsd.mpl.validation;

import static edu.mdsd.mpl.validation.ConstraintHelper.filter;
import static edu.mdsd.mpl.validation.ConstraintHelper.getParameters;
import static edu.mdsd.mpl.validation.ConstraintHelper.getVariables;
import static edu.mdsd.mpl.validation.ConstraintHelper.parentClosure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.Variable;
import edu.mdsd.mpl.mpl.VariableDeclaration;
import edu.mdsd.mpl.mpl.VariableReference;

public class VariableMustHaveBeenDeclaredConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		VariableReference varRef = (VariableReference) ctx.getTarget();
		String varName = varRef.getVariable().getName();

		Stream<EObject> parents = parentClosure(varRef);
		Optional<VariableDeclaration> isInsideDeclaration = filter(parents, VariableDeclaration.class).findAny();
		return isInsideDeclaration.flatMap((varDecl) -> {
			List<Variable> vars = getDeclaredVariableList(varDecl);
			int posVarRef = vars.indexOf(varRef.getVariable());
			int posVarDecl = vars.indexOf(varDecl.getVariable());
			if (posVarRef >= posVarDecl) {
				return Optional.of(ctx.createFailureStatus(varName));
			}
			return Optional.empty();
		}).orElseGet(() -> ctx.createSuccessStatus());
	}

	private List<Variable> getDeclaredVariableList(VariableDeclaration varDecl) {
		Stream<Variable> parameters = getParameters(varDecl);
		Stream<Variable> variables = getVariables(varDecl);
		return Stream.concat(parameters, variables).collect(Collectors.toList());
	}

}
