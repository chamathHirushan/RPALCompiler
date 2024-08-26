# RPAL Interpreter

## Abstract

This project focuses on creating an interpreter for the RPAL (Right-reference Pedagogic Algorithmic Language) programming language. The interpreter includes several critical components:

- **Lexical Analyzer**: Processes the input code and converts it into tokens.
- **Parser**: Analyzes the token stream and generates an Abstract Syntax Tree (AST).
- **Abstract Syntax Tree (AST)**: Represents the hierarchical structure of the source code.
- **Standardized Tree (ST)**: Converts the AST into a standardized format.
- **Control Stack Environment (CSE) Machine**: Executes RPAL programs by processing the ST.

The primary goal of this project is to develop a fully functional RPAL interpreter capable of parsing and executing RPAL code, providing a deeper understanding of the language's structure and semantics.

## Introduction

RPAL is an educational programming language designed to explore fundamental concepts in programming language design and implementation. This interpreter is created to enhance understanding of parsing techniques, abstract syntax trees, and interpretation algorithms. The implementation is done in Java.

## Key Components

- **Lexical Analyzer**: Tokenizes the input RPAL code.
- **Parser**: Builds the Abstract Syntax Tree (AST) from the tokens.
- **Abstract Syntax Tree (AST)**: A tree representation of the program's structure.
- **Standardized Tree (ST)**: Transforms the AST into a standardized format for execution.
- **Control Stack Environment (CSE) Machine**: Executes the standardized tree by simulating the runtime environment.

## Project Goals

- To implement a working RPAL interpreter in Java.
- To provide insights into the structure and semantics of the RPAL language.
- To enhance understanding of the principles of parsing, AST generation, and program execution.

## Technologies Used

- **Java**: The primary programming language used for building the interpreter.

## Instructions

The project is organized as follows:

- **Main Directory**: This is where the `rpal20.java` file is located.

### Included Files and Folders

- **bin**: Contains the compiled output files.
- **test_cases**: Directory with RPAL files to be tested using the interpreter.
- **src**: The source folder containing the source code.
- **myrpal.java**: The main file to run the interpreter.
- **makefile**: Contains instructions for building the project.

### Setup and Execution

1. Ensure that the main directory is set as the current working directory in your command prompt, terminal, or VSCode.

2. To compile the program, use the following command:
   ```sh
   make
or
 ```sh
  make all
```
3. To run a test program use
```sh
  make run file=(file_name)
  java -cp bin myrpal test_cases/(file_name)’
```
4. To print the Abstract Syntax Tree(AST)
   ```sh
   make ast file=(file_name)
   java -cp bin myrpal test_cases/(file_name) -ast
   ```
5. To print the Standardize Tree(ST)
   ```sh
   make st file=(file_name)
   java -cp bin myrpal test_cases/(file_name) -st
   ```
