package cs311.hw8.OSMMap;


import cs311.hw8.graph.Graph;
import cs311.hw8.graph.IGraph;
import cs311.hw8.graphalgorithms.GraphAlgorithms;
import cs311.hw8.graphalgorithms.IWeight;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OSMMap {

    //graph to store XML map data
    private final Graph<Location, edgeData> streetMap;
    //is incremented every time an edge is added
    private double totalDistance;

    // stores vertex data
    private static class Location {
        private double latitude;
        private double longitude;
        //needed for closest road method
        private boolean outDegree = false;

        private Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        private double getLatitude() {       // returns the latitude of the location
            return latitude;
        }
        private double getLongitude() {      // returns the longitude of the location
            return longitude;
        }

        //needed for closest road method
        private void setOutDegree() {
            outDegree = true;
        }
        private boolean hasOutDegree() {
            return outDegree;
        }
    }

    //stores edge data
    private class edgeData implements IWeight {
        String name;
        double distance;

        private edgeData(String name, double distance) {
            this.name = name;
            this.distance = distance;
        }
        //edge weight is distance for this project
        @Override
        public double getWeight() {
            return distance;
        }
    }
    //intializes graph and distance
    public OSMMap() {
        streetMap = new Graph<>();
        streetMap.setDirectedGraph();
        totalDistance = 0;
    }

    // Found at http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
    // Written by StackOverflow user "David George"
    // Modified to eliminate need for height between points and to return distance in miles
    private static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance * 0.000621371; // return distance in miles
    }

    //Loads XML Map data into graph
    private void LoadMap(String filename)  {
        //If a previous Map is loaded, clear the graph
        if (!streetMap.getEdges().isEmpty() && !streetMap.getVertices().isEmpty()) {
            streetMap.clear();
        }
        //exception handling
        try {
            //Initialize document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            //load file to parse & intialize needed variables
            Document doc = builder.parse(new File(filename));
            doc.normalize();
            NodeList nodeList = doc.getElementsByTagName("node");
            Element e;
            String name;
            double lon;  double lat;
            int length = nodeList.getLength();
            //for each node: parse name, latitude, and longitude
            for (int i = 0; i < length; i++) {
                e = (Element) nodeList.item(i);
                name = e.getAttribute("id");
                lat = Double.parseDouble(e.getAttribute("lat"));
                lon = Double.parseDouble(e.getAttribute("lon"));
                //add data to street map
                Location loc = new Location(lat, lon);
                streetMap.addVertex(name, loc);
            }
            //get edge elements
            NodeList edgeList = doc.getElementsByTagName("way");
            length = edgeList.getLength();
            double distance;
            String point1; String point2; String type;
            // populate edges in streetMap
            for (int i = 0; i < length; i++) {
                type = null; name = null; point2 = null;
                e = (Element) edgeList.item(i);
                NodeList children = e.getChildNodes();
                NamedNodeMap pointList; NamedNodeMap nameList;
                //parse for name and road type
                for (int j = 0; j < children.getLength(); j++) {
                    if (children.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        if (children.item(j).getNodeName().equals("tag")) {
                            nameList = children.item(j).getAttributes();
                            //add road type and road name if available
                            if (nameList.item(0).getTextContent().equals("highway") || (nameList.item(0).getTextContent().equals("oneway") && nameList.item(1).getTextContent().equals("yes"))) {
                                type = nameList.item(0).getTextContent();
                            } else if (nameList.item(0).getTextContent().equals("name")) {
                                name = nameList.item(1).getTextContent();
                            }
                        }
                    }
                }
                //check if valid edge
                if (name != null && type != null) {
                    //parse start and end points for edges
                    int childrenLength = children.getLength();
                    for (int j = 0; j < childrenLength; j++) {
                        if (children.item(j).getNodeName().equals("nd")) {
                            pointList = children.item(j).getAttributes();
                            point1 = point2;
                            point2 = pointList.item(0).getTextContent();
                            //add edge and with edge data to streetMap
                            if (point1 != null) {
                                distance = distance(streetMap.getVertex(point1).getVertexData().latitude, streetMap.getVertex(point2).getVertexData().latitude,
                                    streetMap.getVertex(point1).getVertexData().longitude, streetMap.getVertex(point2).getVertexData().longitude);
                                //increment mileage count
                                totalDistance += distance;
                                edgeData eData = new edgeData(name, distance);
                                //default is add one edge, adds a double edge if type is highway
                                streetMap.addEdge(point1, point2, eData);
                                streetMap.getVertex(point1).getVertexData().setOutDegree();
                                if (type.equals("highway")) {
                                    streetMap.addEdge(point2, point1, eData);
                                    totalDistance += distance;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { System.out.println(ex.getMessage()); }
    }

    //Calculates total number of miles of road in Map
    public double TotalDistance() {
        return totalDistance / 2;
    }

    //return closest vertex to location
    public String ClosestRoad(Location loc) {
        String name = "";
        double currentDistance = Double.MAX_VALUE;
        double newDistance;
        // for all the vertices in the graph
        for (IGraph.Vertex<Location> v : streetMap.getVertices()) {
            // calculate the distance between the vertex and the specified location
            newDistance = distance(v.getVertexData().getLatitude(), loc.getLatitude(), v.getVertexData().getLongitude(), loc.getLongitude());
            // if the distance from the vertex to the location is the shortest so far, record the vertex name
            if (currentDistance > newDistance && v.getVertexData().hasOutDegree()) {
                currentDistance = newDistance;
                name = v.getVertexName();
            }
        }
        //return the name of the vertex with the shortest distance to the location
        return name;
    }

    //returns shortest route from one location to another
    public List<String> ShortestRoute(Location fromLocation, Location toLocation) {
        //returns closest vertex to location
        String startID = ClosestRoad(fromLocation);
        String endID = ClosestRoad(toLocation);
        List<String> route = new ArrayList<>();
        //use Dijkstra's on graph to return edges of shortest route
        List<IGraph.Edge<edgeData>> edges = GraphAlgorithms.ShortestPath(streetMap, startID, endID);
        //add vertex ids in shortest route to list
        route.add(edges.get(0).getVertexName1());
        for (IGraph.Edge<edgeData> e : edges) {
            route.add(e.getVertexName2());
        }
        //return list of vertex ids in shortest route
        return route;
    }

    //returns list of street names from list of vertex names
    public List<String> StreetRoute(List<String> vertices) {
        List<String> streetList = new ArrayList<>();
        String previousStreet = "";
        String vertex1 = null;
        String streetName;
        // iterate through input list of vertices
        for (String vertex2 : vertices) {
            // add edges from graph that correspond to vertices
            if (vertex1 != null) {
                streetName = streetMap.getEdge(vertex1, vertex2).getEdgeData().name;
                //check for redundant street names
                if (!(previousStreet.equals(streetName))) {
                    streetList.add(streetName);
                    previousStreet = streetName;
                }
            }
            vertex1 = vertex2;
        }
        //return list of street names
        return streetList;
    }

    // loads map from XML file specified as argument and returns total distance in miles of streets
    public static void main2(String[] args) {
        OSMMap map = new OSMMap();
        // can be directly modified to filepath if wanted, default is function argument
        map.LoadMap(args[0]);
        //print out total Ames road distance in miles
        System.out.println(map.TotalDistance());
    }

    // Reads location data from file and outputs sequence of street names forming the shortest route between locations
    public static void main3(String[] args) {
        // load files from function arguments
        try {
        OSMMap map = new OSMMap();
        List<String> route = new ArrayList<>();
        List<Location> locations = new ArrayList<>();
        String longitude; String latitude;
        map.LoadMap(args[0]);
        File locationData = new File(args[1]);
        Scanner scanner = new Scanner(locationData);
        // scan longitudes and latitudes from specified file and store as locations
        while (scanner.hasNext()) {
            latitude = scanner.next();
            longitude = scanner.next();
            locations.add(new Location(Double.parseDouble(latitude), Double.parseDouble(longitude)));
        }
        // find shortest route between each location and add it to the route list
        List<String> vertexList;
        List<String> edgeList;
        for (int i = 1; i < locations.size(); i++) {
            vertexList = map.ShortestRoute(locations.get(i - 1), locations.get(i));
            edgeList = map.StreetRoute(vertexList);
            route.addAll(edgeList);
        }
        // output the shortest route list without duplicates
        String previousStreet = "";
        for (String s : route) {
            if (!(previousStreet.equals(s))) {
                System.out.println(s);
                previousStreet = s;
            }
        }
        //exception handling for bad file path
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
