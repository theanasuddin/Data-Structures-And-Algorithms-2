import java.io.*;
import java.util.*;

public class MST {

  static class Edge {
    int from;
    int to;
    int w;

    Edge(int f, int t, int w) {
      this.from = f;
      this.to = t;
      this.w = w;
    }
  }

  public static void main(String[] args) throws Exception {

    String filename = args[0];
    int start = Integer.parseInt(args[1]);

    Map<Integer, List<Edge>> graph = new HashMap<>();

    BufferedReader br = new BufferedReader(new FileReader(filename));
    String line;

    while ((line = br.readLine()) != null) {

      line = line.trim();
      if (line.isEmpty())
        continue;

      String[] parts = line.split("\\s+");

      int u = Integer.parseInt(parts[0]);
      int v = Integer.parseInt(parts[1]);
      int w = Integer.parseInt(parts[2]);

      Edge e = new Edge(u, v, w);

      graph.putIfAbsent(u, new ArrayList<>());
      graph.putIfAbsent(v, new ArrayList<>());

      graph.get(u).add(e);
      graph.get(v).add(e);
    }

    br.close();

    Set<Integer> inMST = new HashSet<>();

    PriorityQueue<Edge> pq = new PriorityQueue<>(
        (a, b) -> {
          if (a.w != b.w)
            return Integer.compare(a.w, b.w);
          if (a.from != b.from)
            return Integer.compare(a.from, b.from);
          return Integer.compare(a.to, b.to);
        });

    List<Edge> mstEdges = new ArrayList<>();

    int totalCost = 0;

    inMST.add(start);

    for (Edge e : graph.getOrDefault(start, Collections.emptyList())) {
      pq.add(e);
    }

    int iteration = 1;

    while (!pq.isEmpty()) {

      System.out.println("** Iteration " + iteration + " **");
      iteration++;

      Edge e = pq.poll();

      boolean fromIn = inMST.contains(e.from);
      boolean toIn = inMST.contains(e.to);

      if (fromIn && toIn) {
        continue;
      }

      int newNode = fromIn ? e.to : e.from;

      mstEdges.add(e);
      totalCost += e.w;

      inMST.add(newNode);

      System.out.println(
          "Adding the edge (" + e.from + ", " + e.to + ", " + e.w + ") with the new node " + newNode);

      for (Edge f : graph.getOrDefault(newNode, Collections.emptyList())) {

        boolean fFrom = inMST.contains(f.from);
        boolean fTo = inMST.contains(f.to);

        if (!(fFrom && fTo)) {
          pq.add(f);
        }
      }
    }

    mstEdges.sort((a, b) -> {
      if (a.from != b.from)
        return Integer.compare(a.from, b.from);
      return Integer.compare(a.to, b.to);
    });

    System.out.print("MST(" + totalCost + "):");

    for (Edge e : mstEdges) {
      System.out.print(" " + e.from + "-" + e.to);
    }

    System.out.println();
  }
}