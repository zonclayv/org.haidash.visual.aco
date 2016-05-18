package org.haidash.visual.aco.algorithm.util;

public class Pair<T, P> {

	public T first;
	public P second;

	public Pair(final Pair<T, P> pair) {
		this.first = pair.first;
		this.second = pair.second;
	}

	public Pair(final T first, final P second) {
		this.first = first;
		this.second = second;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		Pair<T, P> other = (Pair<T, P>) obj;

		if (first == null) {
			if (other.first != null) {
				return false;
			}
		} else if (!first.equals(other.first)) {
			return false;
		}

		if (second == null) {
			if (other.second != null) {
				return false;
			}
		} else if (!second.equals(other.second)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = (prime * result) + ((first == null) ? 0 : first.hashCode());
		result = (prime * result) + ((second == null) ? 0 : second.hashCode());

		return result;
	}

}
