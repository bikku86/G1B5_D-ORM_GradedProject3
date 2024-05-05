package com.GreatLearning.D_ORM.GroupAssignment3.Exception;

public class TicketNotFoundException extends RuntimeException {

	// Default constructor
	public TicketNotFoundException() {
		super();
	}

	// Constructor that accepts a message
	public TicketNotFoundException(String message) {
		super(message);
	}

	// Constructor that accepts a message and a cause
	public TicketNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	// Constructor that accepts a cause
	public TicketNotFoundException(Throwable cause) {
		super(cause);
	}
}
