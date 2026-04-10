import java.io.*;
import java.util.*;

public class SuffArr {

  public static void main(String[] args) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    String T = br.readLine().trim();
    String P = br.readLine().trim();
    br.close();

    int n = T.length();
    int m = P.length();

    Integer[] sa = new Integer[n];
    for (int i = 0; i < n; i++) sa[i] = i;
    Arrays.sort(sa, (a, b) -> T.substring(a).compareTo(T.substring(b)));

    StringBuilder sb = new StringBuilder("SA:");
    for (int i = 0; i < n; i++) {
      sb.append(' ').append(sa[i]);
    }
    System.out.println(sb.toString());

    System.out.println("Binary search for " + P);

    int low = 0, high = n;
    while (low < high) {
      int mid = (low + high) / 2;

      int k = 0;
      boolean fullMatch = false;
      while (k < m) {
        int tIdx = sa[mid] + k;
        char tp = P.charAt(k);
        char tt = (tIdx < n) ? T.charAt(tIdx) : (char) 0;

        if (tp != tt) {
          break;
        }
        if (k == m - 1) {
          fullMatch = true;
          break;
        }
        k++;
      }

      if (fullMatch) {
        String tSlice = T.substring(sa[mid], sa[mid] + m);
        System.out.println("(" + low + ", " + mid + ", " + high + "): " +
          P + " <=> " + tSlice);
        System.out.println("Found the pattern at location " + sa[mid]);
        return;
      } else {
        int tIdx = sa[mid] + k;
        String ps = P.substring(0, k + 1);
        String ts;
        if (tIdx < n) {
          ts = T.substring(sa[mid], sa[mid] + k + 1);
        } else {
          ts = T.substring(sa[mid], n);
        }
        System.out.println("(" + low + ", " + mid + ", " + high + "): " +
          ps + " <=> " + ts);

        char tp = P.charAt(k);
        char tt = (tIdx < n) ? T.charAt(tIdx) : (char) 0;
        if (tp > tt) {
          low = mid + 1;
        } else {
          high = mid;
        }
      }
    }
  }
}
