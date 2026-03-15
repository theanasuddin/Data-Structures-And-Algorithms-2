import java.io.*;
import java.util.*;

public class TopCycle {

  public static void main(String[] args) throws Exception {

    String filename = args[0];

    Map<Integer, List<Integer>> out = new HashMap<>();
    Map<Integer, List<Integer>> in = new HashMap<>();
    Map<Integer, Integer> indeg = new HashMap<>();
    Set<Integer> nodes = new HashSet<>();

    BufferedReader br = new BufferedReader(new FileReader(filename));
    String line;

    while ((line = br.readLine()) != null) {

      line = line.trim();
      if (line.isEmpty())
        continue;

      String[] p = line.split("\\s+");

      int u = Integer.parseInt(p[0]);
      int v = Integer.parseInt(p[1]);

      nodes.add(u);
      nodes.add(v);

      out.putIfAbsent(u, new ArrayList<>());
      out.putIfAbsent(v, new ArrayList<>());
      in.putIfAbsent(u, new ArrayList<>());
      in.putIfAbsent(v, new ArrayList<>());

      out.get(u).add(v);
      in.get(v).add(u);
    }

    br.close();

    for (int n : nodes)
      indeg.put(n, 0);

    for (int u : out.keySet())
      for (int v : out.get(u))
        indeg.put(v, indeg.get(v) + 1);

    PriorityQueue<Integer> pq = new PriorityQueue<>();

    for (int n : nodes)
      if (indeg.get(n) == 0)
        pq.add(n);

    List<Integer> topo = new ArrayList<>();
    Set<Integer> removed = new HashSet<>();

    while (!pq.isEmpty()) {

      int u = pq.poll();
      topo.add(u);
      removed.add(u);

      for (int v : out.getOrDefault(u, Collections.emptyList())) {

        indeg.put(v, indeg.get(v) - 1);

        if (indeg.get(v) == 0)
          pq.add(v);
      }
    }

    if (topo.size() == nodes.size()) {

      System.out.print("Found a topological order:");
      for (int n : topo)
        System.out.print(" " + n);

      System.out.println();
      return;
    }

    TreeSet<Integer> remaining = new TreeSet<>();
    for (int n : nodes)
      if (!removed.contains(n))
        remaining.add(n);

    int start = remaining.first();

    List<Integer> path = new ArrayList<>();
    Map<Integer, Integer> pos = new HashMap<>();

    int cur = start;

    while (true) {

      if (pos.containsKey(cur)) {

        int firstIndex = pos.get(cur);

        List<Integer> cycle = new ArrayList<>();

        for (int i = firstIndex; i < path.size(); i++)
          cycle.add(path.get(i));

        Collections.reverse(cycle);

        while (cycle.get(0) != cur) {
          cycle.add(cycle.remove(0));
        }

        cycle.add(cur);

        System.out.print("The graph is not acyclic, found a cycle:");
        for (int n : cycle)
          System.out.print(" " + n);

        System.out.println();
        return;
      }

      pos.put(cur, path.size());
      path.add(cur);

      int next = Integer.MAX_VALUE;

      for (int p : in.getOrDefault(cur, Collections.emptyList()))
        if (remaining.contains(p))
          next = Math.min(next, p);

      cur = next;
    }
  }
}
