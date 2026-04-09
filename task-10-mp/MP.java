import java.io.*;

public class MP {

  static int[] computeFm(String P) {
    int m = P.length();
    int[] fm = new int[m];
    fm[0] = 0;
    int k = 0;
    for (int i = 1; i < m; i++) {
      while (k > 0 && Character.toLowerCase(P.charAt(i)) != Character.toLowerCase(P.charAt(k))) {
        k = fm[k - 1];
      }
      if (Character.toLowerCase(P.charAt(i)) == Character.toLowerCase(P.charAt(k))) {
        k++;
      }
      fm[i] = k;
    }
    return fm;
  }

  public static void main(String[] args) throws IOException {
    String filename = args[0];
    BufferedReader br = new BufferedReader(new FileReader(filename));
    String P = br.readLine();
    String T = br.readLine();
    br.close();

    int m = P.length();
    int n = T.length();

    System.out.println("P: " + P);

    int[] fm = computeFm(P);
    StringBuilder sbFm = new StringBuilder("Function fm:");
    for (int x: fm) {
      sbFm.append(" ").append(x);
    }
    System.out.println(sbFm.toString());

    int i = 0, j = 0;

    while (i <= n - m + j) {
      int pos = i - j;
      System.out.println("P at pos " + pos + " with i = " + i + " and j = " + j);

      int matchStartI = i;
      int matchStartJ = j;

      while (j < m && i < n &&
        Character.toLowerCase(T.charAt(i)) == Character.toLowerCase(P.charAt(j))) {
        i++;
        j++;
      }

      if (i > matchStartI) {
        System.out.println("  matched T[" + matchStartI + ".." + (i - 1) + "] = " +
          T.substring(matchStartI, i) +
          " = P[" + matchStartJ + ".." + (j - 1) + "] = " +
          P.substring(matchStartJ, j));
      }

      if (j == m) {
        System.out.println("  found an occurrence of P");
        System.out.println("  updated j from " + j + " to fm[" + (j - 1) + "] = " + fm[j - 1]);
        j = fm[j - 1];
      } else if (i < n) {
        System.out.println("  mismatch T[" + i + "] = " + T.charAt(i) +
          " != P[" + j + "] = " + P.charAt(j));
        if (j > 0) {
          System.out.println("  updated j from " + j + " to fm[" + (j - 1) + "] = " + fm[j - 1]);
          j = fm[j - 1];
        } else {
          System.out.println("  incremented i from " + i + " to " + (i + 1));
          i++;
        }
      } else {
        break;
      }
    }
  }
}
