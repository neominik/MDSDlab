package edu.mdsd.mpl.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.ExpressionStatement;
import edu.mdsd.mpl.mpl.OperationExpression;

public class NoSideEffectsConstraints extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		ExpressionStatement statement = (ExpressionStatement) ctx.getTarget();
		if (statement.getExpression() instanceof OperationExpression) {
			return ctx.createSuccessStatus();
		}
		return ctx.createFailureStatus();
	}

}
