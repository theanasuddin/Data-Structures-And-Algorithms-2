import java.io.*;
import java.util.*;

public class LinAlign {
  static String A, B;
  static List < int[] > path = new ArrayList < > ();

  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    A = br.readLine();
    B = br.readLine();
    br.close();

    int m = A.length(), n = B.length();
    recursivePath(0, m - 1, 0, n - 1);

    path.sort((x, y) -> x[1] - y[1]);

    StringBuilder sb = new StringBuilder();
    for (int[] cell: path) {
      sb.append(" (").append(cell[0]).append(", ").append(cell[1]).append(")");
    }
    System.out.println(sb.toString());
  }

  static void recursivePath(int a, int b, int c, int d) {

    String subA = (a <= b) ? A.substring(a, b + 1) : "";
    String subB = (c <= d) ? B.substring(c, d + 1) : "";
    System.out.println("[" + subA + " " + a + " " + b + " " + subB + " " + c + " " + d + "]");

    int mid = (c + d) / 2;
    int m = (b >= a) ? (b - a + 1) : 0;

    int[] fwd = new int[m + 1];
    for (int j = c; j <= mid; j++) {
      int prevDiag = 0;
      for (int k = 1; k <= m; k++) {
        int temp = fwd[k];
        if (A.charAt(a + k - 1) == B.charAt(j)) {
          fwd[k] = prevDiag + 1;
        } else {
          fwd[k] = Math.max(fwd[k], fwd[k - 1]);
        }
        prevDiag = temp;
      }
    }

    int[] rev = new int[m + 1];
    for (int j = d; j >= mid + 1; j--) {
      int prevDiag = 0;
      for (int k = 1; k <= m; k++) {
        int temp = rev[k];
        if (A.charAt(b + 1 - k) == B.charAt(j)) {
          rev[k] = prevDiag + 1;
        } else {
          rev[k] = Math.max(rev[k], rev[k - 1]);
        }
        prevDiag = temp;
      }
    }

    int bestK = 0;
    int bestVal = fwd[0] + rev[m];
    for (int k = 1; k <= m; k++) {
      int val = fwd[k] + rev[m - k];
      if (val > bestVal) {
        bestVal = val;
        bestK = k;
      }
    }

    int row = a + bestK;
    int col = mid + 1;
    path.add(new int[] {
      row,
      col
    });

    int i = a + bestK - 1;

    if (c < mid) {
      recursivePath(a, i, c, mid);
    }

    if (mid + 1 < d) {
      recursivePath(i + 1, b, mid + 1, d);
    }
  }
}
