import java.util.*;
import java.io.*;

public class Dinitz {

  static int[] eTo;
  static int[] eRes;
  static int edgeCnt;
  static List < Integer > [] adj;
  static int N;

  @SuppressWarnings("unchecked")
  static void init(int n, int numOrigEdges) {
    N = n;
    eTo = new int[numOrigEdges * 2 + 4];
    eRes = new int[numOrigEdges * 2 + 4];
    adj = new List[N];
    for (int i = 0; i < N; i++) adj[i] = new ArrayList < > ();
    edgeCnt = 0;
  }

  static void addEdge(int u, int v, int cap) {
    eTo[edgeCnt] = v;
    eRes[edgeCnt] = cap;
    adj[u].add(edgeCnt++);
    eTo[edgeCnt] = u;
    eRes[edgeCnt] = 0;
    adj[v].add(edgeCnt++);
  }

  static int src, snk;
  static int[] level;

  static boolean bfs() {
    level = new int[N];
    Arrays.fill(level, -1);
    level[src] = 0;
    Deque < Integer > q = new ArrayDeque < > ();
    q.add(src);
    while (!q.isEmpty()) {
      int u = q.poll();
      if (u == snk) continue;
      for (int e: adj[u]) {
        int v = eTo[e];
        if (eRes[e] > 0 && level[v] == -1) {
          level[v] = level[u] + 1;
          q.add(v);
        }
      }
    }
    return level[snk] != -1;
  }

  static List < Integer > [] levelAdj;
  static int[] curArc;
  static boolean[] blocked;

  @SuppressWarnings("unchecked")
  static void buildLevelGraph() {
    levelAdj = new List[N];
    for (int i = 0; i < N; i++) levelAdj[i] = new ArrayList < > ();
    curArc = new int[N];
    blocked = new boolean[N];

    for (int u = 0; u < N; u++) {
      if (level[u] < 0 || level[u] >= level[snk]) continue;
      for (int e: adj[u]) {
        int v = eTo[e];
        if (eRes[e] > 0 && level[v] == level[u] + 1)
          levelAdj[u].add(e);
      }
      levelAdj[u].sort(Comparator.comparingInt(e -> eTo[e]));
    }
  }

  static void printLevelGraph(int iter, PrintWriter out) {
    int maxLev = level[snk];

    @SuppressWarnings("unchecked")
    List < int[] > [] byLevel = new List[maxLev + 1];
    for (int k = 0; k <= maxLev; k++) byLevel[k] = new ArrayList < > ();

    for (int u = 0; u < N; u++) {
      if (level[u] < 0 || level[u] >= maxLev) continue;
      for (int e: adj[u]) {
        int v = eTo[e];
        if (eRes[e] > 0 && level[v] == level[u] + 1) {
          int k = level[v];
          int pu = (e % 2 == 0) ? u : v;
          int pv = (e % 2 == 0) ? v : u;
          byLevel[k].add(new int[] {
            pu,
            pv
          });
        }
      }
    }

    out.println("Edges of the level graph for iteration " + iter + ":");
    for (int k = 1; k <= maxLev; k++) {
      List < int[] > edges = byLevel[k];
      edges.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);
      StringBuilder sb = new StringBuilder(" level ").append(k).append(":");
      for (int[] e: edges)
        sb.append(' ').append(e[0]).append('-').append(e[1]);
      out.println(sb);
    }
  }

  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    StringTokenizer st = new StringTokenizer(br.readLine());
    src = Integer.parseInt(st.nextToken());
    snk = Integer.parseInt(st.nextToken());

    List < int[] > edgeList = new ArrayList < > ();
    int maxNode = Math.max(src, snk);
    String line;
    while ((line = br.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty()) continue;
      st = new StringTokenizer(line);
      int u = Integer.parseInt(st.nextToken());
      int v = Integer.parseInt(st.nextToken());
      int c = Integer.parseInt(st.nextToken());
      edgeList.add(new int[] {
        u,
        v,
        c
      });
      maxNode = Math.max(maxNode, Math.max(u, v));
    }
    br.close();

    init(maxNode + 1, edgeList.size());
    for (int[] e: edgeList) addEdge(e[0], e[1], e[2]);

    PrintWriter out = new PrintWriter(new BufferedOutputStream(System.out));

    int totalFlow = 0;
    int iteration = 0;

    while (bfs()) {
      iteration++;
      printLevelGraph(iteration, out);
      buildLevelGraph();

      int[] pathNode = new int[N + 2];
      int[] pathEdge = new int[N + 2];
      pathNode[0] = src;
      int pathLen = 1;

      while (true) {
        int u = pathNode[pathLen - 1];

        if (u == snk) {
          int bot = Integer.MAX_VALUE;
          for (int i = 1; i < pathLen; i++)
            bot = Math.min(bot, eRes[pathEdge[i]]);
          totalFlow += bot;

          StringBuilder sb = new StringBuilder("Found a path with capacity ")
            .append(bot).append(':');
          for (int i = 0; i < pathLen; i++)
            sb.append(' ').append(pathNode[i]);
          out.println(sb);

          for (int i = 1; i < pathLen; i++) {
            eRes[pathEdge[i]] -= bot;
            eRes[pathEdge[i] ^ 1] += bot;
          }
          pathLen = 1;
          continue;
        }

        boolean advanced = false;
        while (curArc[u] < levelAdj[u].size()) {
          int e = levelAdj[u].get(curArc[u]);
          int v = eTo[e];
          if (eRes[e] <= 0) {
            curArc[u]++;
            continue;
          }
          if (blocked[v]) {
            curArc[u]++;
            continue;
          }
          if ((e & 1) == 1)
            out.println("DFS steps from " + u + " to " + v +
              " with available reverse capacity " + eRes[e]);
          else
            out.println("DFS steps from " + u + " to " + v +
              " with available capacity " + eRes[e]);
          pathNode[pathLen] = v;
          pathEdge[pathLen] = e;
          pathLen++;
          advanced = true;
          break;
        }

        if (!advanced) {
          if (pathLen == 1) {
            out.println();
            break;
          }
          pathLen--;
          int parent = pathNode[pathLen - 1];
          blocked[u] = true;
          curArc[parent]++;
          out.println("DFS backtracks from " + u + " to " + parent);
        }
      }
    }

    out.println("Maximum flow: " + totalFlow);
    out.flush();
  }
}
