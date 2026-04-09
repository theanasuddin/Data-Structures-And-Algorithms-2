import java.io.*;
import java.util.*;

public class StrQuick3 {

  static String[] V;

  static int charAt(String s, int d) {
    if (d < s.length()) return s.charAt(d);
    return -1;
  }

  static void sort(int s, int e, int d) {
    if (s >= e) {
      System.out.println("Immediate return from subarray " + s + "..." + e + " with depth " + d);
      return;
    }

    int mid = (s + e) / 2;
    String pivotStr = V[mid];
    System.out.println("Sorting subarray " + s + "..." + e + " with depth " + d + " and pivot " + pivotStr);

    V[mid] = V[s];
    V[s] = pivotStr;

    int v = charAt(pivotStr, d);
    int lt = s, gt = e, i = s + 1;

    while (i <= gt) {
      int c = charAt(V[i], d);
      if (c < v) {
        String tmp = V[lt];
        V[lt] = V[i];
        V[i] = tmp;
        lt++;
        i++;
      } else if (c > v) {
        String tmp = V[i];
        V[i] = V[gt];
        V[gt] = tmp;
        gt--;
      } else {
        i++;
      }
    }

    sort(s, lt - 1, d);
    if (v >= 0) sort(lt, gt, d + 1);
    sort(gt + 1, e, d);
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      System.err.println("Usage: StrQuick3 <filename>");
      return;
    }

    List < String > list = new ArrayList < > ();
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    String line;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) list.add(line);
    }
    br.close();

    V = list.toArray(new String[0]);
    int n = V.length;

    StringBuilder sb = new StringBuilder("Original:");
    for (String s: V) sb.append(" ").append(s);
    System.out.println(sb);

    sort(0, n - 1, 0);

    sb = new StringBuilder("Sorted:");
    for (String s: V) sb.append(" ").append(s);
    System.out.println(sb);
  }
}
