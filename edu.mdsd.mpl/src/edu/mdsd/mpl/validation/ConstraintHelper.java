package edu.mdsd.mpl.validation;

import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

public abstract class ConstraintHelper {

	public static Stream<EObject> parentClosure(EObject initial) {
		return Stream.iterate(initial, eo -> eo.eContainer() != null, EObject::eContainer);
	}

	@SuppressWarnings("unchecked")
	public static <T> Stream<T> filter(Stream<? super T> s, Class<T> clazz) {
		return s.filter(clazz::isInstance).map(o -> (T) o);
	}

}
