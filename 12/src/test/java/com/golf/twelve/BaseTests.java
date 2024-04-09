package com.golf.twelve;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseTests {

  /*
    Word to the accepted answer at SO, they're a real one.
    https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println
  */

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @Rule
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  @BeforeAll
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  public static String checksum(String input) throws IOException, NoSuchAlgorithmException {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] digest = md.digest(input.getBytes());
      BigInteger num = new BigInteger(1, digest);
      String hash = num.toString(16);
      return hash;
    } catch (Exception err) {
    }
    return null;
  }

  static Stream<Arguments> testProgramOutput() throws Exception {
    URL resource = BaseTests.class.getClassLoader().getResource("test.cases");
    File file = Paths.get(resource.toURI()).toFile();
    String absPath = file.getAbsolutePath();
    List<String> cases = new ArrayList<String>();
    BufferedReader reader = new BufferedReader(new FileReader(absPath));
    String line = reader.readLine();
    while(line != null) {
      cases.add(line);
      line = reader.readLine();
    }
    reader.close();
    return Stream.of(
      Arguments.of((Object) cases.toArray(new String[0]))
    );
  }

  @MethodSource
  @Test
  @ParameterizedTest
  void testProgramOutput(String[] args) throws Exception {
    List<String> results = new ArrayList<String>();
    for(int i = 0; i < args.length; i++) {
      String[] arg = {args[i]};
      Main.main(arg);
      results.add(checksum(outContent.toString().strip()));
    }
    assertEquals(
      results.get(0),
      "e7147da3c99ae1b5900949ae2b87e53a"
    );
    assertEquals(
      results.get(1),
      "ebf485d749170612971097e1903d4d1"
    );
    assertEquals(
      results.get(2),
      "b4f86d08bbc95078589b1734cca8fc77"
    );
  }

  @AfterAll
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

}
