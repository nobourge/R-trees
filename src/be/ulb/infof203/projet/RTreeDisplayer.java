package be.ulb.infof203.projet;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RTreeDisplayer {
    // display the tree representation

    private final static Logger logger = LoggerFactory.getLogger(RTreeDisplayer.class);

    private static int rootX;
    public static void displayTree(RNode root) {
        logger.debug("displayTree()");
        logger.info("Tree representation:");
        displayTerminal(root, 0);
    }

    public static void displayTerminal(Node node, int level) {
        // disable logger :

        if (level == 0) {
            logger.debug("displayTerminal()");
            System.out.println("Tree representation:");
            // Disable logger for displayTerminal method
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            ch.qos.logback.classic.Logger displayLogger = loggerContext.getLogger(RTreeDisplayer.class);
            displayLogger.setLevel(Level.OFF);

        }
        if (node == null) {
            return;
        }
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
        System.out.println(node.showInfo());
        if (!node.isLeaf()) {
            for (Node child : node.getChildren()) {
                displayTerminal(child, level + 1);
            }
        }
    }

    public static void display(RTree tree) {
        // get the list of strings that represent the tree
        logger.debug("display()");
        // define the root position at the center of the screen:
        rootX = tree.getNodeQuantity();
        List<String> lines = displayAux(tree.getRoot());
        for (String line : lines) {

            System.out.println("-----------------");
            System.out.println(line);
        }
    }

    private static List<String> displayAux(Node node) {
        // get the list of strings that represent the tree
        logger.debug("displayAux()");
        List<String> lines = new ArrayList<String>();


        if (node.isLeaf()) {
            String nodeLine = node.getInfo(); // get string representation of the node
            lines.add(nodeLine);
            return lines;
        }

        List<Node> childNodes = node.getChildren(); // get list of child nodes
        int numChildNodes = childNodes.size();

        // call _display_aux() recursively on each child node
        List<List<String>> childNodeLines = new ArrayList<List<String>>();
        List<Integer> childNodeWidths = new ArrayList<Integer>();
        List<Integer> childNodeHeights = new ArrayList<Integer>();
        List<Integer> childNodeRootXs = new ArrayList<Integer>();
        for (Node childNode : childNodes) {
            List<String> childLines = displayAux(childNode);
            childNodeLines.add(childLines);
            childNodeWidths.add(childLines.get(0).length());
            childNodeHeights.add(childLines.size());
            childNodeRootXs.add(rootX); // getRootX() is a method of Node which returns the x coordinate re
        }

        int maxHeight = Collections.max(childNodeHeights);
        for (int i = 0; i < maxHeight; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < numChildNodes; j++) {
                List<String> childLines = childNodeLines.get(j);
                int childNodeWidth = childNodeWidths.get(j);
                int childNodeHeight = childNodeHeights.get(j);
                int childNodeRootX = childNodeRootXs.get(j);

                if (i < childNodeHeight) {
                    String childLine = childLines.get(i);
                    int numSpaces = childNodeRootX - sb.length();
                    if (numSpaces > 0) {
                        sb.append(String.join("", Collections.nCopies(numSpaces, " ")));
                    }
                    sb.append(childLine);
                } else {
                    int numSpaces = childNodeWidth + childNodeRootX - sb.length();
                    sb.append(String.join("", Collections.nCopies(numSpaces, " ")));
                }
            }
            lines.add(sb.toString());
        }

        return lines;
    }
}