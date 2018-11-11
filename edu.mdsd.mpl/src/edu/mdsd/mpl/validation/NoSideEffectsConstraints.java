package edu.mdsd.mpl.validation;

import static edu.mdsd.mpl.validation.ConstraintHelper.asStatus;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.ExpressionStatement;
import edu.mdsd.mpl.mpl.OperationExpression;

public class NoSideEffectsConstraints extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		ExpressionStatement statement = (ExpressionStatement) ctx.getTarget();
		return asStatus(ctx, statement.getExpression() instanceof OperationExpression);
	}

}
