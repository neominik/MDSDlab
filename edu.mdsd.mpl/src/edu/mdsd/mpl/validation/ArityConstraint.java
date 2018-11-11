package edu.mdsd.mpl.validation;

import static edu.mdsd.mpl.validation.ConstraintHelper.asStatus;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.OperationExpression;

public class ArityConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		OperationExpression target = (OperationExpression) ctx.getTarget();
		int callerArity = target.getParameterValues().size();
		int calleeArity = target.getOperation().getParameters().size();
		return asStatus(ctx, callerArity == calleeArity, callerArity, target.getOperation().getName(), calleeArity);
	}

}
