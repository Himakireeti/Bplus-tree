/*
COP 5536 Spring 2021 Programming Project - BPlus tree Implementation
Author : Himakireeti Konda
UF Id: 46432937
*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;


public class bplustree {

    int m;
    Node root;
    LeafNode leaf;
    int middle;
    int minimum;

    public bplustree(int m) {
        this.m = m;
        this.middle = (int) Math.ceil((this.m + 1) / 2.0) - 1;
        this.minimum = (int) Math.ceil(this.m / 2.0);
        this.root = null;

    }

    // Creating a class to represent (key, pair)
    public class Pair implements Comparable<Pair> {
        int key; // dictionary key
        double value; // value to be stored with the key

        public Pair(int key, double value) {
            this.key = key;
            this.value = value;
        }

        public int compareTo(Pair p) {
            return (key == p.key ? 0 : (key > p.key ? 1 : -1));
        }

        @Override
        public String toString() {
            return String.format("(" + key + " , " + value + ")");
        }

    }

    /* A class used for conversion between Internal Node and Leaf Node*/
    public class ParentNode {
        Node parent;
    }

    /* Internal node containing key ranges and, sibling and parent pointers */
    class Node extends ParentNode {

        int nodeDegree; //represents the current Degree of the Node
        Node leftSibling; // Left Sibling of the Node
        Node rightSibling; // Right Sibling of the Node

        Integer[] keys; // Array of keys in node
        ParentNode[] children; // Array of Children objects to node

        /*
        Constructor
        @paramerters
        m : degree of the tree we are building
        keys : keys to be in that internal node
         */
        Node(int m, Integer[] keys) {
            this.children = new ParentNode[m + 1];
            this.keys = keys;
            this.nodeDegree = 0;
        }

        /*
        Constructor
        @paramerters
        m : degree of the tree we are building
        keys : keys to be in that internal node
        nodes : children of that node
         */
        Node(int m, Integer[] keys, ParentNode[] nodes) {
            this.nodeDegree = getNullIndex(nodes);
            this.keys = keys;
            this.children = nodes;

        }

        /*
        Adds a given node at the end of the children(objects) array
        @paramerters
        child : child to be appended to the list of children
         */
        void appendChild(ParentNode child) {
            this.children[nodeDegree] = child;
            this.nodeDegree++;
        }

        /*
        inserts child at a particular index in the children array
        @paramerters
        child : child to be added into the list of children
        index : position in the array where the child should be inserted
        */
        void addChild(ParentNode child, int index) {
            int i = nodeDegree - 1;
            while (i >= index) {
                children[i + 1] = children[i];
                i -= 1;
            }
            this.children[index] = child;
            this.nodeDegree += 1;
        }

        /*
        Adds a given node at the begining of the children(objects) array
        @paramerters
        child : child to be prepended to the list of children
         */
        void prependChild(ParentNode child) {
            for (int i = nodeDegree - 1; i >= 0; i--) {
                children[i + 1] = children[i];
            }
            this.children[0] = child;
            this.nodeDegree++;

        }

        /*
        returns the position of the given child in the children array of a particular node
        @paramerters
        child : index of the child in the array list
         */
        int getChildIndex(ParentNode child) {
            int index = -1;
            int i = 0;
            while (i < this.children.length - 1) {
                if (this.children[i] == child) {
                    index = i;
                    break;
                }
                i += 1;
            }
            return index;
        }

        /*
        deletes the key in the keys array of the node at given index
        @parameters
        index : deletes key in the array of keys at the given index
         */
        void deleteKeyAtIndex(int index) {
            int i = index;

            while (i < this.nodeDegree - 2) {
                this.keys[i] = this.keys[i + 1];
                i += 1;
            }
            this.keys[i] = null;
        }

        /*
        deletes child at given index
        index : deletes child at that given index
         */
        void deleteChild(int index) {
            this.children[index] = null;
            this.nodeDegree -= 1;
        }

        /*
        deletes child at given index from the children array
        index : deletes child at that given index
         */
        void deleteChildAtIndex(int index) {
            int i = index;
            while (i < this.nodeDegree - 1) {
                this.children[i] = this.children[i + 1];
                i += 1;
            }
            this.children[i] = null;
            this.nodeDegree -= 1;
        }

        // returns true if the node is deficient
        boolean deficient() {
            return this.nodeDegree < minimum;
        }

        // returns true if node has more than minimum #keys and lend to its siblings
        boolean canLend() {
            return this.nodeDegree > minimum;
        }

        // returns true if node has less than minimum #keys and merges with its siblings
        boolean will_merge() {
            return this.nodeDegree == minimum;
        }

        //returns true if the node reaches its capacity
        boolean full() {
            return this.nodeDegree == m + 1;
        }

    }

    /* Leaf node containing (key , value) pairs and pointers to siblings and parents */
    public class LeafNode extends ParentNode {
        int totalPairs; // #dictionary pairs in leaf Node
        LeafNode leftSibling; // left sibling of the leafnode
        LeafNode rightSibling; // right sibling of the leaf node
        Pair[] pairs; // array of dictionary pairs

        /*
        @parameters:
        m : degree of the leaf Node
        p : (key , value) pair
         */
        public LeafNode(int m, Pair p) {
            this.pairs = new Pair[m];
            this.totalPairs = 0;
            this.insertKey(p);
        }

        /*
        @parameters:
        m : degree of the leaf Node
        p : (key , value) pair
        parent : Internal node to which the leaf node to be added
         */
        public LeafNode(int m, Pair[] pairs, Node parent) {
            this.pairs = pairs;
            this.totalPairs = getNullIndex(pairs);
            this.parent = parent;
        }

        /*
        First checks is it possible to insert a pair into the node
        if possible inserts the dictionary pair into the leaf node and sorts and returns false
        else returns true
        @parameters:
        p : the dictionary pair to be inserted into the leaf Node
         */
        public boolean insertKey(Pair p) {
            if (totalPairs == m - 1) {
                return true;
            } else {
                this.pairs[totalPairs] = p;
                totalPairs += 1;
                Arrays.sort(this.pairs, 0, totalPairs);
                return false;
            }
        }

        /*
        deletes a pair from the leafnode at given index
        index: position at which the pair to be deleted;
         */
        public void deleteKey(int index) {
            this.pairs[index] = null;
            totalPairs -= 1;
        }

        // returns true if the LeafNode is deficient
        public boolean deficient() {
            return totalPairs < ((int) (Math.ceil(m / 2.0) - 1));
        }

        // returns true if the LeafNode can lend its keys to siblings
        public boolean canLend() {
            return totalPairs > ((int) (Math.ceil(m / 2.0) - 1));
        }

        // returns true if the LeafNode can merge with its siblings
        public boolean will_merge() {
            return totalPairs == (int) ((Math.ceil(m / 2.0) - 1));
        }

    }

    /* --------------------- Helper functions ------------------*/
    /*
    return the index where the null values start
    this represents the index where a new pair can be inserted
    @paramaters:
    pairs : an array of pairs
     */
    public int getNullIndex(Pair[] pairs) {
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i] == null) {
                return i;
            }
        }
        return -1;
    }

    /*
    return the index where the null values start
    this represents the index where a new child can be inserted
    @paramaters:
    pairs : an array of pairs
    */
    public int getNullIndex(ParentNode[] nodes) {
        int i = 0;
        while (i < nodes.length) {
            if (nodes[i] == null) {
                return i;
            }
            i += 1;
        }
        return -1;
    }


    /*
    traverses down from the given node to the leaf node in which a key can be inserted
    return the leafNode
    @parameters:
    node : node at which the search should start
    key : the key value from (key, value) pair
     */
    public LeafNode findLeaf(Node node, int key) {
        Integer[] keyList = node.keys;
        int index = 0;

        while (index < node.nodeDegree - 1) {
            if (keyList[index] > key)
                break;

            index += 1;
        }
        ParentNode node_child = node.children[index];

        //If we reach a leaf Node return that leafNode or continue search from childNode
        if (node_child instanceof LeafNode) {
            return (LeafNode) node_child;
        } else {
            return findLeaf((Node) node_child, key);
        }

    }

    /* Sorting dictionary pairs w.r.t pairs using array sort (handling null values)
    pairs: array of pairs to be sorted
     */
    void sortPairs(Pair[] pairs) {
        Arrays.sort(pairs, new Comparator<Pair>() {
            @Override
            public int compare(Pair pair1, Pair pair2) {
                if (pair1 == null && pair2 == null) {
                    return 0;
                }
                if (pair1 == null) {
                    return 1;
                }
                if (pair2 == null) {
                    return -1;
                }
                return pair1.compareTo(pair2);
            }
        });
    }


    /* splitting the inner node's keys into two parts
    from 0 to key and key+1 to end and returns an array with keys 0 to key
    @parameters:
    keys : array of keys to be partitioned
    index : where the split should happen
     */
    public Integer[] splitInternalKeys(Integer[] keys, int index) {
        Integer[] halfKeys = new Integer[this.m];
        int idx = 0;
        int i = index + 1;
        keys[index] = null;
        while (i < keys.length) {
            halfKeys[idx] = keys[i];
            idx += 1;
            keys[i] = null;

            i += 1;
        }
        return halfKeys;
    }

    /* splitting the inner node's children objects into two parts
    from 0 to key and key+1 to end, and return the children objects array from 0 to key
    @parameters:
    node : node for which the children
    index : where the split should happen
     */
    public ParentNode[] splitChildren(Node node, int index) {

        ParentNode[] halfChildren = new ParentNode[this.m + 1];
        int idx = 0;
        int i = index + 1;
        while (i < node.children.length) {
            halfChildren[idx] = node.children[i];
            idx += 1;
            node.deleteChild(i);
            i += 1;
        }
        return halfChildren;
    }

    /*
    Splits an internal Node into two halves and also updates children pointers respectively,
    and creates a new parent node with the middle most key. If it already has
    a parent merges with that parent else it will become a new parent.
    @parameter:
    Node: node which is to be splitted
     */
    void splitInternalNode(Node node) {

        Node parent = node.parent;

        int newParentKey = node.keys[middle];
        Integer[] halfKeys = splitInternalKeys(node.keys, middle);
        ParentNode[] halfChildren = splitChildren(node, middle);

        node.nodeDegree = getNullIndex(node.children);

        Node sibling = new Node(this.m, halfKeys, halfChildren);
        for (ParentNode parentNode : halfChildren) {
            if (parentNode != null) {
                parentNode.parent = sibling;
            }
        }

        sibling.rightSibling = node.rightSibling;
        if (sibling.rightSibling != null) {
            sibling.rightSibling.leftSibling = sibling;
        }
        node.rightSibling = sibling;
        sibling.leftSibling = node;

        if (parent == null) {

            Integer[] keys = new Integer[this.m];
            keys[0] = newParentKey;
            Node newRoot = new Node(this.m, keys);
            newRoot.appendChild(node);
            newRoot.appendChild(sibling);

            node.parent = newRoot;
            sibling.parent = newRoot;
            this.root = newRoot;


        } else {

            parent.keys[parent.nodeDegree - 1] = newParentKey;
            Arrays.sort(parent.keys, 0, parent.nodeDegree);

            int pointerIndex = parent.getChildIndex(node) + 1;
            parent.addChild(sibling, pointerIndex);
            sibling.parent = parent;
        }
    }

    /* splitting the Leaf node's pairs into two parts
   from 0 to key and key+1 to end and returns an array with keys 0 to key
   @parameters:
   node : node for which pairs to be partitioned
   index : where the split should happen
    */
    public Pair[] splitLeaf(LeafNode node, int index) {
        Pair[] pairs = node.pairs;
        Pair[] splittedHalf = new Pair[this.m];
        int idx = 0;
        int i = index;
        while (i < pairs.length) {
            splittedHalf[idx] = pairs[i];
            idx += 1;
            node.deleteKey(i);
            i += 1;
        }
        return splittedHalf;
    }

    /* If a node is deficient it handles deficiency either by merging or lending from siblings
@parameter:
node : node which is deficient
 */
    public void removeDeficiency(Node node) {
        Node parentNode = node.parent;
        Node rightSibling = node.rightSibling;
        Node leftSibling = node.leftSibling;
        if (node == this.root) {
            if (this.root.nodeDegree > 1) {
            } else {
                for (int i = 0; i < node.children.length; i++) {
                    if (node.children[i] != null) {
                        if (node.children[i].getClass().toString().endsWith("ParentNode")) {
                            this.root = (Node) node.children[i];
                            this.root.parent = null;
                        } else if (node.children[i].getClass().toString().endsWith("LeafNode")) {
                            this.root = null;
                        }
                    }
                }
            }
        } else if (rightSibling != null && rightSibling.parent == node.parent && rightSibling.canLend()) {
            int key = rightSibling.keys[0];
            ParentNode firstChild = rightSibling.children[0];

            int nodeIndex = parentNode.getChildIndex(node);
            node.appendChild(firstChild);

            node.keys[node.nodeDegree - 2] = parentNode.keys[nodeIndex];
            firstChild.parent = node;
            parentNode.keys[nodeIndex] = key;

            rightSibling.deleteKeyAtIndex(0);
            rightSibling.deleteChildAtIndex(0);
        } else if (leftSibling != null && leftSibling.parent == node.parent && leftSibling.canLend()) {
            int key = leftSibling.keys[leftSibling.nodeDegree - 2];
            ParentNode lastChild = leftSibling.children[leftSibling.nodeDegree - 2];

            int nodeIndex = parentNode.getChildIndex(node);


            int index = node.nodeDegree - 2;
            while (index >= 0) {
                node.keys[index + 1] = node.keys[index];
                index -= 1;
            }

            node.keys[0] = parentNode.keys[nodeIndex - 1];
            node.appendChild(lastChild);
            lastChild.parent = node;
            parentNode.keys[nodeIndex - 1] = key;

            leftSibling.deleteKeyAtIndex(leftSibling.nodeDegree - 2);
            leftSibling.deleteChildAtIndex(leftSibling.nodeDegree - 1);

        } else if (rightSibling != null && rightSibling.parent == node.parent && rightSibling.will_merge()) {
            int childIndex = parentNode.getChildIndex(node);
            Integer[] keysList = new Integer[this.m];
            int x = 0;

            for (x = 0; x < node.nodeDegree - 1; x++) {
                keysList[x] = node.keys[x];
            }
            keysList[x++] = parentNode.keys[childIndex];

            for (int i = 0; i < rightSibling.nodeDegree - 1; i++) {
                keysList[x] = rightSibling.keys[i];
                x += 1;
            }
            rightSibling.keys = keysList;

            for (int index = node.children.length - 1; index >= 0; index--) {
                if (node.children[index] != null) {
                    rightSibling.prependChild(node.children[index]);
                    node.children[index].parent = rightSibling;
                }
            }

            parentNode.deleteKeyAtIndex(childIndex);
            parentNode.deleteChildAtIndex(childIndex);

            rightSibling.leftSibling = node.leftSibling;

            if (rightSibling.leftSibling != null) {
                rightSibling.leftSibling.rightSibling = rightSibling;
            }
            if (node.parent.deficient()) {
                removeDeficiency(node.parent);
            }
        } else if (leftSibling != null && leftSibling.parent == node.parent && leftSibling.will_merge()) {
            int childIndex = parentNode.getChildIndex(node);
            Integer[] keysList = new Integer[this.m];
            int x = 0;
            for (x = 0; x < leftSibling.nodeDegree - 1; x++) {
                keysList[x] = leftSibling.keys[x];
            }
            keysList[x++] = parentNode.keys[childIndex - 1];
            for (int i = 0; i < node.nodeDegree - 1; i++) {
                keysList[x++] = node.keys[i];
            }
            leftSibling.keys = keysList;

            for (int k = 0; k < node.children.length; k++) {
                if (node.children[k] != null) {
                    leftSibling.appendChild(node.children[k]);
                    node.children[k].parent = leftSibling;
                }
            }

            parentNode.deleteKeyAtIndex(childIndex - 1);
            parentNode.deleteChildAtIndex(childIndex);

            leftSibling.rightSibling = node.rightSibling;
            if (leftSibling.rightSibling != null) {
                leftSibling.rightSibling.leftSibling = leftSibling;
            }

            if (node.parent.deficient()) {
                removeDeficiency(node.parent);
            }

        }

    }

    /* -------------------------------Main functions ----------------------------- */

    /* Inserts the given (key, value) pair into the Bplustree
    also handles the overflowing of nodes
    @parameters:
    key : key value of dictionary
    value: value in the dictionary pair
     */
    public void insert(int key, double value) {
        Pair pair = new Pair(key, value);
        if (this.leaf == null) {
            LeafNode leafNode = new LeafNode(this.m, pair);
            this.leaf = leafNode;
        } else {
            LeafNode leafNode;
            if (this.root == null) //Means only a leafNode is present in the tree
            {
                leafNode = this.leaf;
            } else {
                leafNode = findLeaf(this.root, key);
            }
            boolean full = leafNode.insertKey(pair);
            //If our LeafNode is full
            if (full) {
                leafNode.pairs[leafNode.totalPairs] = new Pair(key, value);
                leafNode.totalPairs += 1;
                sortPairs(leafNode.pairs);
                Pair[] splittedHalf = splitLeaf(leafNode, this.middle);

                //If LeafNode has no parent then create a parent
                if (leafNode.parent == null) {
                    Integer[] newParentKeys = new Integer[this.m];
                    newParentKeys[0] = splittedHalf[0].key;
                    Node newParent = new Node(this.m, newParentKeys);
                    leafNode.parent = newParent;
                    newParent.appendChild(leafNode);
                } else //Already have a parent
                {
                    int newKey = splittedHalf[0].key;
                    leafNode.parent.keys[leafNode.parent.nodeDegree - 1] = newKey;
                    Arrays.sort(leafNode.parent.keys, 0, leafNode.parent.nodeDegree);
                }

                //Handling the first half of the leaf after splitting
                LeafNode rightLeafNode = new LeafNode(this.m, splittedHalf, leafNode.parent);

                int childIndex = leafNode.parent.getChildIndex(leafNode) + 1;
                leafNode.parent.addChild(rightLeafNode, childIndex);

                rightLeafNode.rightSibling = leafNode.rightSibling;
                if (rightLeafNode.rightSibling != null) {
                    rightLeafNode.rightSibling.leftSibling = rightLeafNode;
                }

                leafNode.rightSibling = rightLeafNode;
                rightLeafNode.leftSibling = leafNode;

                // If root is null make the new parent as root node
                if (this.root == null) {
                    this.root = leafNode.parent;
                } else //If root is not null handle merging towards the top of the tree
                {
                    Node node = leafNode.parent;
                    while (node != null) {
                        if (node.full()) {
                            splitInternalNode(node);
                        } else
                            break;
                        node = node.parent;
                    }

                }

            }
        }
    }


    /* searches for a key in the bplus tree
    return value associated with that key else null
    @parameter:
    key : key to be searched
     */
    public Double search(int key) {
        if (this.leaf == null) {
            return null;
        }
        LeafNode leaf;
        if (this.root == null) {
            leaf = this.leaf;
        } else {
            leaf = findLeaf(this.root, key);
        }

        Pair[] pairs = leaf.pairs;
        for (Pair p : pairs) {
            if (p == null) {
                break;
            }
            if (p.key == key) {
                return p.value;
            }
        }
        return null;

    }

    /*searches for a keys in the bplus tree in the range
    [low key, high key] and return an array of values associated with the keys in the range
    @parameters:
    low key : start value of search
    high key : end value of search
     */
    public Double[] search(int lowKey, int highKey) {
        ArrayList<Double> output = new ArrayList<Double>();
        if (this.leaf == null) {
            return null;
        }
        LeafNode leaf;
        if (this.root == null) {
            leaf = this.leaf;
        } else {
            leaf = findLeaf(this.root, lowKey);
        }

        while (leaf != null) {
            Pair[] pairs = leaf.pairs;
            for (Pair p : pairs) {
                if (p == null) {
                    continue;
                }
                if (p.key >= lowKey && p.key <= highKey) {
                    output.add(p.value);
                }
            }
            leaf = leaf.rightSibling;
        }
        Double[] op = new Double[output.size()];
        op = output.toArray(op);
        return op;
    }


    /*
    deletes a key from the bplus tree and makes necessary changes to balance the bplus tree
    @parameters:
    key : key wich is to be removed from the bplus tree
     */
    public void delete(int key) {
        if (this.leaf == null) {
            return;
        }
        LeafNode leafNode;
        if (this.root == null) {
            leafNode = this.leaf;
        } else {
            leafNode = findLeaf(this.root, key);
        }

        int index = -1;
        int childIndex;
        for (int i = 0; i < leafNode.totalPairs; i++) {
            if (leafNode.pairs[i].key == key) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return;
        }

        leafNode.deleteKey(index);
        sortPairs(leafNode.pairs);

        if (leafNode.deficient()) 
        {
            Node parentNode = leafNode.parent;
            LeafNode rightSibling = leafNode.rightSibling;
            LeafNode leftSibling = leafNode.leftSibling;

            if (rightSibling != null && rightSibling.parent == leafNode.parent && rightSibling.canLend()) {
                Pair pair = rightSibling.pairs[0];
                leafNode.insertKey(pair);
                rightSibling.deleteKey(0);
                sortPairs(rightSibling.pairs);
                childIndex = parentNode.getChildIndex(leafNode);
                if (pair.key >= parentNode.keys[childIndex]) {
                    parentNode.keys[childIndex] = rightSibling.pairs[0].key;
                }
            } 
            else if (leftSibling != null && leftSibling.parent == leafNode.parent && leftSibling.canLend()) {
                Pair pair = leftSibling.pairs[leftSibling.totalPairs - 1];
                leafNode.insertKey(pair);
                sortPairs(leftSibling.pairs);
                leftSibling.deleteKey(leftSibling.totalPairs - 1);
                childIndex = parentNode.getChildIndex(leafNode);
                if (pair.key < parentNode.keys[childIndex - 1]) {
                    parentNode.keys[childIndex - 1] = leafNode.pairs[0].key;
                }
            } 
            else if (rightSibling != null && rightSibling.parent == leafNode.parent && rightSibling.will_merge()) {

                childIndex = parentNode.getChildIndex(leafNode);

                parentNode.deleteKeyAtIndex(childIndex);
                parentNode.deleteChildAtIndex(childIndex);

                for (int i = 0; i < leafNode.pairs.length; i++) {
                    if (leafNode.pairs[i] != null) {
                        rightSibling.insertKey(leafNode.pairs[i]);
                    }
                }
                rightSibling.leftSibling = leafNode.leftSibling;
                if (rightSibling.leftSibling == null) {
                    this.leaf = rightSibling;
                } else {
                    rightSibling.leftSibling.rightSibling = rightSibling;
                }

                if (parentNode.deficient()) {
                    removeDeficiency(parentNode);
                }
            } 
            else if (leftSibling != null && leftSibling.parent == leafNode.parent && leftSibling.will_merge()) {
                childIndex = parentNode.getChildIndex(leafNode);

                parentNode.deleteKeyAtIndex(childIndex - 1);
                parentNode.deleteChildAtIndex(childIndex);

                for (int i = 0; i < leafNode.pairs.length; i++) {
                    if (leafNode.pairs[i] != null) {
                        leftSibling.insertKey(leafNode.pairs[i]);
                    }
                }

                leftSibling.rightSibling = leafNode.rightSibling;
                if (leftSibling.rightSibling != null) {
                    leftSibling.rightSibling.leftSibling = leftSibling;
                }

                if (parentNode.deficient()) {
                    removeDeficiency(parentNode);
                }

            } else if (this.root == null && this.leaf.totalPairs == 0) {
                this.leaf = null;
            }
        } 
        else {
            sortPairs(leafNode.pairs);
        }

    }


    static final String initialize_command = "Initialize";
    static final String insert_command = "Insert";
    static final String delete_command = "Delete";
    static final String search_command = "Search";
    static final String NULL = "Null";

    public static void main(String[] args)
    {

        if(args.length != 1){
            System.out.println("Pass the input file name as an arguement to this Java Program (Example : input.txt)");
            return;
        }
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("output_file.txt"));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]))){
            String command = bufferedReader.readLine();
            bplustree bp = null;
            while(command != null){
                command = command.trim();
                String[] parts = command.split("[,()]");
                switch(parts[0].trim()){
                    case initialize_command:
                        bp = new bplustree(Integer.parseInt(parts[1].trim()));
                        break;
                    case insert_command:
                        bp.insert(Integer.parseInt(parts[1].trim()), Double.parseDouble(parts[2].trim()));
                        break;
                    case search_command:
                        String output = "";
                        if(parts.length == 2){
                            Double value = bp.search(Integer.parseInt(parts[1].trim()));
                            output = value == null ? NULL : String.valueOf(value);
                        }else{
                            StringBuffer buffer = new StringBuffer();
                            Double[] list = bp.search(Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim()));
                            if(list.length == 0){
                                buffer.append(NULL);
                            }else{
                                for(Double value: list)
                                {
                                    buffer.append(value).append(", ");
                                }
                                buffer.deleteCharAt(buffer.length() - 1);
                            }
                            bufferedWriter.write(buffer.toString());
                        }
                        bufferedWriter.write(output);
                        bufferedWriter.newLine();
                        break;
                    case delete_command:
                        bp.delete(Integer.parseInt(parts[1].trim()));
                        break;
                    default:
                        System.out.println("Invalid operation "+ parts[0]);
                }

                command = bufferedReader.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
