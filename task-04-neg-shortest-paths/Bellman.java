import java.io.*;
import java.util.*;

public class Bellman {

  static class Edge {
    int to;
    int w;

    Edge(int t, int w) {
      to = t;
      this.w = w;
    }
  }

  public static void main(String[] args) throws Exception {

    String filename = args[0];
    int s = Integer.parseInt(args[1]);

    Map<Integer, List<Edge>> graph = new HashMap<>();
    TreeSet<Integer> nodes = new TreeSet<>();

    BufferedReader br = new BufferedReader(new FileReader(filename));
    String line;

    while ((line = br.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty())
        continue;

      String[] p = line.split("\\s+");

      int u = Integer.parseInt(p[0]);
      int v = Integer.parseInt(p[1]);
      int w = Integer.parseInt(p[2]);

      graph.computeIfAbsent(u, k -> new ArrayList<>()).add(new Edge(v, w));

      nodes.add(u);
      nodes.add(v);
    }

    br.close();

    for (int u : graph.keySet())
      graph.get(u).sort(Comparator.comparingInt(e -> e.to));

    Map<Integer, Long> dist = new HashMap<>();
    Map<Integer, Integer> parent = new HashMap<>();

    for (int v : nodes)
      dist.put(v, Long.MAX_VALUE);

    dist.put(s, 0L);

    TreeSet<Integer> current = new TreeSet<>();
    current.add(s);

    int n = nodes.size();

    TreeSet<Integer> lastImproved = null;

    for (int iter = 1; iter <= n; iter++) {

      TreeSet<Integer> next = new TreeSet<>();

      for (int u : current) {

        long du = dist.get(u);
        if (du == Long.MAX_VALUE)
          continue;

        for (Edge e : graph.getOrDefault(u, Collections.emptyList())) {

          int v = e.to;
          long nd = du + e.w;

          if (nd < dist.get(v)) {

            dist.put(v, nd);
            parent.put(v, u);
            next.add(v);
          }
        }
      }

      if (next.isEmpty()) {

        System.out.println("No improvements in iteration " + iter);

        System.out.print("Distances from " + s + ": ");

        boolean first = true;

        for (int v : nodes) {

          if (!first)
            System.out.print(", ");
          first = false;

          if (dist.get(v) == Long.MAX_VALUE)
            System.out.print("d(" + v + ") = INF");
          else
            System.out.print("d(" + v + ") = " + dist.get(v));
        }

        System.out.println();
        return;
      }

      System.out.print("Improvements in iteration " + iter + ": ");

      boolean first = true;

      for (int v : next) {

        if (!first)
          System.out.print(", ");
        first = false;

        System.out.print("d(" + v + ") = " + dist.get(v));
      }

      System.out.println();

      lastImproved = next;
      current = next;
    }

    int u = lastImproved.first();

    Map<Integer, Integer> seen = new HashMap<>();
    List<Integer> path = new ArrayList<>();

    int cur = u;

    while (true) {

      if (seen.containsKey(cur)) {

        int start = seen.get(cur);

        List<Integer> cycle = new ArrayList<>();

        for (int i = start; i < path.size(); i++)
          cycle.add(path.get(i));

        cycle.add(cur);

        Collections.reverse(cycle);

        int cost = 0;

        for (int i = 0; i < cycle.size() - 1; i++) {

          int a = cycle.get(i);
          int b = cycle.get(i + 1);

          for (Edge e : graph.getOrDefault(a, Collections.emptyList())) {
            if (e.to == b) {
              cost += e.w;
              break;
            }
          }
        }

        System.out.print("A negative cycle with cost " + cost + " detected:");

        for (int v : cycle)
          System.out.print(" " + v);

        System.out.println();
        return;
      }

      seen.put(cur, path.size());
      path.add(cur);

      cur = parent.get(cur);
    }
  }
}
