package edu.mdsd.mpl.compiler;

import java.util.stream.Stream;

public abstract class StreamUtil {

	public static <T> Stream<T> stream(T w) {
		return Stream.of(w);
	}

	public static <T> Stream<T> stream(Stream<T> v, T w) {
		return Stream.concat(v, Stream.of(w));
	}

	public static <T> Stream<T> stream(Stream<T> v, Stream<T> w) {
		return Stream.concat(v, w);
	}

	public static <T> Stream<T> stream(T u, Stream<T> v, T w) {
		return Stream.concat(Stream.concat(Stream.of(u), v), Stream.of(w));
	}

	public static <T> Stream<T> stream(Stream<T> u, Stream<T> v, T w) {
		return Stream.concat(Stream.concat(u, v), Stream.of(w));
	}

}
