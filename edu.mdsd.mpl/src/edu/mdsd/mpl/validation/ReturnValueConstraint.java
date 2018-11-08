package edu.mdsd.mpl.validation;

import static edu.mdsd.mpl.validation.ConstraintHelper.getParent;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.Function;
import edu.mdsd.mpl.mpl.Return;

public class ReturnValueConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		Return ret = (Return) ctx.getTarget();
		return getParent(ret, Function.class).map(f -> {
			if (ret.getValue() != null) {
				return ctx.createSuccessStatus();
			}
			return ctx.createFailureStatus();
		}).orElseGet(ctx::createSuccessStatus);
	}

}
