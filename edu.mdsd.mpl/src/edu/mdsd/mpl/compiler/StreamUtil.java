package edu.mdsd.mpl.compiler;

import java.util.Arrays;
import java.util.stream.Stream;

public abstract class StreamUtil {

	public static <T> Stream<T> stream(T w) {
		return Stream.of(w);
	}

	@SafeVarargs
	public static <T> Stream<T> stream(Stream<T>... streams) {
		return Arrays.stream(streams).flatMap(s -> s);
	}

}
