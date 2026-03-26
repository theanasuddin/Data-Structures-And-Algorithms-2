import java.io.*;
import java.util.*;

public class SegTree {

  static int[] S;
  static int n;
  static int size;

  public static void main(String[] args) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    String[] arr = br.readLine().split(" ");
    n = arr.length;

    size = 1;
    while (size < n) size *= 2;

    int[] V = new int[size];

    for (int i = 0; i < n; i++) {
      V[i] = Integer.parseInt(arr[i]);
    }

    S = new int[2 * size];

    for (int i = 0; i < size; i++) {
      S[size + i] = V[i];
    }

    for (int i = size - 1; i >= 1; i--) {
      S[i] = S[2 * i] + S[2 * i + 1];
    }

    printTree();
    System.out.println();

    br = new BufferedReader(new FileReader(args[1]));
    String line;

    while ((line = br.readLine()) != null) {
      if (line.trim().isEmpty()) continue;

      String[] parts = line.split(" ");
      String cmd = parts[0];
      int p1 = Integer.parseInt(parts[1]);
      int p2 = Integer.parseInt(parts[2]);

      System.out.println();

      if (cmd.equals("query")) {
        query(p1, p2);
      } else if (cmd.equals("set")) {
        System.out.println("Updating V[" + p1 + "] = " + p2);
        update(p1, p2);
      }
    }
  }

  static void printTree() {
    System.out.println("Segment tree levels:");
    int levelStart = 1;
    int levelSize = 1;

    while (levelStart < 2 * size) {
      System.out.print(" ");
      for (int i = 0; i < levelSize && levelStart + i < 2 * size; i++) {
        if (i > 0) System.out.print(" ");
        System.out.print(S[levelStart + i]);
      }
      System.out.println();
      levelStart *= 2;
      levelSize *= 2;
    }
  }

  static void update(int idx, int val) {
    int pos = size + idx;
    S[pos] = val;

    pos /= 2;
    while (pos >= 1) {
      S[pos] = S[2 * pos] + S[2 * pos + 1];
      pos /= 2;
    }
  }

  static void query(int l, int r) {
    System.out.println("Querying interval " + l + "..." + r);

    int L = l + size;
    int R = r + size;

    int result = 0;

    while (L <= R) {
      System.out.println("  left and right positions: " + L + " " + R);

      if (L % 2 == 1) {
        int old = result;
        result += S[L];
        System.out.println("    updated result from " + old + " to " + result + " using S[" + L + "]=" + S[L]);
        L++;
      }

      if (R % 2 == 0) {
        int old = result;
        result += S[R];
        System.out.println("    updated result from " + old + " to " + result + " using S[" + R + "]=" + S[R]);
        R--;
      }

      L /= 2;
      R /= 2;
    }

    System.out.println();
    System.out.println("Sum(" + l + "..." + r + ") = " + result);
  }
}
