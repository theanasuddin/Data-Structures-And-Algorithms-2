import java.io.*;
import java.util.*;

public class ArtBridges {

  static Map<String, List<String>> graph = new HashMap<>();

  static Map<String, Integer> disc = new HashMap<>();
  static Map<String, Integer> low = new HashMap<>();
  static Map<String, String> parent = new HashMap<>();

  static Set<String> articulation = new HashSet<>();
  static List<String[]> bridges = new ArrayList<>();

  static int time = 0;

  static void dfs(String u) {

    disc.put(u, time);
    low.put(u, time);
    time++;

    int children = 0;

    for (String v : graph.getOrDefault(u, Collections.emptyList())) {

      if (!disc.containsKey(v)) {

        parent.put(v, u);
        children++;

        dfs(v);

        low.put(u, Math.min(low.get(u), low.get(v)));

        if (!parent.containsKey(u) && children > 1) {
          articulation.add(u);
        }

        if (parent.containsKey(u) && low.get(v) >= disc.get(u)) {
          articulation.add(u);
        }

        if (low.get(v) > disc.get(u)) {

          String a = u;
          String b = v;

          if (a.compareTo(b) > 0) {
            String tmp = a;
            a = b;
            b = tmp;
          }

          bridges.add(new String[] { a, b });
        }

      } else if (!v.equals(parent.get(u))) {

        low.put(u, Math.min(low.get(u), disc.get(v)));

      }
    }
  }

  public static void main(String[] args) throws Exception {

    String filename = args[0];

    BufferedReader br = new BufferedReader(new FileReader(filename));
    String line;

    while ((line = br.readLine()) != null) {

      line = line.trim();
      if (line.isEmpty())
        continue;

      String[] parts = line.split("\\s+");

      String u = parts[0];
      String v = parts[1];

      graph.putIfAbsent(u, new ArrayList<>());
      graph.putIfAbsent(v, new ArrayList<>());

      graph.get(u).add(v);
      graph.get(v).add(u);
    }

    br.close();

    String start = graph.keySet().iterator().next();
    dfs(start);

    List<String> arts = new ArrayList<>(articulation);
    Collections.sort(arts);

    System.out.print("Found " + arts.size() + " articulation nodes:");
    for (String a : arts) {
      System.out.print(" " + a);
    }
    System.out.println();

    bridges.sort((a, b) -> {
      int cmp = a[0].compareTo(b[0]);
      if (cmp != 0)
        return cmp;
      return a[1].compareTo(b[1]);
    });

    System.out.print("Found " + bridges.size() + " bridges:");
    for (String[] e : bridges) {
      System.out.print(" " + e[0] + "-" + e[1]);
    }
    System.out.println();
  }
}