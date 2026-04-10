import java.util.*;
import java.io.*;

public class RegEx {

  static class State {
    String label;
    State nonEps;
    List < State > eps = new ArrayList < > ();
    State forward;

    State canon() {
      State s = this;
      while (s.forward != null) s = s.forward;
      return s;
    }
  }

  static class NFA {
    State start, end;
    NFA(State s, State e) {
      start = s;
      end = e;
    }
  }

  static Map < Integer, String[] > nodes = new HashMap < > ();

  public static void main(String[] args) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    String line;
    while ((line = br.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty()) continue;
      int sp = line.indexOf(' ');
      int num = Integer.parseInt(line.substring(0, sp));
      String rest = line.substring(sp + 1);
      String[] parts = rest.split(" ");
      nodes.put(num, parts);
    }
    br.close();

    NFA nfa = build(0);

    Map < State, Integer > stateId = new LinkedHashMap < > ();
    Map < State, Integer > stateLvl = new HashMap < > ();
    Queue < State > queue = new LinkedList < > ();
    Map < Integer, List < String >> byLevel = new TreeMap < > ();

    State init = nfa.start.canon();
    stateId.put(init, 0);
    stateLvl.put(init, 0);
    queue.add(init);

    while (!queue.isEmpty()) {
      State s = queue.poll();
      int sid = stateId.get(s);
      int sl = stateLvl.get(s);
      int tl = sl + 1;
      List < String > trans = byLevel.computeIfAbsent(tl, k -> new ArrayList < > ());

      if (s.nonEps != null) {
        State t = s.nonEps.canon();
        if (!stateId.containsKey(t)) {
          stateId.put(t, stateId.size());
          stateLvl.put(t, tl);
          queue.add(t);
        }
        trans.add("[" + sid + "-" + stateId.get(t) + ": " + s.label + "]");
      }

      for (State e: s.eps) {
        State t = e.canon();
        if (!stateId.containsKey(t)) {
          stateId.put(t, stateId.size());
          stateLvl.put(t, tl);
          queue.add(t);
        }
        trans.add("[" + sid + "-" + stateId.get(t) + ": EPS]");
      }
    }

    System.out.println("NFA transitions:");
    for (Map.Entry < Integer, List < String >> entry: byLevel.entrySet()) {
      List < String > ts = entry.getValue();
      if (!ts.isEmpty()) {
        StringBuilder sb = new StringBuilder("Level ").append(entry.getKey()).append(":");
        for (String t: ts) sb.append(" ").append(t);
        System.out.println(sb.toString());
      }
    }
  }

  static NFA build(int num) {
    String[] p = nodes.get(num);
    String op = p[0];
    if (op.equals("concat")) return buildConcat(Integer.parseInt(p[1]), Integer.parseInt(p[2]));
    if (op.equals("|")) return buildUnion(Integer.parseInt(p[1]), Integer.parseInt(p[2]));
    if (op.equals("*")) return buildStar(Integer.parseInt(p[1]));
    if (op.equals("+")) return buildPlus(Integer.parseInt(p[1]));
    return buildPrimitive(op);
  }

  static NFA buildPrimitive(String str) {
    if (str.equals("\\w") || str.equals("\\.")) {
      State s = new State(), e = new State();
      s.label = str.equals("\\.") ? "." : "\\w";
      s.nonEps = e;
      return new NFA(s, e);
    }
    State first = new State();
    State cur = first;
    for (int i = 0; i < str.length(); i++) {
      State next = new State();
      cur.label = String.valueOf(str.charAt(i));
      cur.nonEps = next;
      cur = next;
    }
    return new NFA(first, cur);
  }

  static NFA buildConcat(int n1, int n2) {
    NFA a = build(n1), b = build(n2);
    State ae = a.end, bs = b.start;
    ae.label = bs.label;
    ae.nonEps = bs.nonEps;
    ae.eps.addAll(bs.eps);
    bs.forward = ae;
    return new NFA(a.start, b.end);
  }

  static NFA buildUnion(int n1, int n2) {
    NFA a = build(n1), b = build(n2);
    State start = new State(), end = new State();
    start.eps.add(a.start);
    start.eps.add(b.start);
    a.end.eps.add(end);
    b.end.eps.add(end);
    return new NFA(start, end);
  }

  static NFA buildStar(int n1) {
    NFA a = build(n1);
    State start = new State(), end = new State();
    a.end.eps.add(a.start);
    start.eps.add(a.start);
    start.eps.add(end);
    a.end.eps.add(end);
    return new NFA(start, end);
  }

  static NFA buildPlus(int n1) {
    NFA a = build(n1);
    a.end.eps.add(a.start);
    return new NFA(a.start, a.end);
  }
}
