package com.interpreter.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

public class Main {

  public static final Interpreter interpreter = new Interpreter();

  static boolean hadError = false;
  static boolean hadRuntimeError = false;

  public static Path importPath = null;

  static void error(int line, String message) {
    report(line, "", message);
  }

  static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end ", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }

  static void runtimeError(RuntimeError error) {
    System.err.println("[line " + error.token.line + "] " + error.getMessage());
    hadRuntimeError = true;
  }

  private static void report(int line, String where, String message) {
    System.err.println(
      "[line " + line + "] Error" + where + ": " + message
    );
    hadError = true;
  }

  private static void runFile(String path) throws IOException {
    importPath = Paths.get(path).getParent();
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));
    if (hadError) {
      try{
        throw new IOException();
      } catch (IOException e) {
    }
      //System.exit(0);
      //System.exit(65);
    }
    if (hadRuntimeError) {
        throw new IOException();
        //System.exit(0);
    }
  }

  public static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);
    for (;;) {
      hadError = false;
      System.out.print("> ");
      Scanner scanner = new Scanner(reader.readLine());
      List<Token> tokens = scanner.scanTokens();
      Parser parser = new Parser(tokens);
      Object syntax = parser.parseRepl();
      if(hadError) continue;
      if(syntax instanceof List) {
        interpreter.interpret((List<Stmt>)syntax);
      } else if (syntax instanceof Expr) {
        String result = interpreter.interpret((Expr)syntax);
        if (result != null) {
            System.out.println("= " + result);
        }
      }
    }
  }

  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();
    if (hadError) return;
    Resolver resolver = new Resolver(interpreter);
    resolver.resolve(statements);
    if (hadError) return;
    interpreter.interpret(statements);
  }

  public static void main(String[] args) throws IOException {
    if (args.length >= 1) {
      for (String file : args) {
        runFile(file);
      }
    } else {
      runPrompt();
    }
  }
}
