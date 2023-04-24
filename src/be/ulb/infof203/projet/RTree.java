package be.ulb.infof203.projet;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.DirectPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.geotools.geometry.jts.JTS.toEnvelope;

public class RTree {
    // (1) Every leaf node contains between m
    //and M index records unless it is the
    //root.

    //(2) For each index record
    //(I, tuple-identifier) in a leaf node, I is
    //the smallest rectangle that spatially
    //contains the n-dimensional data object
    //represented by the indicated tuple.

    //(3) Every non-leaf node has between m
    //and M children unless it is the root.

    //(4) For each entry (/, child —pointer ) in a
    //non-leaf node,
    // I is the smallest rectangle that spatially contains
    // the rectangles in the child node.

    //(5) The root node has at least two children
    //unless it is a leaf.

    //(6) All leaves appear on the same level.

    private static final Logger logger = LoggerFactory.getLogger(RTree.class);
    private final Node root;

    // as every leaf is at the same level, the tree depth is the same as the tree height & the leaf depth

    private final int maxDepth;
    private final int minDepth;
    private int depth;
    private int size;
    private final int maxChildren;
    private final int minChildren;
    private int leafQuantity;
    private int rNodeQuantity;

    private int mode;


    //main constructor
    public RTree(int maxChildren, int minChildren, int maxDepth, int minDepth) {
        logger.debug("RTree()");
        this.maxDepth = maxDepth;
        this.minDepth = minDepth;
        this.maxChildren = maxChildren;
        this.minChildren = minChildren;
        this.root = new RNode();
        leafQuantity = 0;
        rNodeQuantity = 0;
        this.depth = 0;
        this.size = 0;

    }

    //insertion (addLeaf) : si, après ajout d’une nouvelle feuille, le nombre de feuilles
    //composant un nœud atteint un seuil N fixé, ce nœud sera « coupé en deux » (split),
    //selon une méthode décrite plus bas. Cette division augmentera dès lors de 1 le
    //nombre de sous-nœuds du niveau supérieur. Si besoin, la division sera également
    //réalisée à ce niveau-là, en remontant ainsi jusqu’à la racine

    // addLeaf ( node , label , polygon ):
        //if size ( node . subnodes )==0 or node. subnodes [0] is a leaf :
            //# bottom level is reached -> create leaf
            //node . subnodes . add ( new Leaf ( name , polygon ))
        //else :# still need to go deeper
            //n = chooseNode ( node , polygon )
            //new_node = addLeaf (n , label , polygon )
            //if new_node != null :
                //# a split occurred in addLeaf ,
                //# a new node is added at this level
                //node.subnodes . add ( new_node )
                //expand node . mbr to include polygon
                //if size ( node . subnodes ) >= N :
                //return split ( node )
            //else :
                //return null

    public Node addLeaf(Node rnode
            , String label
            , Polygon polygon
            , String mode
                        ) {
        logger.debug("addLeaf()");
        if (rnode.getChildren() == null ||rnode.getChildren().size()==0 || rnode.getChildren().get(0) instanceof RLeaf) {
            // bottom level is reached -> create leaf
            rnode.getChildren().add(new RLeaf(polygon, label));
            leafQuantity++;
            logger.debug("leafQuantity: " + leafQuantity);
        } else {
            RNode node = chooseNode(rnode, polygon);
            Node new_node = addLeaf(node, label, polygon, mode);
            if (new_node != null) {
                rnode.getChildren().add(new_node);
//                rnode.updateMBR(polygon);
                rNodeQuantity++;
                logger.debug("rNodeQuantity: " + rNodeQuantity);
            }
            if (rnode.getChildren().size() >= maxChildren) {
                if (mode.equals("quadratic")) {
                    return splitQuadratic(node);

                } else if (mode.equals("linear")) {
                    return splitLinear(node);

                } else {
                    logger.error("Unknown mode: " + mode);
                }
            } else {
                return null;
            }
        }
        return rnode;
    }

    private RNode splitQuadratic(RNode rnode) {
        logger.debug("splitQuadratic()");

        List<Node> children = rnode.getChildren();
        int numChildren = children.size();

        if (numChildren <= 1) {
            return null;
        }

        Node[] seeds = pickSeeds(children);

        RNode node1 = new RNode(Collections.singletonList(seeds[0]));
        RNode node2 = new RNode(Collections.singletonList(seeds[1]));

        children.remove(seeds[0]);
        children.remove(seeds[1]);

        while (!children.isEmpty()) {
            if (node1.getChildren().size() + children.size() == minChildren) {
                for (Node child : children) {
                    node1.addChild(child);
                }
                children.clear();
                break;
            } else if (node2.getChildren().size() + children.size() == minChildren) {
                for (Node child : children) {
                    node2.addChild(child);
                }
                children.clear();
                break;
            }

            double cost1 = quadraticCost(node1, children);
            double cost2 = quadraticCost(node2, children);

            if (cost1 < cost2) {
                node1.addChild(children.remove(0));
            } else {
                node2.addChild(children.remove(0));
            }
        }

        node1.updateMBR();
        node2.updateMBR();

        RNode parent = rnode.getParent();

        if (parent == null) {
            parent = new RNode(new ArrayList<>());
            parent.addChild(node1);
            parent.addChild(node2);
            return parent;
        } else {
            parent.removeChild(rnode);
            parent.addChild(node1);
            parent.addChild(node2);
            if (parent.getChildren().size() > maxChildren) {
                splitNode(mode, parent);
            }
            return null;
        }
    }

    private Node[] pickSeeds(List<Node> children) {
        logger.debug("pickSeeds()");
        Node[] seeds =new Node[2];
        double maxDistance = 0.0;
        for (int i = 0; i < children.size(); i++) {
            for (int j = i + 1 ; j < children.size(); j++) {
                double distance = children.get(i).getMBR().distance(children.get(j).getMBR());
                if (distance > maxDistance) {
                    maxDistance = distance;
                    seeds[0] = children.get(i);
                    seeds[1] = children.get(j);
                }
            }
        }
        return seeds;
    }

    private  double quadraticCost(RNode node, List<Node> children) {
        logger.debug("quadraticCost()");
        ReferencedEnvelope mbr = node.getMBR();
        double area = mbr.getArea();

        ReferencedEnvelope union = mbr;
        for (Node child : children) {
            union.expandToInclude(child.getMBR());
        }
        double enlargedArea = union.getArea();
        return enlargedArea - area;
    }

    private RNode splitLinear(RNode rnode) {
        // Node Splitting
        //In order to add a new entry to a full
        //node containing M entries, it is necessary
        //to divide the collection of M+1 entries
        //between two nodes. The division should be
        //done in a way that makes it as unlikely as
        //possible that both new nodes will need to
        //be examined bn subsequent searches.

        //Since the decision whether to visit a node
        //depends on whether its covering rectangle
        //overlaps the search area, the total area of
        //the two covering rectangles after a split
        //should be minimized.
        // The area of the covering rectangles in the “bad split" case is much
        //larger than in the “good split" case.

        //The same criterion was used in procedure ChooseLeaf to decide where to
        //insert a new index entry: at each level in
        //the tree, the subtree chosen was the one
        //whose covering rectangle would have to be
        //enlarged least.
        //We now turn to algorithms for tioning the set of M+1 entries into two
        //groups, one for each new node.
        logger.debug("splitLinear()");
        int numChildren = rnode.getChildren().size();
        int midIndex = numChildren / 2;

        List<Node> newChildren = new ArrayList<>();
        for (int i = midIndex; i < numChildren; i++){
            newChildren.add(rnode.getChildren().get(i));
        }
        rnode.getChildren().subList(midIndex, numChildren).clear();
        RNode newNode = new RNode(newChildren);

        for (int i=0; i < rnode.getChildren().size();i++){
            rnode.getMBR().expandToInclude(rnode.getChildren().get(i).getMBR());
        }
        for (int i=0; i < newNode.getChildren().size(); i++){
            newNode.getMBR().expandToInclude(newNode.getChildren().get(i).getMBR());
        }
    return newNode;
    }

    private RNode chooseNode(Node rnode, Polygon polygon){
//    private RNode chooseNode(Node rnode, MultiPolygon polygon){
//    private RNode chooseNode(Node rnode, ReferencedEnvelope ToInsertEnvelope){
        logger.debug("chooseNode()");
        double minArea = Double.POSITIVE_INFINITY;
        RNode result = null;
        for (Node childNode : rnode.getChildren()) {
            ReferencedEnvelope childNodeEnvelope = childNode.getMBR();
            ReferencedEnvelope ToInsertEnvelope = new ReferencedEnvelope(toEnvelope(polygon));
//            Polygon childNodePolygon = convertToPolygon(childNodeEnvelope);   // fixme: convertToPolygon ?
            Polygon childNodePolygon = childNode.getPolygon();
            if (childNodePolygon.contains(polygon)) {
                return (RNode) childNode;
            } else {
//                double area = childNodeEnvelope.intersection(convertToEnvelope(polygon)).getArea();  // fixme: convertToEnvelope ?
                double area = childNodeEnvelope.intersection(ToInsertEnvelope).getArea();
                if (area < minArea) {
                    minArea = area;
                    result = (RNode) childNode;
                }
            }
        }
        return result;
    }
    private RLeaf chooseLeaf(RNode rnode, RLeaf leaf) {
        // Algorithm ChooseLeaf.
        // Select a leaf node
        //in which to place a new index entry E.
        //CLl. [Initialize.]
        // Set N to be the root
        //node.
        //CL2. [Leaf check.]
        // If N is a leaf, return N.
        //CL3. [Choose subtree.]
        // If Af is not a leaf,
        //let F be the entry in N whose rectangle F.I needs least enlargement to
        //include E.I. Resolve ties by choosing
        //the entry with the rectangle of smallest area.

        //CL4. [Descend until a leaf is reached.]
        // Set N to be the child node pointed to by F.p
        // and repeat from CL2

        logger.debug("chooseLeaf()");
        double minEnlargement = Double.POSITIVE_INFINITY;
        RLeaf result = null;
        for (Node childNode : rnode.getChildren()) {
            ReferencedEnvelope childNodeEnvelope = childNode.getMBR();
            double childNodeEnvelopeArea = childNodeEnvelope.getArea();
//            ReferencedEnvelope expandedEnvelope = nodeEnvelope.expandToInclude(leaf.getMBR());
            childNodeEnvelope.expandToInclude(leaf.getMBR());
            double enlargement = childNodeEnvelope.getArea() - childNodeEnvelopeArea;
            if (enlargement < minEnlargement) {
                minEnlargement = enlargement;
                result = (RLeaf) childNode;
            } else if (enlargement == minEnlargement && result != null) {
                if (childNodeEnvelope.intersection(leaf.getMBR()).getArea() < result.getMBR().intersection(leaf.getMBR()).getArea()) {
                    result = (RLeaf) childNode;
                }
            }
        }
        return result;
    }


//  todo: see ReferencedEnvelope.expandToInclude

//    public ReferencedEnvelope expandToInclude(ReferencedEnvelope other) {
//        if (other == null) {
//            return new ReferencedEnvelope(this);
//        }
//
//        double xmin = Math.min(this.getMinX(), other.getMinX());
//        double ymin = Math.min(this.getMinY(), other.getMinY());
//        double xmax = Math.max(this.getMaxX(), other.getMaxX());
//        double ymax = Math.max(this.getMaxY(), other.getMaxY());
//        return new ReferencedEnvelope(xmin, xmax, ymin, ymax, this.getCoordinateReferenceSystem());
//    }

    private void splitNode(int mode, RNode node) {
        // todo
    }


    public Node getRoot() {
        return root;
    }

    public List<RLeaf> search(Point point) {
        logger.debug("search()");
        List<RLeaf> result = new ArrayList<>();
        result = searchRecursive(point
                , root
                , result
                , 0);
        if (result.isEmpty()) {
            logger.debug("search() result is empty");
        }
        return result;
    }

    public List<RLeaf> searchRecursive(Point point
            , Node node
            , List<RLeaf> result
    , int depth) {
        /*if (node instanceof RLeaf) {
            RLeaf leaf = (RLeaf) node;
            if (leaf.getPolygon().contains(point)) {
                result.add(leaf);
            }
        } else {
            RNode rnode = (RNode) node;
            for (Node child : rnode.getChildren()) {
                // Ici, le DirectPosition c'est intellij qui te met une error si tu le met
                // mais ça ne devrait pas poser de problème
                if (child.getMBR().contains((DirectPosition) point)) {
                    searchRecursive(point, child, result);
                }
            }
        }*/
        logger.debug("search() recursive");
        if (node instanceof RLeaf) {
            logger.debug("RLeaf");
                RLeaf leaf = (RLeaf) node;
                if (leaf.getPolygon().contains(point)) {
                    logger.debug("RLeaf contains point");
                    result.add(leaf);
                    return result;
                }
            } else if (node instanceof RNode) {
            logger.debug("RNode");
            depth++;
            logger.debug("search() recursive depth: " + depth);
                RNode rnode = (RNode) node;
                for (Node child : rnode.getChildren()) {
                    if (child.getMBR().contains((DirectPosition) point)) {
                        searchRecursive(point, child, result, depth);
                    }
                }
            }
        return result;
    }

    // build tree
    public void addFeatureCollection(SimpleFeatureCollection allFeatures, String mode) {
        logger.debug("addFeatureCollection()");


        try ( SimpleFeatureIterator iterator = allFeatures.features() ){
            while( iterator.hasNext()){
                SimpleFeature feature = iterator.next();
                // feature has attribute MultiPolygon
                MultiPolygon multiPolygon = (MultiPolygon) feature.getDefaultGeometry(); // getDefaultGeometry returns a Geometry Object

//                Polygon polygon = (Polygon) feature.getDefaultGeometry(); // Exception in thread "main" java.lang.ClassCastException: class org.locationtech.jts.geom.MultiPolygon cannot be cast to class org.locationtech.jts.geom.Polygon (org.locationtech.jts.geom.MultiPolygon and org.locationtech.jts.geom.Polygon are in unnamed module of loader 'app')
                String id = Objects.toString(feature.getAttribute("id"));

                for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                    Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
                    addLeaf(root
                            , id + "_" + i
                            , polygon
                            , mode);
                }
            }
        }
    }
}
