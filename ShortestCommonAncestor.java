import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class ShortestCommonAncestor {

    private Digraph G; // global var for digraph
    private boolean cycle; // global var to track cycle
    private boolean[] marked; // global var to track marked verts
    private boolean[] onstack; // global var to track stack
    private int root; // track root

    // constructor takes a rooted DAG as argument
    public ShortestCommonAncestor(Digraph G) {
        if (G == null) throw new IllegalArgumentException("G cannot be null");
        if (G.V() == 0) {
            throw new IllegalArgumentException("G is not a DAG");
        }
        int moreroots = 0;
        root = 0;

        for (int i = 0; i < G.V(); i++) {
            if (G.outdegree(i) == 0) {
                moreroots++;
                root = i;
            }
        }
        if (moreroots > 1) {
            throw new IllegalArgumentException("more than 1 root");
        }
        marked = new boolean[G.V()];
        onstack = new boolean[G.V()];
        for (int n = 0; n < G.V(); n++) {
            if (!marked[n] && !cycle) {
                iscyclic(G, n);
            }
        }
        if (cycle) throw new IllegalArgumentException("cycle");
        this.G = new Digraph(G);
    }

    // Test if G is rooted


    // test if G is acyclic
    private void iscyclic(Digraph cyclicdigraph, int n) {
        onstack[n] = true;
        marked[n] = true;

        for (int x : cyclicdigraph.adj(n)) {
            if (cycle) {
                return;
            }
            else if (!marked[x]) {
                iscyclic(cyclicdigraph, x);

            }
            else if (onstack[x]) {
                cycle = true;
            }
        }
        onstack[n] = false;
    }

    // helper method that finds the shortest length and ancestor of inputs v and w
    private HashMap<Integer, Integer> findshortestint(int v) {

        HashMap<Integer, Integer> dist = new HashMap<Integer, Integer>();

        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(v);
        dist.put(v, 0);

        while (queue.peek() != null) {
            int temp = queue.remove();
            for (int vert : G.adj(temp)) {
                if (!dist.containsKey(vert)) { // w has not been visited yet
                    queue.add(vert);
                    dist.put(vert, 1 + dist.get(temp));
                }
            }
        }
        return dist;
    }

    // length of shortest ancestral path between v and w
    public int length(int v, int w) {
        validate(v);
        validate(w);

        HashMap<Integer, Integer> bfsV = findshortestint(v);
        HashMap<Integer, Integer> bfsW = findshortestint(w);

        int shortest = Integer.MAX_VALUE;
        for (int i : bfsV.keySet()) {
            if (bfsW.containsKey(i)) {
                int curdist = bfsV.get(i) + bfsW.get(i);
                if (curdist < shortest) {
                    shortest = curdist;
                }
            }
        }
        return shortest;
    }


    // a shortest common ancestor of vertices v and w
    public int ancestor(int v, int w) {
        validate(v);
        validate(w);

        HashMap<Integer, Integer> bfsV = findshortestint(v);
        HashMap<Integer, Integer> bfsW = findshortestint(w);

        int shortest = Integer.MAX_VALUE;
        int snode = root;

        for (int i : bfsV.keySet()) {
            if (bfsW.containsKey(i)) {
                int curdist = bfsV.get(i) + bfsW.get(i);
                if (curdist < shortest) {
                    shortest = curdist;
                    snode = i;
                }
            }
        }
        return snode;
    }


    // helper method that finds shortest length and ancestor of inputs v and w
    private HashMap<Integer, Integer> findshortestsubset(Iterable<Integer> verts) {

        HashMap<Integer, Integer> dist = new HashMap<Integer, Integer>();
        Queue<Integer> queue = new LinkedList<Integer>();

        for (int v : verts) {
            queue.add(v);
            dist.put(v, 0);
        }

        while (queue.peek() != null) {
            int temp = queue.remove();
            for (int vert : G.adj(temp)) {
                if (!dist.containsKey(vert)) { // w has not been visited yet
                    queue.add(vert);
                    dist.put(vert, 1 + dist.get(temp));
                }
            }
        }
        return dist;
    }

    // length of shortest ancestral path of vertex subsets A and B
    public int lengthSubset(Iterable<Integer> subsetA, Iterable<Integer> subsetB) {
        if (subsetA == null || subsetB == null)
            throw new IllegalArgumentException("Input cannot be null");
        validate1(subsetA);
        validate1(subsetB);

        HashMap<Integer, Integer> bfsV = findshortestsubset(subsetA);
        HashMap<Integer, Integer> bfsW = findshortestsubset(subsetB);

        int shortest = Integer.MAX_VALUE;
        for (int i : bfsV.keySet()) {
            if (bfsW.containsKey(i)) {
                int curdist = bfsV.get(i) + bfsW.get(i);
                if (curdist < shortest) {
                    shortest = curdist;
                }
            }
        }
        return shortest;
    }

    // a shortest common ancestor of vertex subsets A and B
    public int ancestorSubset(Iterable<Integer> subsetA, Iterable<Integer> subsetB) {
        if (subsetA == null || subsetB == null)
            throw new IllegalArgumentException("Input cannot be null");
        validate1(subsetA);
        validate1(subsetB);

        HashMap<Integer, Integer> bfsV = findshortestsubset(subsetA);
        HashMap<Integer, Integer> bfsW = findshortestsubset(subsetB);

        int shortest = Integer.MAX_VALUE;
        int snode = root;

        for (int i : bfsV.keySet()) {
            if (bfsW.containsKey(i)) {
                int curdist = bfsV.get(i) + bfsW.get(i);
                if (curdist < shortest) {
                    shortest = curdist;
                    snode = i;
                }
            }
        }
        return snode;
    }

    // validate a single vertex in digraph
    private void validate(int v) {
        if (v < 0 || v >= G.V()) throw new IllegalArgumentException();
    }

    // calls validate to go through iterable
    private void validate1(Iterable<Integer> v) {
        int count = 0;
        if (v == null) throw new IllegalArgumentException("Input is null");

        for (Integer i : v) {
            if (i == null) throw new IllegalArgumentException("null input");
            count++;
            validate(i);
        }
        if (count < 1) {
            throw new IllegalArgumentException("no v in Iterable");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        //    In in = new In(args[0]);
        //  Digraph G = new Digraph(in);
        // ShortestCommonAncestor sca = new ShortestCommonAncestor(G);
        // while (!StdIn.isEmpty()) {
        //   int v = StdIn.readInt();
        // int w = StdIn.readInt();
        //     int length = sca.length(v, w);
        //   int ancestor = sca.ancestor(v, w);
        // StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        //      }
    }
}

