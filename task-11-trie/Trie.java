import java.util.*;
import java.io.*;

public class Trie {

  static int nodeCount = 0;

  static ArrayList < int[] > [] edges;
  static ArrayList < Integer > [] stringSet;

  static final int MAX_NODES = 50000;

  @SuppressWarnings("unchecked")
  static void init() {
    edges = new ArrayList[MAX_NODES];
    stringSet = new ArrayList[MAX_NODES];
    for (int i = 0; i < MAX_NODES; i++) {
      edges[i] = new ArrayList < > ();
      stringSet[i] = new ArrayList < > ();
    }
  }

  static int newNode() {
    return nodeCount++;
  }

  static int getChild(int node, int c) {
    ArrayList < int[] > e = edges[node];
    int lo = 0, hi = e.size();
    while (lo < hi) {
      int mid = (lo + hi) >>> 1;
      int mc = e.get(mid)[0];
      if (mc == c) return e.get(mid)[1];
      else if (mc < c) lo = mid + 1;
      else hi = mid;
    }
    return -1;
  }

  static void addChild(int node, int c, int child) {
    ArrayList < int[] > e = edges[node];
    int lo = 0, hi = e.size();
    while (lo < hi) {
      int mid = (lo + hi) >>> 1;
      if (e.get(mid)[0] < c) lo = mid + 1;
      else hi = mid;
    }
    e.add(lo, new int[] {
      c,
      child
    });
  }

  static void insert(int root, String s, int strIdx) {
    int cur = root;
    for (int i = 0; i < s.length(); i++) {
      int c = s.charAt(i);
      int child = getChild(cur, c);
      if (child == -1) {
        child = newNode();
        addChild(cur, c, child);
      }
      cur = child;
      stringSet[cur].add(strIdx);
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.err.println("Usage: Trie <strings_file> <patterns_file>");
      System.exit(1);
    }

    List < String > strings = readLines(args[0]);
    List < String > patterns = readLines(args[1]);

    init();
    int root = newNode();

    for (int i = 0; i < strings.size(); i++) {
      insert(root, strings.get(i), i + 1);
    }

    int[] depth = new int[MAX_NODES];
    Arrays.fill(depth, -1);
    depth[root] = 0;

    Queue < Integer > bfsQ = new ArrayDeque < > ();
    bfsQ.add(root);

    TreeMap < Integer, List < int[] >> levelEdges = new TreeMap < > ();

    while (!bfsQ.isEmpty()) {
      int node = bfsQ.poll();
      for (int[] e: edges[node]) {
        int child = e[1];
        int c = e[0];
        int lvl = depth[node] + 1;
        if (depth[child] == -1) {
          depth[child] = lvl;
          bfsQ.add(child);
        }
        levelEdges.computeIfAbsent(lvl, k -> new ArrayList < > ())
          .add(new int[] {
            node,
            child,
            c
          });
      }
    }

    StringBuilder out = new StringBuilder();
    out.append("Trie levels:\n");
    for (Map.Entry < Integer, List < int[] >> entry: levelEdges.entrySet()) {
      int lvl = entry.getKey();
      List < int[] > eList = entry.getValue();
      eList.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[2] - b[2]);

      out.append(" level ").append(lvl).append(':');
      for (int[] e: eList) {
        out.append(" (").append(e[0]).append('-').append(e[1])
          .append(", ").append((char) e[2]).append(')');
      }
      out.append('\n');
    }

    for (String pattern: patterns) {
      out.append('\n');
      out.append("Matching P = ").append(pattern).append('\n');
      int cur = root;
      boolean failed = false;

      for (int i = 0; i < pattern.length() && !failed; i++) {
        int c = pattern.charAt(i);

        ArrayList < int[] > e = edges[cur];
        int lo = 0, hi = e.size();
        out.append("  searching ").append((char) c).append(':');

        int foundChild = -1;
        while (lo < hi) {
          int mid = (lo + hi) >>> 1;
          int mc = e.get(mid)[0];
          out.append(" [").append(lo).append(", ").append(hi)
            .append(", ").append(Character.toLowerCase((char) mc)).append(']');
          if (mc == c) {
            foundChild = e.get(mid)[1];
            break;
          } else if (mc < c) {
            lo = mid + 1;
          } else {
            hi = mid;
          }
        }
        out.append('\n');

        if (foundChild != -1) {
          out.append("  move from ").append(cur).append(" to ").append(foundChild)
            .append(" with character P[").append(i).append("] = ").append((char) c)
            .append('\n');
          cur = foundChild;
          if (i == pattern.length() - 1) {
            out.append("  P matches with (prefixes of):");
            for (int idx: stringSet[cur]) {
              out.append(" S").append(idx);
            }
            out.append('\n');
          }
        } else {
          out.append("  matching failed at ").append(cur)
            .append(" with character P[").append(i).append("] = ").append((char) c)
            .append('\n');
          failed = true;
        }
      }
    }

    System.out.print(out);
  }

  static List < String > readLines(String filename) throws IOException {
    List < String > lines = new ArrayList < > ();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (!line.isEmpty()) lines.add(line);
      }
    }
    return lines;
  }
}
