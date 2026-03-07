import java.io.*;
import java.util.*;

public class BFS {

  public static void main(String[] args) throws Exception {

    String filename = args[0];
    int start = Integer.parseInt(args[1]);

    Map<Integer, List<Integer>> graph = new HashMap<>();

    BufferedReader br = new BufferedReader(new FileReader(filename));
    String line;

    while ((line = br.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty())
        continue;

      String[] parts = line.split("\\s+");
      int u = Integer.parseInt(parts[0]);
      int v = Integer.parseInt(parts[1]);

      graph.putIfAbsent(u, new ArrayList<>());
      graph.putIfAbsent(v, new ArrayList<>());

      graph.get(u).add(v);
      graph.get(v).add(u);
    }

    br.close();

    Queue<Integer> queue = new LinkedList<>();
    Map<Integer, Integer> distance = new HashMap<>();

    queue.add(start);
    distance.put(start, 0);

    while (!queue.isEmpty()) {
      int current = queue.poll();
      int d = distance.get(current);

      for (int neighbor : graph.getOrDefault(current, Collections.emptyList())) {
        if (!distance.containsKey(neighbor)) {
          distance.put(neighbor, d + 1);
          queue.add(neighbor);
        }
      }
    }

    Map<Integer, List<Integer>> layers = new HashMap<>();

    for (Map.Entry<Integer, Integer> entry : distance.entrySet()) {
      int node = entry.getKey();
      int layer = entry.getValue();

      layers.putIfAbsent(layer, new ArrayList<>());
      layers.get(layer).add(node);
    }

    List<Integer> layerNumbers = new ArrayList<>(layers.keySet());
    Collections.sort(layerNumbers);

    for (int l : layerNumbers) {

      List<Integer> nodes = layers.get(l);
      Collections.sort(nodes);

      System.out.print("Layer " + l + ":");

      for (int node : nodes) {
        System.out.print(" " + node);
      }

      System.out.println();
    }
  }
}