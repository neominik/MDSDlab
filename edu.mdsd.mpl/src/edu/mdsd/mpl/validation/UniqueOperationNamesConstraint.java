package edu.mdsd.mpl.validation;

import static edu.mdsd.mpl.validation.ConstraintHelper.filter;
import static edu.mdsd.mpl.validation.ConstraintHelper.parentClosure;

import java.util.stream.Stream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.FunctionalUnit;
import edu.mdsd.mpl.mpl.MPLModel;
import edu.mdsd.mpl.mpl.Operation;

public class UniqueOperationNamesConstraint extends AbstractModelConstraint {

	@Override
	public IStatus validate(IValidationContext ctx) {
		Operation operation = (Operation) ctx.getTarget();
		String opName = operation.getName();

		Stream<? extends FunctionalUnit> program = getProgram(operation);
		Stream<? extends FunctionalUnit> operations = getOperations(operation);

		return Stream.concat(program, operations).filter(otherFU -> {
			String otherFUName = otherFU.getName();
			return operation != otherFU && opName.equals(otherFUName);
		}).findAny().map(v -> ctx.createFailureStatus(opName)).orElseGet(() -> ctx.createSuccessStatus());
	}

	public static Stream<? extends FunctionalUnit> getProgram(EObject element) {
		return filter(parentClosure(element), MPLModel.class).map(MPLModel::getProgram);
	}

	public static Stream<? extends FunctionalUnit> getOperations(EObject element) {
		return filter(parentClosure(element), MPLModel.class).flatMap(m -> m.getOperations().stream());
	}

}
