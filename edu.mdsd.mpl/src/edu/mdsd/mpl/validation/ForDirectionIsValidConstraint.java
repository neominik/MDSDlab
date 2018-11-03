package edu.mdsd.mpl.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.Expression;
import edu.mdsd.mpl.mpl.For;
import edu.mdsd.mpl.mpl.LiteralValue;

public class ForDirectionIsValidConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		For fore = (For) ctx.getTarget();
		Expression fromExp = fore.getFrom().getRightHandSide();
		Expression toExp = fore.getTo();

		if (fromExp instanceof LiteralValue && toExp instanceof LiteralValue) {
			int from = ((LiteralValue) fromExp).getRawValue();
			int to = ((LiteralValue) toExp).getRawValue();

			boolean sucess = fore.getDownwards() ? from >= to : from <= to;
			if (sucess) {
				return ctx.createSuccessStatus();
			} else {
				String order = fore.getDownwards() ? "incrementing" : "decrementing";
				String action = fore.getDownwards() ? "Omit" : "Use";
				return ctx.createFailureStatus(order, action);
			}
		}

		return ctx.createSuccessStatus();
	}

}
