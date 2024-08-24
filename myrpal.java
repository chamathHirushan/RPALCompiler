
import java.io.IOException;

import src.AST;
import src.CSEM;
import src.ParseException;
import src.Parser;
import src.Scanner;

// Main class for RPAL interpreter
public class myrpal {

  // File name to process
  public static String fileName;

  // Main method
  public static void main(String[] args) {
    // Flags for AST and ST options
    boolean astFlag = false;
    boolean stFlag = false;
    fileName = "";
    AST ast = null;

    // Parse command line arguments
    for (String cmdOption : args) {
      if (cmdOption.equals("-ast"))
        astFlag = true;
      else if (cmdOption.equals("-st"))
        stFlag = true;
      else
        fileName = cmdOption;
    }

    // If no flags are set, only print the result
    if (!astFlag && !stFlag) {
      ast = buildAST(fileName, true);
      ast.standardize();
      evaluateST(ast);
      return;
    }

    // If AST flag is set, print AST and result
    if (astFlag) {
      if (fileName.isEmpty())
        throw new ParseException("Input a relevant file.");
      ast = buildAST(fileName, true);
      printAST(ast);
      ast.standardize();
      evaluateST(ast);
    }

    // If ST flag is set, print standardized AST and result
    if (stFlag) {
      if (fileName.isEmpty())
        throw new ParseException("Input a relevant file.");
      ast = buildAST(fileName, true);
      ast.standardize();
      printAST(ast);
      evaluateST(ast);
    }
  }

  // Evaluate the standardized AST using CSEM and print the result
  private static void evaluateST(AST ast) {
    CSEM csem = new CSEM(ast);
    csem.evaluateProgram();
    System.out.println();
  }

  // Build the AST from the given file
  private static AST buildAST(String fileName, boolean printOutput) {
    AST ast = null;
    try {
      Scanner scanner = new Scanner(fileName);
      Parser parser = new Parser(scanner);
      ast = parser.buildAST();
    } catch (IOException e) {
      throw new ParseException("ERROR: Could not read from file: " + fileName);
    }
    return ast;
  }

  // Print the AST
  private static void printAST(AST ast) {
    ast.print();
  }

}
