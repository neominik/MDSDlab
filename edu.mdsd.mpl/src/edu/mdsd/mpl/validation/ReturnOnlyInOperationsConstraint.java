package edu.mdsd.mpl.validation;

import static edu.mdsd.mpl.validation.ConstraintHelper.filter;
import static edu.mdsd.mpl.validation.ConstraintHelper.parentClosure;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.Operation;
import edu.mdsd.mpl.mpl.Return;

public class ReturnOnlyInOperationsConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		Return ret = (Return) ctx.getTarget();
		return filter(parentClosure(ret), Operation.class).findAny().map(o -> ctx.createSuccessStatus())
				.orElseGet(ctx::createFailureStatus);
	}

}
