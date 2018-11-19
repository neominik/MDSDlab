package edu.mdsd.mpl.validation;

import static edu.mdsd.mpl.validation.ConstraintHelper.asStatus;
import static edu.mdsd.mpl.validation.ConstraintHelper.returns;

import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.Block;
import edu.mdsd.mpl.mpl.Statement;

public class CodeAfterReturnConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		Statement s = (Statement) ctx.getTarget();
		boolean reachable = getPrecedingStatement(s).map(prev -> !returns(prev)).orElse(true);
		return asStatus(ctx, reachable);
	}

	private Optional<Statement> getPrecedingStatement(Statement statement) {
		Block block = (Block) statement.eContainer();
		List<Statement> statements = block.getStatements();
		int statementIndex = statements.indexOf(statement);
		if (statementIndex > 0) {
			return Optional.of(statements.get(statementIndex - 1));
		}
		return Optional.empty();
	}

}
