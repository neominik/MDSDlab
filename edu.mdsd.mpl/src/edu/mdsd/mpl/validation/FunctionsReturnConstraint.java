package edu.mdsd.mpl.validation;

import static edu.mdsd.mpl.validation.ConstraintHelper.asStatus;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.Block;
import edu.mdsd.mpl.mpl.Function;
import edu.mdsd.mpl.mpl.Statement;

public class FunctionsReturnConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		Function function = (Function) ctx.getTarget();
		Block start = function.getBody();
		if (start == null) {
			return ctx.createFailureStatus();
		}
		return asStatus(ctx, returns(start.getStatements()));
	}

	private boolean returns(List<Statement> statements) {
		return statements.stream().anyMatch(ConstraintHelper::returns);
	}
}
