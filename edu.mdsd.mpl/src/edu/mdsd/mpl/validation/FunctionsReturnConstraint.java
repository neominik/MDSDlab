package edu.mdsd.mpl.validation;

import static edu.mdsd.mpl.validation.ConstraintHelper.asStatus;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.Block;
import edu.mdsd.mpl.mpl.Form;
import edu.mdsd.mpl.mpl.Function;
import edu.mdsd.mpl.mpl.If;
import edu.mdsd.mpl.mpl.Loop;
import edu.mdsd.mpl.mpl.Return;
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
		return statements.stream().anyMatch(this::returns);
	}

	private boolean returns(Statement statement) {
		Form form = statement.getForm();
		if (form instanceof Return) {
			return true;
		}
		if (form instanceof Loop) {
			return loopReturns((Loop) form);
		}
		if (form instanceof If) {
			return ifReturns((If) form);
		}
		return false;
	}

	private boolean loopReturns(Loop loop) {
		if (loop.getBody() != null) {
			return loop.getBody().getStatements().stream().anyMatch(this::returns);
		}
		return false;
	}

	private boolean ifReturns(If ifForm) {
		if (ifForm.getThen() != null && ifForm.getElse() != null) {
			return ifForm.getThen().getStatements().stream().anyMatch(this::returns)
					&& ifForm.getElse().getStatements().stream().anyMatch(this::returns);
		}
		return false;
	}
}
