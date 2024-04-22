package cfh.taxi.cli;

import java.net.URL;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.prefs.Preferences;
import java.io.File;
import java.nio.file.Files;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import cfh.taxi.Location;
import cfh.taxi.Node;
import cfh.taxi.Path;
import cfh.taxi.Program;
import cfh.taxi.RoadMap;
import cfh.taxi.Taxi;
import cfh.taxi.TaxiException;
import cfh.taxi.Path.Instruction;
import cfh.taxi.Program.InputOutput;
import cfh.taxi.Program.InputOutput.Level;

import static cfh.taxi.Program.InputOutput.Level.*;

public class Main {

  private static final String NORMAL_STYLE = "normal";
  private static final String ERROR_STYLE = "error";

  private static void compile(RoadMap map, String programText) throws TaxiException {
      Program prg;
      Scanner cliReader = new Scanner(System.in);
      Program.InputOutput inpout = new Program.InputOutput() {
        @Override
        public void print(String format, Object... args) {
          System.out.println(String.format(format, args));
        }

        @Override
        public void error(String format, Object... args) {
          //System.out.println(String.format(format, args));
        }

        @Override
        public boolean isLogging(Level level) {
          // Note: to turn on debug logging, set return value to true
          return true;
        }

        @Override
        public void log(Level level, String format, Object... args) {
          if(isLogging(null)) {
            System.out.println(String.format(format, args));
          }
        }

        @Override
        public String readLine() {
          System.out.print("Input passenger to wait at Post Office: ");
          return cliReader.nextLine();
        }

      };
      try {
          prg = new Program(map, inpout, programText);
          prg.run();
      } catch (ParseException ex) {
          ex.printStackTrace();
      }
  }

  public static void main(String[] args) throws IOException, URISyntaxException, TaxiException {
    InputStream stream = null;
    try {
      stream = Main.class.getClassLoader().getResourceAsStream(args[0]);
      if (stream == null) {
        System.err.println("[ERROR] Map not found.");
        return;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    byte[] bytes = Files.readAllBytes(Paths.get(args[1]));
    String program = new String(bytes, Charset.defaultCharset());
    compile(RoadMap.load(stream), program);
  }

}
