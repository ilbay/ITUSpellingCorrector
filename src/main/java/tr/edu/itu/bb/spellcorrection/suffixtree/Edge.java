package tr.edu.itu.bb.spellcorrection.suffixtree;

public class Edge {


    private String label;
    private Node destination;

    public Edge(String label, Node destination) {
        this.label = label;
        this.destination = destination;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

}
