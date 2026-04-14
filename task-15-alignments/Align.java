import java.io.*;

public class Align {

  static int[][] L;
  static String A, B;
  static int m, n;
  static char[] alignA;
  static char[] alignB;
  static int len;
  static PrintWriter out;

  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    A = br.readLine().trim();
    B = br.readLine().trim();
    br.close();

    m = A.length();
    n = B.length();

    L = new int[m + 1][n + 1];
    for (int i = 1; i <= m; i++) {
      for (int j = 1; j <= n; j++) {
        if (A.charAt(i - 1) == B.charAt(j - 1)) {
          L[i][j] = L[i - 1][j - 1] + 1;
        } else {
          L[i][j] = Math.max(L[i - 1][j], L[i][j - 1]);
        }
      }
    }

    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
    out.println("LCS length: " + L[m][n]);

    alignA = new char[m + n];
    alignB = new char[m + n];
    len = 0;

    dfs(m, n);
    out.flush();
  }

  static void dfs(int i, int j) {

    if (i == 0 && j == 0) {

      StringBuilder ra = new StringBuilder(len);
      StringBuilder rb = new StringBuilder(len);
      for (int k = len - 1; k >= 0; k--) {
        ra.append(alignA[k]);
        rb.append(alignB[k]);
      }
      out.println();
      out.println(ra);
      out.println(rb);
      return;
    }

    if (i > 0 && j > 0 &&
      A.charAt(i - 1) == B.charAt(j - 1) &&
      L[i][j] == L[i - 1][j - 1] + 1) {
      alignA[len] = A.charAt(i - 1);
      alignB[len] = B.charAt(j - 1);
      len++;
      dfs(i - 1, j - 1);
      len--;
    }

    if (j > 0 && L[i][j] == L[i][j - 1]) {
      alignA[len] = ' ';
      alignB[len] = B.charAt(j - 1);
      len++;
      dfs(i, j - 1);
      len--;
    }

    if (i > 0 && L[i][j] == L[i - 1][j]) {
      alignA[len] = A.charAt(i - 1);
      alignB[len] = ' ';
      len++;
      dfs(i - 1, j);
      len--;
    }
  }
}
