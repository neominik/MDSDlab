package edu.mdsd.mpl.validation;

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
		if (callerArity != calleeArity) {
			return ctx.createFailureStatus(callerArity, target.getOperation().getName(), calleeArity);
		}
		return ctx.createSuccessStatus();
	}

}
