package com.interpreter.codelike;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
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

import com.interpreter.codelike.Interpreter;

class BaseTests {

  /*
    Word to the accepted answer at SO, they're a real one.
    https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println
  */

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

  private final InputStream originalIn = System.in;
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @Rule
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  @BeforeAll
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  static Stream<Arguments> testProgramOutput() throws Exception {
    URL resource = BaseTests.class.getClassLoader().getResource("main.txt");
    File file = Paths.get(resource.toURI()).toFile();
    String absPath = file.getAbsolutePath();
    return Stream.of(
      Arguments.of((Object) new String[]{absPath})
    );
  }

  @MethodSource
  @Test
  @ParameterizedTest
  void testProgramOutput(String[] args) throws Exception {
    ByteArrayInputStream in = new ByteArrayInputStream("256".getBytes());
    System.setIn(in);
    Interpreter.main(args);
    assertEquals(
      "128\n64\n32\n16\n8\n4\n2\n1\n0\n9",
      outContent.toString().strip()
    );
  }

  @AfterAll
  public void restoreStreams() {
    System.setIn(originalIn);
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

}
