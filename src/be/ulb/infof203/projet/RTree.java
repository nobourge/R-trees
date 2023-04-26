package be.ulb.infof203.projet;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.GeometryBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RTree {

    private static final Logger logger = LoggerFactory.getLogger(RTree.class);
    private RNode root; // enonce: des nœuds (dont une racine)
    // as every leaf is at the same level, the tree depth is the same as the tree height & the leaf depth
    private final int maxDepth;
    private final int minDepth;
    private int depth;
    private final int maxChildren;
    private final int minChildren;
    private int leafQuantity;
    private int rNodeQuantity;
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
    }

    public int getRNodeQuantity() {
        return rNodeQuantity;
    }

    public int getLeafQuantity() {
        return leafQuantity;
    }

    public int getNodeQuantity() {
        return leafQuantity + rNodeQuantity;
    }

    public int getDepth() {
        return depth;
    }


    public RNode split(RNode node, String mode) {
        if (mode.equals("quadratic")) {
            return splitQuadratic(node);
        }
        else if (mode.equals("linear")) {
            return splitLinear(node);
        }
        else {
            logger.error("Unknown mode: " + mode);
            return null;
        }
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

    public RNode addLeaf(RNode rnode
            , String label
            , MultiPolygon polygon
            , String mode
                        ) {
        logger.debug("addLeaf()");
        logger.debug("rnode: " + rnode);
        logger.debug("label: " + label);
        if (rnode.getChildren().isEmpty() || rnode.getChildren().get(0).isLeaf()){
            logger.debug("bottom level is reached -> create leaf");
            rnode.addChild(new RLeaf(polygon, label));
            leafQuantity++;
            logger.debug("leafQuantity: " + leafQuantity);
        }
        else {
            logger.debug("still need to go deeper");
            RNode node = chooseNode(rnode, polygon);
            RNode newNode = addLeaf(node, label, polygon, mode);
            if (newNode != null) {
                // a split occurred in addLeaf, a new node is added at this level
                rnode.getChildren().add(newNode);
                rNodeQuantity++;
                logger.debug("rNodeQuantity: " + rNodeQuantity);
            }
        }
        rnode.getMBR().expandToInclude(polygon.getEnvelopeInternal());
        if (rnode.getChildren().size() >= maxChildren) {
            logger.info("splitting node");
            return split(rnode, mode);
        } else {
            return null;
        }
    }

    private RNode splitQuadratic(RNode rnode) {
        logger.debug("splitQuadratic()");
        List<Node> children = rnode.getChildren();
        int numChildren = children.size();
        if (numChildren <= 1) {
            return null;
        }
        // Dans cette version, on commence par choisir deux « seeds » (pickSeeds),
        // soit deux nœuds les plus éloignés possible.
        Node[] seeds = pickSeeds(children);

        RNode node1 = new RNode(Collections.singletonList(seeds[0]));
        RNode node2 = new RNode(Collections.singletonList(seeds[1]));

        children.remove(seeds[0]);
        children.remove(seeds[1]);

        if (children.isEmpty()) {
            return null;
        }

        // todo: check warnings
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

        Node parent = rnode.getParent();

        if (parent == null) {
            parent = new RNode(new ArrayList<>());
            ((RNode) parent).addChild(node1);
            ((RNode) parent).addChild(node2);
            return (RNode) parent;
        } else {
            parent.removeChild(rnode);
            parent.addChild(node1);
            parent.addChild(node2);
            if (parent.getChildren().size() > maxChildren) {
                splitQuadratic((RNode) parent);
            }
            return null;
        }
    }

    private Node[] pickSeeds(List<Node> children) {
        // Pour ce faire, on considère chaque paire de nœuds.
        // Pour chacune de ces paires,
        // on calcule la superficie de l’enveloppe englobant les deux enve-loppes,
        // moins celles des enveloppes elles-mêmes.
        // On sélectionnera la paire qui maximise cette superficie.
        // returns two nodes that will be used as the initial seeds for the split
        logger.debug("pickSeeds()");
        Node[] seeds =new Node[2];
        double maxDistance = 0.0;
        for (int i = 0; i < children.size(); i++) {
            Envelope childiEnvelope = children.get(i).getMBR();
            double childiEnvelopeArea = childiEnvelope.getArea();
            for (int j = i + 1 ; j < children.size(); j++) {
                Envelope childiEnvelopeCopy = new Envelope(childiEnvelope);

                Envelope childjEnvelope = children.get(j).getMBR();
                childiEnvelopeCopy.expandToInclude(childjEnvelope);

                double distance = childiEnvelopeCopy.getArea() - childiEnvelopeArea - childjEnvelope.getArea();
                if (distance > maxDistance) {
                    logger.debug("i: " + i);
                    logger.debug("j: " + j);
                    logger.debug("distance: " + distance);
                    maxDistance = distance;
                    seeds[0] = children.get(i);
                    seeds[1] = children.get(j);
                }
            }
        }
        logger.debug("seeds[0]: " + seeds[0]);
        logger.debug("seeds[1]: " + seeds[1]);
        return seeds;
    }

    private  double quadraticCost(RNode node, List<Node> children) {
        logger.debug("quadraticCost()");
        Envelope mbr = node.getMBR();
        double area = mbr.getArea();

        Envelope union = new Envelope(mbr);
        for (Node child : children) {
            union.expandToInclude(child.getMBR());
        }
        double enlargedArea = union.getArea();
        return enlargedArea - area;
    }

    private RNode splitLinear(Node rnode) {
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

    private RNode chooseNode(Node rnode, MultiPolygon polygon){
        //  identifier le nœud
        //  pour lequel l’insertion du nouveau polygone
        //  minimisera l’augmentation du MBR

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
        logger.debug("chooseNode()");

        Envelope toInsertEnvelope = polygon.getEnvelopeInternal();
        if (rnode.getChildren().isEmpty() || rnode.getChildren().get(0) instanceof RLeaf) {
            return (RNode) rnode;
        }
        double minEnlargement = Double.POSITIVE_INFINITY;
        RNode result = null;
        for (Node childNode : rnode.getChildren()) {

            Envelope childNodeEnvelope = childNode.getMBR();
            MultiPolygon childNodePolygon = childNode.getPolygon();
            if (childNodePolygon.contains(polygon)) {
                return (RNode) childNode;
            } else {
                // deepcopy of childNodeEnvelope:
                Envelope childNodeEnvelopeExpanded = new Envelope(childNodeEnvelope);
                childNodeEnvelopeExpanded.expandToInclude(toInsertEnvelope);
                double childNodeEnvelopeArea = childNodeEnvelope.getArea();
                double childNodeEnvelopeAreaEnlarged = childNodeEnvelopeExpanded.getArea();
                double enlargement = childNodeEnvelopeAreaEnlarged - childNodeEnvelopeArea;
                if (enlargement < minEnlargement) {
                    minEnlargement = enlargement;
                    result = (RNode) childNode;
                } else if (enlargement == minEnlargement
                        && result != null
                        && (childNodeEnvelope.intersection(toInsertEnvelope).getArea()
                        < result.getMBR().intersection(toInsertEnvelope).getArea())) {
                        result = (RNode) childNode;

                }
            }
        }

        return result;
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
                                        , int depth
                                        ) {

        logger.debug("search() recursive");
        if (node.isLeaf()) {
            logger.debug("in RLeaf");
                if (node.getPolygon().contains(point)) {
                    logger.debug("RLeaf contains point");
                    result.add((RLeaf) node);
                }
                return result;
        } else {
            logger.debug("in RNode");
            logger.debug("search() recursive depth: " + depth);
            logger.debug("children quantity: " + node.getChildren().size());
                for (Node child : node.getChildren()) {
                    if (child.getMBR().contains(point.getX(), point.getY())) {
                        searchRecursive(point, child, result, depth++);
                    }
                }
            }
        return result;
    }

    // build tree
    public void addFeatureCollection(SimpleFeatureCollection allFeatures
            , String mode
            , SimpleFeatureSource featureSource) {
        logger.debug("addFeatureCollection()");


        try ( SimpleFeatureIterator iterator = allFeatures.features() ){
            while( iterator.hasNext()){
                SimpleFeature feature = iterator.next();
                display(feature, featureSource);
                // pause 5 seconds:
                Thread.sleep(500);
                // feature has attribute MultiPolygon
                MultiPolygon polygon = (MultiPolygon) feature.getDefaultGeometry(); // getDefaultGeometry returns a Geometry Object
//                String id = Objects.toString(feature.getAttribute("id"));
//                String id = Objects.toString(feature.getAttribute("STRING"));
//                String id = Objects.toString(feature.getAttribute.getID());
                String id = Objects.toString(feature.getID());
                logger.debug("id: " + id);
                addLeaf(root
                        , id
                        , polygon
                        , mode);
                RTreeDisplayer.displayTerminal(root, 0);
                RTreeDisplayer.display(this);

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static SimpleFeatureCollection getSimpleFeatureCollection(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists())
            throw new FileNotFoundException("Shapefile does not exist.");
        // create a map content and add our shapefile to it
        FileDataStore store = FileDataStoreFinder.getDataStore(file); // store is a ShapefileDataStore
        //
        SimpleFeatureSource featureSource = store.getFeatureSource(); // featureSource is a ShapefileFeatureSource

        SimpleFeatureCollection allFeatures = featureSource.getFeatures();

        store.dispose(); // close the store

        ReferencedEnvelope globalBounds = featureSource.getBounds();
        logger.info("Global bounds: "+globalBounds);

        return allFeatures;
    }

    public void display(SimpleFeature target, SimpleFeatureSource featureSource) {
        GeometryBuilder gb = new GeometryBuilder();

        MapContent map = new MapContent();
        map.setTitle("Projet INFO-F203");

        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        Layer layer = new FeatureLayer(featureSource, style);
        map.addLayer(layer);

        ListFeatureCollection collection = new ListFeatureCollection(featureSource.getSchema());
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureSource.getSchema());


        // Add target polygon
        collection.add(target);
        // Add MBR
        if (target !=null) {
            featureBuilder.add(gb.box(target.getBounds().getMinX(),
                    target.getBounds().getMinY(),
                    target.getBounds().getMaxX(),
                    target.getBounds().getMaxY()
            ));
            collection.add(featureBuilder.buildFeature(null));
        }
        Style style2 = SLD.createLineStyle(Color.red, 2.0f);
        Layer layer2 = new FeatureLayer(collection, style2);
        map.addLayer(layer2);
        // Now display the map
        JMapFrame.showMap(map);
    }

}