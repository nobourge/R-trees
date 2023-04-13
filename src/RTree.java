
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.MultiPolygon;

import org.geotools.geometry.jts.ReferencedEnvelope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Node root;

    private int maxDepth;
    private int minDepth;
    private int depth;
    private int size;
    private int maxChildren;
    private int minChildren;
    private int leafSize;
    private int nodeSize;
    private int maxLeafSize;
    private int minLeafSize;
    private int maxNodeSize;
    private int minNodeSize;
    private int maxTreeSize;
    private int minTreeSize;
    private int mode;


    //main constructor
    public RTree(int maxChildren, int minChildren, int maxDepth, int minDepth) {
        logger.debug("RTree()");
        this.maxDepth = maxDepth;
        this.minDepth = minDepth;
        this.root = new Node();
        this.depth = 0;
        this.size = 0;
        this.leafSize = 0;
        this.nodeSize = 0;
        this.maxLeafSize = 0;
        this.minLeafSize = 0;
        this.maxNodeSize = 0;
        this.minNodeSize = 0;
        this.maxTreeSize = 0;
        this.minTreeSize = 0;

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

    public Node addLeaf(Node rnode, String label, Polygon polygon) {
        logger.debug("addLeaf()");
        if (rnode.getChildren().size() == 0 || rnode.getChildren().get(0) instanceof RLeaf) {
            rnode.getChildren().add(new RLeaf(polygon, label));
        } else {
            RNode node = chooseNode(rnode, polygon);
            Node new_node = addLeaf(node, label, polygon);
            if (new_node != null) {
                rnode.getChildren().add(new_node);
//                rnode.updateMBR(polygon);
            }
            if (rnode.getChildren().size() >= maxChildren) {
                return splitQuadratic(node);
//                return splitLinear(rnode);
            } else {
                return null;
            }
        }
        return rnode;
    }

    private RNode splitQuadratic(RNode rnode) {
        return null;
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
        return null;
    }

    private RNode chooseNode(Node rnode, Polygon polygon){
        // todo
        return null;
    }
    private RNode chooseLeaf(RNode rnode, RLeaf leaf) {
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
        // Set
        //N to be the child node pointed to by
        //F.p and repeat from CL2


        return null;
    }


    private void splitNode(int mode, RNode node) {
        // todo
    }


    public Node getRoot() {
        return root;
    }
}
