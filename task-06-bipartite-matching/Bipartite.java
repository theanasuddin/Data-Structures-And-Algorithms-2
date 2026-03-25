import java.io.*;
import java.util.*;

public class Bipartite {

  static Map < String, List < String >> graph = new HashMap < > ();
  static Map < String, String > matchR = new HashMap < > ();
  static Set < String > visited;

  public static void main(String[] args) throws Exception {

    String filename = args[0];

    BufferedReader br = new BufferedReader(new FileReader(filename));

    Set < String > leftNodes = new HashSet < > ();

    String line;
    while ((line = br.readLine()) != null) {

      line = line.trim();
      if (line.isEmpty()) continue;

      String[] parts = line.split("\\s+");

      String u = parts[0];
      String v = parts[1];

      graph.putIfAbsent(u, new ArrayList < > ());
      graph.get(u).add(v);

      leftNodes.add(u);
    }

    br.close();

    int matchingSize = 0;

    for (String u: leftNodes) {

      visited = new HashSet < > ();

      if (dfs(u)) {
        matchingSize++;
      }
    }

    System.out.println("A maximum bipartite matching with " + matchingSize + " pairs:");

    for (Map.Entry < String, String > entry: matchR.entrySet()) {
      String v = entry.getKey();
      String u = entry.getValue();
      System.out.println(u + " " + v);
    }
  }

  static boolean dfs(String u) {

    if (!graph.containsKey(u)) return false;

    for (String v: graph.get(u)) {

      if (visited.contains(v)) continue;
      visited.add(v);

      if (!matchR.containsKey(v) || dfs(matchR.get(v))) {
        matchR.put(v, u);
        return true;
      }
    }

    return false;
  }
}
