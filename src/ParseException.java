package src;

// Custom exception class for handling parse errors
public class ParseException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  // Constructor that accepts a custom error message
  public ParseException(String message) {
    super(message); // Pass the message to the superclass constructor
  }
}

// Custom exception class for handling standardization errors
class StandardizeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  // Constructor that accepts a custom error message
  public StandardizeException(String message) {
    super(message); // Pass the message to the superclass constructor
  }
}