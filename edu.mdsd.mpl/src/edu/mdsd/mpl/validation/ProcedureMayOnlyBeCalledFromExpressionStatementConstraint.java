package edu.mdsd.mpl.validation;

import static edu.mdsd.mpl.validation.ConstraintHelper.asStatus;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.ExpressionStatement;
import edu.mdsd.mpl.mpl.OperationExpression;
import edu.mdsd.mpl.mpl.Procedure;

public class ProcedureMayOnlyBeCalledFromExpressionStatementConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		OperationExpression expr = (OperationExpression) ctx.getTarget();
		boolean success = !(expr.getOperation() instanceof Procedure)
				|| expr.eContainer() instanceof ExpressionStatement;
		return asStatus(ctx, success);
	}

}
