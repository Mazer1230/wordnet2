import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class WordNet {
    /* make symbol table contain the word as a key and all the edges
     * the value (integer iterable). load words from sysnet alongside
     * their vertex number and load all connected vertices from
     * hypernym.*/

    private Digraph G;
    private ShortestCommonAncestor sca;
    private HashMap<String, Stack<Integer>> st;
    private HashMap<Integer, String> vo;

    private int V;

    // length of readline.split(",") determines how to obtain each value
    // constructor takes the name of the two input files

    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }

        st = new HashMap<String, Stack<Integer>>();
        vo = new HashMap<Integer, String>();
        In sys = new In(synsets);
        In hyp = new In(hypernyms);
        int numVerts = 0;
        HashMap<String, Stack<Integer>> synonyms = new HashMap<String, Stack<Integer>>();
        HashMap<Integer, String> integer = new HashMap<Integer, String>();

        // synonyms
        while (!sys.isEmpty()) {
            String b = sys.readLine();
            String[] sysread = b.split(",");
            numVerts++;
            String synset = sysread[1];
            int key = Integer.parseInt(sysread[0]);

            integer.put(key, synset);
            String[] nouns = synset.split(" ");

            for (String n : nouns) {
                if (synonyms.containsKey(n)) {
                    Stack<Integer> strStack = synonyms.get(n);
                    strStack.push(key);
                    synonyms.put(n, strStack);
                }
                else {
                    Stack<Integer> newstack = new Stack<Integer>();
                    newstack.push(key);
                    synonyms.put(n, newstack);
                }
            }
        }

        Digraph dg = new Digraph(numVerts);

        // hypernyms
        while (!hyp.isEmpty()) {
            String hypstrings = hyp.readLine();
            String[] arStrings = hypstrings.split(",");
            //  String hypset = arStrings[1];
            int k = Integer.parseInt(arStrings[0]);

            for (int i = 1; i < arStrings.length; i++) {
                dg.addEdge(k, Integer.parseInt(arStrings[i]));
            }
        }

        this.st = synonyms;
        this.vo = integer;
        G = new Digraph(dg);
        V = G.V();
        this.sca = new ShortestCommonAncestor(G);
        isRooted();
        isCycle();
    }


    // check for rooted digraph
    private void isRooted() {
        Digraph Gr = this.G.reverse();
        for (int v = 0; v < V; v++) {
            if (this.G.outdegree(v) == 0) {
                int count = 1;
                boolean[] marked = new boolean[V];
                marked[v] = true;
                Queue<Integer> visited = new LinkedList<>();
                visited.add(v);
                while (!visited.isEmpty()) {
                    int w = visited.remove();
                    for (int i : Gr.adj(w)) {
                        if (!marked[i]) {
                            marked[i] = true;
                            count++;
                            visited.add(i);
                        }
                    }
                }
                if (count != V) throw new IllegalArgumentException("No Root");
            }
        }
    }

    // check for cycle
    private void isCycle() {
        HashMap<Integer, Integer> indegree = new HashMap<Integer, Integer>();
        Queue<Integer> explore = new LinkedList<Integer>();
        Queue<Integer> order = new LinkedList<Integer>();

        for (int v = 0; v < G.V(); v++) {
            int inDeg = G.indegree(v);
            indegree.put(v, inDeg);
            if (G.indegree(v) == 0) {
                explore.add(v);
            }
        }

        while (!explore.isEmpty()) {
            int temp = explore.remove();
            order.add(temp);
            for (int z : G.adj(temp)) {
                int current = indegree.get(z);
                current--;
                indegree.put(z, current);
                if (current == 0) {
                    explore.add(z);
                }
            }
        }
        if (order.size() != G.V()) {
            throw new IllegalArgumentException("CYCLE");
        }
    }


    // the set of all WordNet nouns
    public Iterable<String> nouns() {
        return this.st.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        return this.st.containsKey(word);
    }


    // a synset (second field of synsets.txt) that is a shortest common ancestor
    // of noun1 and noun2 (defined below)
    public String sca(String noun1, String noun2) {
        if (noun1 == null || noun2 == null || !this.st.containsKey(noun1) || !this.st
                .containsKey(noun2)) {
            throw new IllegalArgumentException("out of pocket man");
        }

        int anty = sca.ancestorSubset(st.get(noun1), st.get(noun2));
        return vo.get(anty);
    }


    // distance between noun1 and noun2 (defined below)
    public int distance(String noun1, String noun2) {
        if (noun1 == null || noun2 == null || !this.st.containsKey(noun1) || !this.st
                .containsKey(noun2)) {
            throw new IllegalArgumentException("out of pocket man");
        }

        return sca.lengthSubset(st.get(noun1), this.st.get(noun2));
    }


    // unit testing (required)
    public static void main(String[] args) {
        WordNet k = new WordNet("synsets.txt", "hypernyms.txt");
        int nearest = k.distance("hood", "vicinity");
        System.out.println(nearest);
    }
}
