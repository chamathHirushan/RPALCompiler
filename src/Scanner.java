package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

// Combination of lexer and screener
public class Scanner {
  private BufferedReader buffer; // Buffer to read the input file
  private String extraCharRead;  // Stores an extra character read for the next token
  private final List<String> reservedIdentifiers = Arrays
      .asList(new String[] { "let", "in", "within", "fn", "where", "aug", "or",
          "not", "gr", "ge", "ls", "le", "eq", "ne", "true",
          "false", "nil", "dummy", "rec", "and" }); // List of reserved keywords in RPAL

  // Constructor to initialize the buffer with the input file
  public Scanner(String inputFile) throws IOException {
    buffer = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputFile))));
  }

  // Reads the next token from the input
  public Token readNextToken() {
    Token nextToken = null;
    String nextChar;

    // Use the extra character read from the previous call if available
    if (extraCharRead != null) {
      nextChar = extraCharRead;
      extraCharRead = null;
    } else
      nextChar = readNextChar();

    // Build the token if a character was read
    if (nextChar != null)
      nextToken = buildToken(nextChar);
    return nextToken;
  }

  // Reads the next character from the input buffer
  private String readNextChar() {
    String nextChar = null;
    try {
      int c = buffer.read();
      if (c != -1) {
        nextChar = Character.toString((char) c);
      } else
        buffer.close();
    } catch (IOException e) {
      // Handle exception silently
    }
    return nextChar;
  }

  // Determines the type of the token based on the current character and builds it
  private Token buildToken(String currentChar) {
    Token nextToken = null;
    if (LexRegex.LetterPattern.matcher(currentChar).matches()) {
      nextToken = buildIdentifierToken(currentChar);
    } else if (LexRegex.DigitPattern.matcher(currentChar).matches()) {
      nextToken = buildIntegerToken(currentChar);
    } else if (LexRegex.OpSymbolPattern.matcher(currentChar).matches()) {
      nextToken = buildOperatorToken(currentChar);
    } else if (currentChar.equals("\'")) {
      nextToken = buildStringToken(currentChar);
    } else if (LexRegex.SpacePattern.matcher(currentChar).matches()) {
      nextToken = buildSpaceToken(currentChar);
    } else if (LexRegex.PunctuationPattern.matcher(currentChar).matches()) {
      nextToken = buildPunctuationPattern(currentChar);
    }
    return nextToken;
  }

  // Builds an identifier token
  private Token buildIdentifierToken(String currentChar) {
    Token identifierToken = new Token();
    identifierToken.setType(TokenType.IDENTIFIER);
    StringBuilder sBuilder = new StringBuilder(currentChar);

    String nextChar = readNextChar();
    while (nextChar != null) {
      if (LexRegex.IdentifierPattern.matcher(nextChar).matches()) {
        sBuilder.append(nextChar);
        nextChar = readNextChar();
      } else {
        extraCharRead = nextChar;
        break;
      }
    }

    String value = sBuilder.toString();
    if (reservedIdentifiers.contains(value))
      identifierToken.setType(TokenType.RESERVED);

    identifierToken.setValue(value);
    return identifierToken;
  }

  // Builds an integer token
  private Token buildIntegerToken(String currentChar) {
    Token integerToken = new Token();
    integerToken.setType(TokenType.INTEGER);
    StringBuilder sBuilder = new StringBuilder(currentChar);

    String nextChar = readNextChar();
    while (nextChar != null) {
      if (LexRegex.DigitPattern.matcher(nextChar).matches()) {
        sBuilder.append(nextChar);
        nextChar = readNextChar();
      } else {
        extraCharRead = nextChar;
        break;
      }
    }

    integerToken.setValue(sBuilder.toString());
    return integerToken;
  }

  // Builds an operator token
  private Token buildOperatorToken(String currentChar) {
    Token opSymbolToken = new Token();
    opSymbolToken.setType(TokenType.OPERATOR);
    StringBuilder sBuilder = new StringBuilder(currentChar);

    String nextChar = readNextChar();

    // Handle comment tokens (starting with "//")
    if (currentChar.equals("/") && nextChar.equals("/"))
      return buildCommentToken(currentChar + nextChar);

    while (nextChar != null) {
      if (LexRegex.OpSymbolPattern.matcher(nextChar).matches()) {
        sBuilder.append(nextChar);
        nextChar = readNextChar();
      } else {
        extraCharRead = nextChar;
        break;
      }
    }

    opSymbolToken.setValue(sBuilder.toString());
    return opSymbolToken;
  }

  // Builds a string token
  private Token buildStringToken(String currentChar) {
    Token stringToken = new Token();
    stringToken.setType(TokenType.STRING);
    StringBuilder sBuilder = new StringBuilder("");

    String nextChar = readNextChar();
    while (nextChar != null) {
      if (nextChar.equals("\'")) {
        stringToken.setValue(sBuilder.toString());
        return stringToken;
      } else if (LexRegex.StringPattern.matcher(nextChar).matches()) {
        sBuilder.append(nextChar);
        nextChar = readNextChar();
      }
    }

    return null; // Return null if string is not properly terminated
  }

  // Builds a space token (which will be deleted)
  private Token buildSpaceToken(String currentChar) {
    Token deleteToken = new Token();
    deleteToken.setType(TokenType.DELETE);
    StringBuilder sBuilder = new StringBuilder(currentChar);

    String nextChar = readNextChar();
    while (nextChar != null) {
      if (LexRegex.SpacePattern.matcher(nextChar).matches()) {
        sBuilder.append(nextChar);
        nextChar = readNextChar();
      } else {
        extraCharRead = nextChar;
        break;
      }
    }

    deleteToken.setValue(sBuilder.toString());
    return deleteToken;
  }

  // Builds a comment token
  private Token buildCommentToken(String currentChar) {
    Token commentToken = new Token();
    commentToken.setType(TokenType.DELETE);
    StringBuilder sBuilder = new StringBuilder(currentChar);

    String nextChar = readNextChar();
    while (nextChar != null) {
      if (LexRegex.CommentPattern.matcher(nextChar).matches()) {
        sBuilder.append(nextChar);
        nextChar = readNextChar();
      } else if (nextChar.equals("\n"))
        break;
    }

    commentToken.setValue(sBuilder.toString());
    return commentToken;
  }

  // Builds a punctuation token
  private Token buildPunctuationPattern(String currentChar) {
    Token punctuationToken = new Token();
    punctuationToken.setValue(currentChar);
    if (currentChar.equals("("))
      punctuationToken.setType(TokenType.L_PAREN);
    else if (currentChar.equals(")"))
      punctuationToken.setType(TokenType.R_PAREN);
    else if (currentChar.equals(";"))
      punctuationToken.setType(TokenType.SEMICOLON);
    else if (currentChar.equals(","))
      punctuationToken.setType(TokenType.COMMA);

    return punctuationToken;
  }
}

// Used by the scanner for tokenizing
class LexRegex {
  // Regex strings for different token types
  private static final String letterRegex = "a-zA-Z";
  private static final String digitRegex = "\\d";
  private static final String opSymbolRegexString = "+-/~:=|!#%_{}\"*<>.&$^\\[\\]?@";
  private static final String opSymbolToEscapeString = "([*<>.&$^?])";
  private static final String spaceRegex = "[\\s\\t\\n]";
  private static final String puncRegex = "();,";
  public static final String opSymbolRegex = "[" + escapeMetaChars(opSymbolRegexString, opSymbolToEscapeString) + "]";

  // Patterns for matching different token types
  public static final Pattern LetterPattern = Pattern.compile("[" + letterRegex + "]");
  public static final Pattern DigitPattern = Pattern.compile(digitRegex);
  public static final Pattern OpSymbolPattern = Pattern.compile(opSymbolRegex);
  public static final Pattern IdentifierPattern = Pattern.compile("[" + letterRegex + digitRegex + "_]");
  public static final Pattern PunctuationPattern = Pattern.compile("[" + puncRegex + "]");
  public static final Pattern StringPattern = Pattern.compile("[ \\t\\n\\\\" + puncRegex
      + letterRegex + digitRegex + escapeMetaChars(opSymbolRegexString, opSymbolToEscapeString) + "]");
  public static final Pattern SpacePattern = Pattern.compile(spaceRegex);
  public static final Pattern CommentPattern = Pattern.compile("[ \\t\\'\\\\ \\r" + puncRegex
      + letterRegex + digitRegex + escapeMetaChars(opSymbolRegexString, opSymbolToEscapeString) + "]");

  // Escapes meta characters in the regex string
  private static String escapeMetaChars(String inputString, String charsToEscape) {
    return inputString.replaceAll(charsToEscape, "\\\\\\\\$1");
  }
}
