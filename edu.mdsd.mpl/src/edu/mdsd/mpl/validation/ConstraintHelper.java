package edu.mdsd.mpl.validation;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.mpl.FunctionalUnit;
import edu.mdsd.mpl.mpl.Operation;
import edu.mdsd.mpl.mpl.Variable;
import edu.mdsd.mpl.mpl.VariableDeclaration;

public abstract class ConstraintHelper {
	
	public static IStatus asStatus(IValidationContext ctx, boolean status, Object... args) {
		if(status) {
			return ctx.createSuccessStatus();
		}
		return ctx.createFailureStatus(args);
	}

	public static <T extends EObject> Optional<T> getParent(EObject initial, Class<T> clazz) {
		return parentsByClass(initial, clazz).findAny();
	}

	public static <T extends EObject> Stream<T> parentsByClass(EObject initial, Class<T> clazz) {
		return filter(parentClosure(initial), clazz);
	}

	public static Stream<EObject> parentClosure(EObject initial) {
		return Stream.iterate(initial, nonNull(), EObject::eContainer);
	}

	@SuppressWarnings("unchecked")
	public static <T> Stream<T> filter(Stream<? super T> s, Class<T> clazz) {
		return s.filter(clazz::isInstance).map(o -> (T) o);
	}

	public static Stream<Variable> getParameters(EObject variable) {
		return parentsByClass(variable, Operation.class).flatMap(o -> o.getParameters().stream());
	}

	public static Stream<Variable> getVariables(EObject variable) {
		return parentsByClass(variable, FunctionalUnit.class).flatMap(o -> o.getVariableDeclarations().stream())
				.map(VariableDeclaration::getVariable);
	}

	public static <T> Predicate<T> nonNull() {
		return t -> t != null;
	}
}
