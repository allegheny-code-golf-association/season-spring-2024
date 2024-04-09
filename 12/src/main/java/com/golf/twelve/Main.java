package com.golf.twelve;

public class Main {

  public static void main(String[] args) {
    int offset;
    boolean flip = false;
    int writtenSize = Integer.parseInt(args[0]);
    int diagramSize = writtenSize * 2 + 1;
    for (int i = 0; i < diagramSize - 1; i++) {
      offset = i;
      if (i == diagramSize / 2) {
        for(int j = 0; j < writtenSize; j++) {
          System.out.print("-");
        }
        System.out.print("O");
        for(int j = 0; j < writtenSize; j++) {
          System.out.print("-");
        }
        System.out.println("");
      }
      if ( offset >= writtenSize ) {
        flip = true;
        offset = (i - diagramSize) * -1 - 2;
      }
      for (int s = 0; s < offset; s++) {
        System.out.print(" ");
      }
      if(!flip) {
        System.out.print("\\");
      } else {
        System.out.print("/");
      }
      for (int s = 0; s < writtenSize - offset - 1 ; s++) {
        System.out.print(" ");
      }
      System.out.print("|");
      for (int s = 0; s < writtenSize - offset - 1 ; s++) {
        System.out.print(" ");
      }
      if(!flip) {
        System.out.print("/");
      } else {
        System.out.print("\\");
      }
      System.out.println("");
    }
  }
}
