/******************************************************************************
 *  Compilation:  javac Digraph.java
 *  Execution:    java Digraph filename.txt
 *  Dependencies: In.java
 *
 *  Data files:   https://algs4.cs.princeton.edu/42digraph/tinyDG.txt
 *                https://algs4.cs.princeton.edu/42digraph/mediumDG.txt
 *                https://algs4.cs.princeton.edu/42digraph/largeDG.txt
 *
 *  A graph, implemented using an array of lists.
 *  Parallel edges and self-loops are permitted.
 *
 *  % java Digraph tinyDG.txt
 *  13 vertices, 22 edges
 *  0: 5 1
 *  1:
 *  2: 0 3
 *  3: 5 2
 *  4: 3 2
 *  5: 4
 *  6: 9 4 8 0
 *  7: 6 9
 *  8: 6
 *  9: 11 10
 *  10: 12
 *  11: 4 12
 *  12: 9
 *
 ******************************************************************************/


import edu.princeton.cs.algs4.In;

import java.util.HashSet;
import java.util.NoSuchElementException;


/**
 * The {@code Digraph} class represents a directed graph of vertices
 * named 0 through <em>V</em> - 1.
 * It supports the following two primary operations: add an edge to the digraph,
 * iterate over all of the vertices adjacent from a given vertex.
 * It also provides
 * methods for returning the indegree or outdegree of a vertex,
 * the number of vertices <em>V</em> in the digraph,
 * the number of edges <em>E</em> in the digraph, and the reverse digraph.
 * Parallel edges and self-loops are permitted.
 * <p>
 * This implementation uses an <em>adjacency-lists representation</em>, which
 * is a vertex-indexed array of java.util.HashSet objects.
 * It uses &Theta;(<em>E</em> + <em>V</em>) space, where <em>E</em> is
 * the number of edges and <em>V</em> is the number of vertices.
 * The <code>reverse()</code> method takes &Theta;(<em>E</em> + <em>V</em>) time
 * and space; all other instancce methods take &Theta;(1) time. (Though, iterating over
 * the vertices returned by {@link #adj(int)} takes time proportional
 * to the outdegree of the vertex.)
 * Constructing an empty digraph with <em>V</em> vertices takes
 * &Theta;(<em>V</em>) time; constructing a digraph with <em>E</em> edges
 * and <em>V</em> vertices takes &Theta;(<em>E</em> + <em>V</em>) time.
 * <p>
 * For additional documentation,
 * see <a href="https://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 * <p>
 * Minor Revision:  in the code below, the instance variables for the number
 * of vertices <em>V</em> and the number of edges <em>E</em> are renamed
 * <em>numOfVertices</em> and <em>numOfEdges</em>. The original implemention
 * use the Bag class in the algs4 library for adjacency list. The implementation
 * below uses java.util.HashSet.
 */

public class Digraph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int numOfVertices;      // number of vertices in this digraph
    private int numOfEdges;               // number of edges in this digraph
    private HashSet<Integer>[] adj;    // adj[v] = adjacency set for vertex v
    private int[] indegree;        // indegree[v] = indegree of vertex v


    /**
     * Initializes an empty digraph with <em>V</em> vertices.
     *
     * @param numOfVertices the number of vertices
     * @throws IllegalArgumentException if {@code V < 0}
     */
    public Digraph(int numOfVertices) {
        if (numOfVertices < 0) throw new IllegalArgumentException(
                "Number of vertices in a Digraph must be non-negative");
        this.numOfVertices = numOfVertices;
        this.numOfEdges = 0;
        indegree = new int[numOfVertices];
        adj = (HashSet<Integer>[]) new HashSet[numOfVertices];
        for (int v = 0; v < numOfVertices; v++) {
            adj[v] = new HashSet<Integer>();
        }
    }

    /**
     * Initializes a digraph from the specified input stream.
     * The format is the number of vertices <em>V</em>,
     * followed by the number of edges <em>E</em>,
     * followed by <em>E</em> pairs of vertices, with each entry separated by whitespace.
     *
     * @param in the input stream
     * @throws IllegalArgumentException if {@code in} is {@code null}
     * @throws IllegalArgumentException if the endpoints of any edge are not in prescribed range
     * @throws IllegalArgumentException if the number of vertices or edges is negative
     * @throws IllegalArgumentException if the input stream is in the wrong format
     */
    public Digraph(In in) {
        if (in == null) throw new IllegalArgumentException("argument is null");
        try {
            this.numOfVertices = in.readInt();
            if (numOfVertices < 0) throw new IllegalArgumentException(
                    "number of vertices in a Digraph must be non-negative");
            indegree = new int[numOfVertices];
            adj = (HashSet<Integer>[]) new HashSet[numOfVertices];
            for (int v = 0; v < numOfVertices; v++) {
                adj[v] = new HashSet<Integer>();
            }
            int numOfEdges = in.readInt();
            if (numOfEdges < 0) throw new IllegalArgumentException(
                    "number of edges in a Digraph must be non-negative");
            for (int i = 0; i < numOfEdges; i++) {
                int v = in.readInt();
                int w = in.readInt();
                addEdge(v, w);
            }
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format in Digraph constructor", e);
        }
    }

    /**
     * Initializes a new digraph that is a deep copy of the specified digraph.
     *
     * @param G the digraph to copy
     * @throws IllegalArgumentException if {@code G} is {@code null}
     */
    public Digraph(Digraph G) {
        if (G == null) throw new IllegalArgumentException("argument is null");

        this.numOfVertices = G.V();
        this.numOfEdges = G.E();
        if (numOfVertices < 0) throw new IllegalArgumentException(
                "Number of vertices in a Digraph must be non-negative");

        // update indegrees
        indegree = new int[numOfVertices];
        for (int v = 0; v < numOfVertices; v++)
            this.indegree[v] = G.indegree(v);

        // update adjacency lists
        adj = (HashSet<Integer>[]) new HashSet[numOfVertices];
        for (int v = 0; v < numOfVertices; v++) {
            adj[v] = new HashSet<Integer>();
        }

        for (int v = 0; v < G.V(); v++) {
            for (int w : G.adj[v]) {
                adj[v].add(w);
            }
        }
    }

    /**
     * Returns the number of vertices in this digraph.
     *
     * @return the number of vertices in this digraph
     */
    public int V() {
        return numOfVertices;
    }

    /**
     * Returns the number of edges in this digraph.
     *
     * @return the number of edges in this digraph
     */
    public int E() {
        return numOfEdges;
    }


    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= numOfVertices)
            throw new IllegalArgumentException(
                    "vertex " + v + " is not between 0 and " + (numOfVertices - 1));
    }

    /**
     * Adds the directed edge v→w to this digraph.
     *
     * @param v the tail vertex
     * @param w the head vertex
     * @throws IllegalArgumentException unless both {@code 0 <= v < V} and {@code 0 <= w < V}
     */
    public void addEdge(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        adj[v].add(w);
        indegree[w]++;
        numOfEdges++;
    }

    /**
     * Returns the vertices adjacent from vertex {@code v} in this digraph.
     *
     * @param v the vertex
     * @return the vertices adjacent from vertex {@code v} in this digraph, as an iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Integer> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    /**
     * Returns the number of directed edges incident from vertex {@code v}.
     * This is known as the <em>outdegree</em> of vertex {@code v}.
     *
     * @param v the vertex
     * @return the outdegree of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int outdegree(int v) {
        validateVertex(v);
        return adj[v].size();
    }

    /**
     * Returns the number of directed edges incident to vertex {@code v}.
     * This is known as the <em>indegree</em> of vertex {@code v}.
     *
     * @param v the vertex
     * @return the indegree of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int indegree(int v) {
        validateVertex(v);
        return indegree[v];
    }

    /**
     * Returns the reverse of the digraph.
     *
     * @return the reverse of the digraph
     */
    public Digraph reverse() {
        Digraph reverse = new Digraph(numOfVertices);
        for (int v = 0; v < numOfVertices; v++) {
            for (int w : adj(v)) {
                reverse.addEdge(w, v);
            }
        }
        return reverse;
    }

    /**
     * Returns a string representation of the graph.
     *
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     * followed by the <em>V</em> adjacency lists
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(numOfVertices + " vertices, " + numOfEdges + " edges " + NEWLINE);
        for (int v = 0; v < numOfVertices; v++) {
            s.append(String.format("%d: ", v));
            for (int w : adj[v]) {
                s.append(String.format("%d ", w));
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

    /**
     * Unit tests the {@code Digraph} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        System.out.println(G);
    }

}

/******************************************************************************
 *  Copyright 2002-2020, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
