import java.io.*;
import java.util.*;

public class AC {

  static int nodeCount = 0;
  static TreeMap < Character, Integer > [] children;
  static int[] parentArr;
  static char[] parentChar;
  static int[] suffix;
  static int[] depth;

  @SuppressWarnings("unchecked")
  static void ensureCapacity(int needed) {
    if (needed <= children.length) return;
    int newCap = Math.max(needed, children.length * 2);
    children = Arrays.copyOf(children, newCap);
    parentArr = Arrays.copyOf(parentArr, newCap);
    parentChar = Arrays.copyOf(parentChar, newCap);
    suffix = Arrays.copyOf(suffix, newCap);
    depth = Arrays.copyOf(depth, newCap);
  }

  @SuppressWarnings("unchecked")
  static int newNode(int par, char ch, int d) {
    ensureCapacity(nodeCount + 1);
    int id = nodeCount++;
    children[id] = new TreeMap < > ();
    parentArr[id] = par;
    parentChar[id] = ch;
    suffix[id] = -1;
    depth[id] = d;
    return id;
  }

  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    String text = br.readLine();
    if (text == null) text = "";
    List < String > patterns = new ArrayList < > ();
    String line;
    while ((line = br.readLine()) != null) {
      patterns.add(line);
    }
    br.close();

    int cap = 1024;
    children = new TreeMap[cap];
    parentArr = new int[cap];
    parentChar = new char[cap];
    suffix = new int[cap];
    depth = new int[cap];

    newNode(-1, '\0', 0);

    for (String pat: patterns) {
      int cur = 0;
      for (int i = 0; i < pat.length(); i++) {
        char c = pat.charAt(i);
        if (!children[cur].containsKey(c)) {
          int child = newNode(cur, c, depth[cur] + 1);
          children[cur].put(c, child);
        }
        cur = children[cur].get(c);
      }
    }

    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));

    out.println("Trie levels:");
    TreeMap < Integer, List < int[] >> edgesByLevel = new TreeMap < > ();
    Queue < Integer > queue = new LinkedList < > ();
    queue.add(0);
    while (!queue.isEmpty()) {
      int u = queue.poll();
      for (Map.Entry < Character, Integer > e: children[u].entrySet()) {
        int v = e.getValue();
        char c = e.getKey();
        edgesByLevel.computeIfAbsent(depth[v], k -> new ArrayList < > ())
          .add(new int[] {
            u,
            v,
            (int) c
          });
        queue.add(v);
      }
    }
    for (Map.Entry < Integer, List < int[] >> e: edgesByLevel.entrySet()) {
      int lv = e.getKey();
      List < int[] > edges = e.getValue();
      edges.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[2] - b[2]);
      StringBuilder sb = new StringBuilder(" level ").append(lv).append(':');
      for (int[] edge: edges) {
        sb.append(" (").append(edge[0]).append('-').append(edge[1])
          .append(", ").append((char) edge[2]).append(')');
      }
      out.println(sb);
    }

    Queue < Integer > bfsQ = new LinkedList < > ();
    for (int child: children[0].values()) {
      suffix[child] = 0;
      bfsQ.add(child);
    }
    while (!bfsQ.isEmpty()) {
      int u = bfsQ.poll();
      for (Map.Entry < Character, Integer > e: children[u].entrySet()) {
        char c = e.getKey();
        int v = e.getValue();
        int s = suffix[u];
        while (s != -1 && !children[s].containsKey(c)) {
          s = suffix[s];
        }
        if (s == -1) {
          suffix[v] = 0;
        } else {
          int cand = children[s].get(c);
          suffix[v] = (cand == v) ? 0 : cand;
        }
        bfsQ.add(v);
      }
    }

    out.println();
    out.println("Suffix function:");
    TreeMap < Integer, List < Integer >> nodesByLevel = new TreeMap < > ();
    Queue < Integer > bfsQ2 = new LinkedList < > ();
    bfsQ2.add(0);
    while (!bfsQ2.isEmpty()) {
      int u = bfsQ2.poll();
      nodesByLevel.computeIfAbsent(depth[u], k -> new ArrayList < > ()).add(u);
      for (int v: children[u].values()) {
        bfsQ2.add(v);
      }
    }
    for (Map.Entry < Integer, List < Integer >> e: nodesByLevel.entrySet()) {
      int lv = e.getKey();
      List < Integer > nodes = e.getValue();
      Collections.sort(nodes);
      StringBuilder sb = new StringBuilder(" level ").append(lv).append(':');
      for (int v: nodes) {
        String fac = (suffix[v] == -1) ? "null" : String.valueOf(suffix[v]);
        sb.append(" <").append(v).append(": ").append(fac).append('>');
      }
      out.println(sb);
    }

    out.println();
    out.println("Processing the text '" + text + "':");
    int cur = 0;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      List < Integer > visited = new ArrayList < > ();
      visited.add(cur);
      while (!children[cur].containsKey(c) && cur != 0) {
        cur = suffix[cur];
        visited.add(cur);
      }
      if (children[cur].containsKey(c)) {
        cur = children[cur].get(c);
        visited.add(cur);
      }
      StringBuilder sb = new StringBuilder(" ").append(c).append(':');
      for (int v: visited) {
        sb.append(' ').append(v);
      }
      out.println(sb);
    }

    out.flush();
  }
}
