package cs311.hw8.graphalgorithms;

import cs311.hw8.graph.IGraph;
import cs311.hw8.graph.IGraph.Vertex;
import cs311.hw8.graph.IGraph.Edge;
import cs311.hw8.graph.Graph;

import java.util.*;

public class GraphAlgorithms
{
    //Dijkstra's Algorithm implementation
    public static <V, E extends IWeight> List<Edge<E>> ShortestPath(
            IGraph<V, E> g, String vertexStart, String vertexEnd) {
        //Maps to store vertex weight and previous vertex in shortest path
        Map<Vertex<V>, Double> vertexWeight = new HashMap<>();
        Map<Vertex<V>, Vertex<V>> previousVertex = new HashMap<>();
        LinkedList<Edge<E>> path = new LinkedList<>();

        //comapre vertices by weight in priority queue
        class vertexComparator implements Comparator<Vertex<V>>
        {
            @Override
            public int compare(Vertex<V> v1, Vertex<V> v2) {
                return Double.compare(vertexWeight.get(v1), vertexWeight.get(v2));
            }
        }

        //implements priority queue for easy access to min element
        PriorityQueue<Vertex<V>> queue = new PriorityQueue<>(new vertexComparator());

        //set each vertex weight as max
        for (Vertex<V> v : g.getVertices()) {
            vertexWeight.put(v, Double.MAX_VALUE);

        }
        //intialization of start vertex
        vertexWeight.replace(g.getVertex(vertexStart), 0.0);
        queue.add(g.getVertex(vertexStart));
        Vertex<V> min;

        //while there are still vertices not processed
        while (!queue.isEmpty()) {
            //remove vertex with smallest weight
            min = queue.remove();
            if (min.getVertexName().equals(vertexEnd)) { break; }
            //check paths from neighbors
            List<Vertex<V>> neighbors = g.getNeighbors(min.getVertexName());
            for (Vertex<V> n : neighbors) {
                double weight = vertexWeight.get(min) + g.getEdge(min.getVertexName(), n.getVertexName()).getEdgeData().getWeight();
                //if path weight from neighbor is less than current path weight, set current path to path from neighbor
                if (weight < vertexWeight.get(n)) {
                    queue.remove(n);
                    vertexWeight.replace(n, weight);
                    previousVertex.put(n, min);
                    //update priority queue
                    queue.add(n);
                }
            }
        }
        //add edge connecting vertexEnd to previous vertex in path
        Vertex<V> prev2 = g.getVertex(vertexEnd);
        Vertex<V> prev1 = previousVertex.get(prev2);
        path.addFirst(g.getEdge(prev1.getVertexName(), prev2.getVertexName()));
        //while there is still a vertex in the shortest path, add it to the list
        while (previousVertex.get(prev1) != null) {
            prev2 = previousVertex.get(prev2);
            prev1 = previousVertex.get(prev1);
            path.addFirst(g.getEdge(prev1.getVertexName(), prev2.getVertexName()));
        }
        //return shortest path as list of vertices
        return path;
    }

    //Khan's algorithm implementation
    public static <V, E> List<Vertex<V>> TopologicalSort( IGraph<V, E> g)
    {
        //Sorted list to be returned
        LinkedList<Vertex<V>> sorted = new LinkedList<>();
        //indegree of vertices
        Map<Integer, Integer> indegree = new HashMap<>();
        //set indegree for each vertex
        for (Vertex<V> v : g.getVertices()) {
            indegree.put(v.hashCode(), 0);
        }
        for (Edge e : g.getEdges()) {
            indegree.put(g.getVertex(e.getVertexName2()).hashCode(), indegree.get(g.getVertex(e.getVertexName2()).hashCode()) + 1);
        }
        //Initialize stack with vertices of degree 0
        Stack<Vertex<V>> stack = new Stack<>();
        for (Vertex<V> v : g.getVertices()) {
            if (indegree.get(v.hashCode()) == 0) {
                stack.add(v);
            }
        }
        while (!stack.isEmpty()) {
            Vertex<V> w = stack.pop();
            //add any neighbors with all incoming edges accounted for
            for (Vertex<V> n : g.getNeighbors(w.getVertexName())) {
                indegree.put(n.hashCode(), indegree.get(n.hashCode()) - 1);
                if (indegree.get(n.hashCode()) == 0) {
                    stack.push(n);
                }
            }
            sorted.addLast(w);
        }
        return (sorted.size() == g.getVertices().size()) ? sorted : null;
    }

    public static <V, E extends IWeight> IGraph<V, E> Kruscal(IGraph<V, E> g )
    {
        //minimum spanning tree
        IGraph<V,E> mst = new Graph<>();
        //Comparator for priority queue
        class EdgeComparator implements Comparator<Edge<E>> {
            @Override
            public int compare(Edge<E> e1, Edge<E> e2) {
                return (e1.getEdgeData().getWeight() < e2.getEdgeData().getWeight()) ? -1 : 1;
            }
        }
        //Initialize structures
        PriorityQueue<Edge<E>> queue = new PriorityQueue<>(g.getEdges().size(),new EdgeComparator());
        int count = 0;
        List<Vertex<V>> vertexList = new ArrayList<>(g.getVertices());
        Map<Integer, Collection<Vertex<V>>> adjList = new HashMap<>();
        //fill priority queue
        for (Edge<E> e : g.getEdges()) {
            queue.add(e);
        }
        //initialize adjacency list for use with components
        for (Vertex<V> v : vertexList) {
            mst.addVertex(v.getVertexName(), g.getVertexData(v.getVertexName()));
            Collection<Vertex<V>> vertices = new ArrayList<Vertex<V>>();
            vertices.add(v);
            adjList.put(v.hashCode(), vertices);
        }
        //While vertices left in graph
        while (count < vertexList.size() - 1) {
            Edge<E> e = queue.remove();
            count++;
            //check if in same component
            if (!adjList.get(g.getVertex(e.getVertexName1()).hashCode()).contains(g.getVertex(e.getVertexName2()))) {
                //Add edge to MST
                mst.addEdge(e.getVertexName1(), e.getVertexName2(), e.getEdgeData());
                //merge components
                Collection<Vertex<V>> tmp1 = adjList.get(g.getVertex(e.getVertexName1()).hashCode());
                Collection<Vertex<V>> tmp2 = adjList.get(g.getVertex(e.getVertexName2()).hashCode());
                tmp1.add(g.getVertex(e.getVertexName2()));
                tmp2.add(g.getVertex(e.getVertexName1()));
                adjList.put(g.getVertex(e.getVertexName1()).hashCode(), tmp1);
                adjList.put(g.getVertex(e.getVertexName2()).hashCode(), tmp2);
            }
        }
        return mst;
    }

}

