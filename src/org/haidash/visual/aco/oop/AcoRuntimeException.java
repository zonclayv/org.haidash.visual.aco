package org.haidash.visual.aco.oop;

import org.apache.log4j.Logger;

public class AcoRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -4160943403013773651L;

	private static final Logger LOGGER = Logger.getLogger(AcoRuntimeException.class);

	public AcoRuntimeException() {
		super();
		LOGGER.error("Aco runtime exception");
	}

	public AcoRuntimeException(final String message) {
		super(message);
		LOGGER.error(message);
	}

	public AcoRuntimeException(final String message, final Throwable cause) {
		super(message, cause);
		LOGGER.error(message, cause);
	}

	public AcoRuntimeException(final Throwable cause) {
		super(cause);
		LOGGER.error(cause.getMessage(), cause);
	}
}
