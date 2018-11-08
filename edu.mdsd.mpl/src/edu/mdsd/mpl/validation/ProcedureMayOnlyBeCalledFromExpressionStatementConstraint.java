package edu.mdsd.mpl.validation;

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
		if (!(expr.getOperation() instanceof Procedure) || expr.eContainer() instanceof ExpressionStatement) {
			return ctx.createSuccessStatus();
		}
		return ctx.createFailureStatus();
	}

}
