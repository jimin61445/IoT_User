package com.example.myapplication;

import android.graphics.Path;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;

public class astar {

    private PriorityQueue<Node> openList;
    private ArrayList<Node> closedList;
    HashMap<Node, Double> gMaps;
    HashMap<Node, Double> fMaps;
    private int initialCapacity = 100000;
    public static Node[] n = new Node[100];
    public static Node[] m = new Node[100];
    public static Node start;
    public static Node end;
    public static Node next;
    public static double fVal=0;
    public static Stack<Node> list = new Stack<>();
    public static Stack<Node> finalList = new Stack<>();
    public astar() {
        gMaps = new HashMap<Node, Double>();
        fMaps = new HashMap<Node, Double>();
        openList = new PriorityQueue<Node>(initialCapacity, new fCompare());
        closedList = new ArrayList<Node>();
    }

    public static Stack<Node> astarMain() {


        if(start.getFloor()!=end.getFloor()) {
            int stair = new astar().findBestStair(start,end);
            if(start.getFloor()==4) {
                new astar().search(m[stair-3], end);
                new astar().search(start, n[stair]);
            }
            else {
                new astar().search(start, m[stair]);
                new astar().search(n[stair+3], end);
            }
        }
        else {
            new astar().search(start, end);
        }


        return list;
    }


    public void search(Node start, Node end) {
        openList.clear();
        closedList.clear();

        gMaps.put(start, (double) 0);
        openList.add(start);


        while (!openList.isEmpty()) {
            Node current = openList.element();
            if (current.equals(end)) {
                printPath(current,end);
                return;
            }

            closedList.add(openList.poll());
            ArrayList<Node> neighbors = current.getNeighbors();

            for (Node neighbor : neighbors) {
                double gScore = gMaps.get(current)+h(current,neighbor);
                double fScore = gScore + h(neighbor, end);
                if (closedList.contains(neighbor)) {

                    if (gMaps.get(neighbor) == null) {
                        gMaps.put(neighbor, gScore);
                    }
                    if (fMaps.get(neighbor) == null) {
                        fMaps.put(neighbor, fScore);
                    }

                    if (fScore >= fMaps.get(neighbor)) {
                        continue;
                    }
                }
                if(neighbor!=end&&neighbor.getNeighbors().size()==1) {
                    closedList.add(neighbor);
                    continue;
                }
                if (!openList.contains(neighbor) ||fScore < fMaps.get(neighbor)) {
                    if(current.getParent()!=neighbor) {
                        neighbor.setParent(current);
                        if(current==start){
                            next = neighbor;
                        }
                    }
                    gMaps.put(neighbor, gScore);
                    fMaps.put(neighbor, fScore);
                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);

                    }
                }
            }
        }
        System.out.println("FAIL");
    }

    private double h(Node node, Node goal) {

        double x = node.getX() - goal.getX();
        double y = node.getY() - goal.getY();
        return  x * x + y * y;
    }

    private void printPath(Node node,Node end) {
        list.push(node);
        while (node.getParent() != null) {
            fVal = fVal + Math.sqrt(h(node, node.getParent()));

            node = node.getParent();
            list.push(node);

        }

    }
    public int findBestStair(Node start,Node end) {
        double shortest=100000;
        Node shortestNode = null;
        double tempLeng;
        Node [] array = new Node[8];
        for(int i=0;i<8;i++) {
            array[i]=new Node();
            array[i].setData(i);
        }
        array[0].setXY(25.5,58.6);
        array[1].setXY(3.4,11.9);
        array[2].setXY(3.4,55.3);
        array[3].setXY(27.2,17.8);
        array[4].setXY(15.1,93.6);
        array[5].setXY(10.9,17.8);
        array[6].setXY(12.2,70);
        array[7].setXY(10.9,95.3);

        for(int i=0;i<8;i++) {
            tempLeng= h(start,array[i])+h(array[i],end);
            if(tempLeng<shortest) {
                shortest=tempLeng;
                shortestNode=array[i];
            }
        }
        if(start.getFloor()==4) {
            return (int)shortestNode.getData()+36;
        }
        else {
            return (int)shortestNode.getData()+33;
        }

    }

    public Stack<Node> getList(){
        return list;
    }

    class fCompare implements Comparator<Node> {

        public int compare(Node o1, Node o2) {


            if (fMaps.get(o1) < fMaps.get(o2)) {
                return -1;
            } else if (fMaps.get(o1) > fMaps.get(o2)) {
                return 1;
            } else {
                return 1;
            }
        }
    }
}

class Node {

    private Node parent;
    private ArrayList<Node> neighbors;
    private double x;
    private double y;
    private Object data;
    private int floor;
    private boolean stair=false;

    public Node() {
        neighbors = new ArrayList<Node>();
        data = new Object();
    }

    public Node(int x, int y) {
        this();
        this.x = x;
        this.y = y;
    }

    public Node(Node parent) {
        this();
        this.parent = parent;
    }

    public Node(Node parent, int x, int y) {
        this();
        this.parent = parent;
        this.x = x;
        this.y = y;
    }

    public ArrayList<Node> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(ArrayList<Node> neighbors) {
        this.neighbors = neighbors;
    }

    public void addneighbor(Node node) {
        this.neighbors.add(node);
    }

    public void addneighbors(Node... node) {
        this.neighbors.addAll(Arrays.asList(node));
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean equals(Node n) {
        return this.x == n.x && this.y == n.y;
    }
    public void setFloor(int floor) {
        this.floor=floor;
    }
    public int getFloor() {
        return this.floor;
    }
    public void setStair() {
        this.stair=true;
    }
    public boolean getStair() {
        return this.stair;
    }
}