package edu.mdsd.mpl.compiler;

import java.util.stream.Stream;

public abstract class StreamUtil {

	public static <T> Stream<T> stream(T w) {
		return Stream.of(w);
	}

	public static <T> Stream<T> stream(Stream<T> v, T w) {
		return Stream.concat(v, stream(w));
	}

	public static <T> Stream<T> stream(T v, Stream<T> w) {
		return Stream.concat(stream(v), w);
	}

	public static <T> Stream<T> stream(Stream<T> v, Stream<T> w) {
		return Stream.concat(v, w);
	}

	public static <T> Stream<T> stream(T u, Stream<T> v, T w) {
		return Stream.concat(Stream.concat(stream(u), v), stream(w));
	}

	public static <T> Stream<T> stream(Stream<T> u, Stream<T> v, T w) {
		return Stream.concat(Stream.concat(u, v), stream(w));
	}

	public static <T> Stream<T> stream(Stream<T> u, Stream<T> v, Stream<T> w) {
		return stream(u, stream(v, w));
	}

	public static <T> Stream<T> stream(T a, Stream<T> b, T c, T d, Stream<T> e) {
		return Stream.concat(Stream.concat(Stream.concat(stream(a), b), Stream.of(c, d)), e);
	}

}
