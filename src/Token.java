package src;

// Token class represents tokens passed from the scanner to the parser
public class Token {
  private TokenType type; // Type of the token
  private String value;   // Value of the token

  // Getter for the token type
  public TokenType getType() {
    return type;
  }

  // Getter for the token value
  public String getValue() {
    return value;
  }

  // Setter for the token type
  public void setType(TokenType type) {
    this.type = type;
  }

  // Setter for the token value
  public void setValue(String value) {
    this.value = value;
  }
}

// Enum representing the types of tokens recognized by the scanner
enum TokenType {
  IDENTIFIER,  // Identifiers (e.g., variable names)
  INTEGER,     // Integer literals
  STRING,      // String literals
  OPERATOR,    // Operators (e.g., +, -, *, /)
  DELETE,      // Delete token
  L_PAREN,     // Left parenthesis '('
  R_PAREN,     // Right parenthesis ')'
  SEMICOLON,   // Semicolon ';'
  COMMA,       // Comma ','
  RESERVED;    // Reserved keywords in RPAL
}