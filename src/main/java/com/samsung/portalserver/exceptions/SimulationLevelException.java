package com.samsung.portalserver.exceptions;

public class SimulationLevelException extends Exception {

    /**
     * Constructs a new exception with null as its detail message. The cause is not initialized, and
     * may subsequently be initialized by a call to {@link #initCause}.
     */
    public SimulationLevelException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.  The cause is not initialized,
     * and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the
     *                {@link #getMessage()} method.
     */
    public SimulationLevelException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause. Note that the detail
     * message associated with cause is not automatically incorporated in this exception's detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link
     *                #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()}
     *                method).  (A null value is permitted, and indicates that the cause is
     *                nonexistent or unknown.)
     * @since 1.4
     */
    public SimulationLevelException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of (cause==null ?
     * null : cause.toString()) (which typically contains the class and detail message of cause).
     * This constructor is useful for exceptions that are little more than wrappers for other
     * throwables (for example, {@link java.security.PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()}
     *              method).  (A null value is permitted, and indicates that the cause is
     *              nonexistent or unknown.)
     * @since 1.4
     */
    public SimulationLevelException(Throwable cause) {
        super(cause);
    }
}
