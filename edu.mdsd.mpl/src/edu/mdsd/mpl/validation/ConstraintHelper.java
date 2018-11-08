package edu.mdsd.mpl.validation;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

import edu.mdsd.mpl.mpl.FunctionalUnit;
import edu.mdsd.mpl.mpl.Operation;
import edu.mdsd.mpl.mpl.Variable;
import edu.mdsd.mpl.mpl.VariableDeclaration;

public abstract class ConstraintHelper {

	public static Stream<EObject> parentClosure(EObject initial) {
		return Stream.iterate(initial, nonNull(), EObject::eContainer);
	}

	@SuppressWarnings("unchecked")
	public static <T> Stream<T> filter(Stream<? super T> s, Class<T> clazz) {
		return s.filter(clazz::isInstance).map(o -> (T) o);
	}

	public static Stream<Variable> getParameters(EObject variable) {
		return filter(parentClosure(variable), Operation.class).flatMap(o -> o.getParameters().stream());
	}

	public static Stream<Variable> getVariables(EObject variable) {
		return filter(parentClosure(variable), FunctionalUnit.class).flatMap(o -> o.getVariableDeclarations().stream())
				.map(VariableDeclaration::getVariable);
	}

	public static <T> Predicate<T> nonNull() {
		return t -> t != null;
	}
}
