package edu.mdsd.mpl.validation;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.Block;
import edu.mdsd.mpl.mpl.Return;
import edu.mdsd.mpl.mpl.Statement;

public class CodeAfterReturnConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		Statement statement = (Statement) ctx.getTarget();
		Block block = (Block) statement.eContainer();
		List<Statement> statements = block.getStatements();
		int statementIndex = statements.indexOf(statement);
		int returnIndex = statements.stream().filter(s -> s.getForm() instanceof Return).findFirst().map(statements::indexOf)
				.orElse(Integer.MAX_VALUE);
		if (statementIndex > returnIndex) {
			return ctx.createFailureStatus();
		}
		return ctx.createSuccessStatus();
	}

}
