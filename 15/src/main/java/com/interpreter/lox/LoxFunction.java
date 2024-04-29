package com.interpreter.lox;

import java.util.List;

class LoxFunction implements LoxCallable {

  @Override
  public int arity() {
    return declaration.parameters.size();
  }

  private final Environment closure;
  private final String name;
  private final Expr.Function declaration;
  private final boolean isInitializer;

  LoxFunction(String name, Expr.Function declaration, Environment closure) {
    this.name = name;
    this.closure = closure;
    this.declaration = declaration;
    this.isInitializer = false;
  }

  @Override
  public String toString() {
    if (name == null) return "<fn>";
    return "<fn " + name + ">";
  }

  LoxFunction(Expr.Function declaration, Environment closure,
              boolean isInitializer) {
    this.name = null;
    this.isInitializer = isInitializer;
    this.closure = closure;
    this.declaration = declaration;
  }

  LoxFunction bind(LoxInstance instance) {
    Environment environment = new Environment(closure);
    environment.define("this", instance);
    return new LoxFunction(declaration, environment,
                           isInitializer);
  }

  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
    Environment environment = new Environment(closure);
    // STUDENT: Once again, the IF statement
    if (declaration.parameters != null) {
      for (int i = 0; i < declaration.parameters.size(); i++) {
        environment.define(declaration.parameters.get(i).lexeme,
          arguments.get(i));
      }
    }
    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      if (isInitializer) return closure.getAt(0, "this");
      return returnValue.value;
    }

    if (isInitializer) return closure.getAt(0, "this");
    return null;
  }

  // STUDENT: This function should be derived by the student,
  //          though it's possible to do this in another place,
  //          way.
  public boolean isGetter() {
    return declaration.parameters == null;
  }

}
