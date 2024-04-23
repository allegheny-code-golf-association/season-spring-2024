package cfh.zirconium.cli;

import cfh.graph.Dot;
import cfh.zirconium.Compiler;
import cfh.zirconium.Program;
import cfh.zirconium.Compiler.CompileException;
import cfh.zirconium.Environment;
import cfh.zirconium.Environment.*;
import cfh.zirconium.Settings;
import cfh.zirconium.net.Pos;
import cfh.zirconium.net.Single;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Scanner;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class Main {

  private static Program program;
  private static Environment environment;

  private static void compile(String instructions) {
    try {
      program = new Compiler(environment).compile(new String(), instructions, new String());
    } catch (CompileException ex) {
      program = null;
      error(ex, "at %s", ex.pos);
    }
    while(!environment.halted()) {
      if(program.step()){
        break;
      }
    }
    System.out.println();
  }

  private static void error(Throwable ex, String format, Object... args) {
    String msg = String.format(format, args);
    System.err.println(msg);
    ex.printStackTrace();
    print("%n%s %s", ex.getClass().getSimpleName(), msg);
  }

  private static void print(String format, Object... args) {
    System.out.println(String.format(format, args));
  }

  public static void main(String[] args) throws IOException {

    Input input = new Input() {
      @Override
      public void reset() {
      }
      @Override
      public int readByte() {
        try {
          Scanner scanner = new Scanner(System.in);
          return (int)scanner.nextByte();
        } catch (Exception ex) {
          return -1;
        }
      }
      @Override
      public int readInteger() {
        try{
          Scanner scanner = new Scanner(System.in);
          return scanner.nextInt();
        } catch (Exception ex) {
          return -1;
        }
      }
    };

    Output output = new Output() {
      @Override
      public void reset() {

      }
      @Override
      public void write(int b) {
        System.out.print(Character.toString((char) (b & 0xFF)));
      }
      @Override
      public void write(String str) {
        System.out.print(str);
      }
    };

    Output error = new Output() {
      @Override
      public void reset() {
      }
      @Override
      public void write(int b) {
      }
      @Override
      public void write(String str) {
      }
    };

    Printer printer = new Printer() {
      @Override
      public void print(String format, Object... args) {
        //System.out.print(String.format(format, args));
      }
    };

    environment = new Environment(printer, input, output, error);

    // Load program
    byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
    String program = new String(bytes, Charset.defaultCharset());
    compile(program);
  }

}
