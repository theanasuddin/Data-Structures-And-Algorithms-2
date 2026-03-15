import java.io.*;
import java.util.*;

public class AllPairs {

  static final long INF = Long.MAX_VALUE / 4;

  public static void main(String[] args) throws Exception {

    String filename = args[0];

    BufferedReader br = new BufferedReader(new FileReader(filename));

    List<int[]> edges = new ArrayList<>();
    int maxNode = 0;

    String line;
    while ((line = br.readLine()) != null) {

      line = line.trim();
      if (line.isEmpty())
        continue;

      String[] p = line.split("\\s+");

      int u = Integer.parseInt(p[0]);
      int v = Integer.parseInt(p[1]);
      int w = Integer.parseInt(p[2]);

      edges.add(new int[] { u, v, w });
      maxNode = Math.max(maxNode, Math.max(u, v));
    }

    br.close();

    int n = maxNode;

    long[][] D = new long[n + 1][n + 1];
    Integer[][] spEdge = new Integer[n + 1][n + 1];

    for (int i = 1; i <= n; i++)
      for (int j = 1; j <= n; j++)
        D[i][j] = (i == j) ? 0 : INF;

    for (int[] e : edges) {
      int u = e[0], v = e[1], w = e[2];
      if (w < D[u][v]) {
        D[u][v] = w;
        spEdge[u][v] = v;
      }
    }

    printTables(0, D, spEdge, n);

    for (int k = 1; k <= n; k++) {

      for (int i = 1; i <= n; i++) {

        if (D[i][k] == INF)
          continue;

        for (int j = 1; j <= n; j++) {

          if (D[k][j] == INF)
            continue;

          long newDist = D[i][k] + D[k][j];

          if (newDist < D[i][j]) {

            D[i][j] = newDist;
            spEdge[i][j] = spEdge[i][k];
          }
        }
      }

      printTables(k, D, spEdge, n);

      for (int i = 1; i <= n; i++) {
        if (D[i][i] < 0) {
          printCycle(i, spEdge);
          return;
        }
      }
    }
  }

  static void printTables(int k, long[][] D, Integer[][] spEdge, int n) {

    System.out.println("Iteration " + k);

    for (int i = 1; i <= n; i++) {

      for (int j = 1; j <= n; j++) {

        String val = (D[i][j] == INF) ? "inf" : String.valueOf(D[i][j]);

        System.out.printf("%5s", val);
      }

      System.out.println();
    }

    System.out.println();

    for (int i = 1; i <= n; i++) {

      for (int j = 1; j <= n; j++) {

        String val;

        if (spEdge[i][j] == null)
          val = "null";
        else
          val = i + "-" + spEdge[i][j];

        System.out.printf("%7s", val);
      }

      System.out.println();
    }

    System.out.println();
  }

  static void printCycle(int start, Integer[][] spEdge) {

    List<Integer> cycle = new ArrayList<>();
    Set<Integer> visited = new HashSet<>();

    int cur = start;

    while (!visited.contains(cur)) {
      visited.add(cur);
      cycle.add(cur);
      cur = spEdge[cur][start];
    }

    cycle.add(cur);

    System.out.print("A negative cycle detected:");

    for (int v : cycle)
      System.out.print(" " + v);

    System.out.println();
  }
}
