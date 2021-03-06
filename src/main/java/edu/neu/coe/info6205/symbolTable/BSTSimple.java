package edu.neu.coe.info6205.symbolTable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import edu.neu.coe.info6205.union_find.UF_HWQUPC;
import edu.neu.coe.info6205.util.Benchmark_Timer;

public class BSTSimple<Key extends Comparable<Key>, Value> implements BstDetail<Key, Value> {
    @Override
    public Boolean contains(Key key) {
        return get(key) != null;
    }

    /**
     * This implementation of putAll ensures that the keys are inserted into this BST in random order.
     *
     * @param map a map of key value pairs
     */
    @Override
    public void putAll(Map<Key, Value> map) {
        List<Key> ks = new ArrayList<>(map.keySet());
        Collections.shuffle(ks);
        for (Key k : ks) put(k, map.get(k));
    }

    @Override
    public int size() {
        return root != null ? root.count : 0;
    }

    @Override
    public void inOrderTraverse(BiFunction<Key, Value, Void> f) {
        doTraverse(0, root, f);
    }

    @Override
    public Value get(Key key) {
        return get(root, key);
    }

    @Override
    public Value put(Key key, Value value) {
        NodeValue nodeValue = put(root, key, value);
        if (root == null) root = nodeValue.node;
        if (nodeValue.value == null) root.count++;
        return nodeValue.value;
    }

    public void delete(Key key) {
        root = delete(root, key);
    }
    
    public void deleteRandom(Key key) {
        root = deleteRandom(root, key);
    }
    

    @Override
    public void deleteMin() {
        root = deleteMin(root);
    }
    
    @Override
    public void deleteMax() {
        root = deleteMax(root);
    }

    @Override
    public Set<Key> keySet() {
        return null;
    }

    /**
     * Method to yield the depth of a key, relative to the root.
     *
     * @param key the key whose depth we are interested in.
     * @return the depth of the key (root: 0) otherwise -1 if key is not found.
     */
    public int depth(Key key) {
        try {
            return depth(root, key);
        } catch (DepthException e) {
            return -1;
        }
    }

    public BSTSimple() {
    }

    public BSTSimple(Map<Key, Value> map) {
        this();
        putAll(map);
    }

    Node root = null;

    private Value get(Node node, Key key) {
        Node result = getNode(node, key);
        return result != null ? result.value : null;
    }

    private Node getNode(Node node, Key key) {
        if (node == null) return null;
        int cf = key.compareTo(node.key);
        if (cf < 0) return getNode(node.smaller, key);
        else if (cf > 0) return getNode(node.larger, key);
        else return node;
    }

    /**
     * Method to put the key/value pair into the subtree whose root is node.
     *
     * @param node  the root of a subtree
     * @param key   the key to insert
     * @param value the value to associate with the key
     * @return a tuple of Node and Value: Node is the
     */
    private NodeValue put(Node node, Key key, Value value) {
        // If node is null, then we return the newly constructed Node, and value=null
        if (node == null) return new NodeValue(new Node(key, value, 0), null);
        int cf = key.compareTo(node.key);
        if (cf == 0) {
            // If keys match, then we return the node and its value
            NodeValue result = new NodeValue(node, node.value);
            node.value = value;
            return result;
        } else if (cf < 0) {
            // if key is less than node's key, we recursively invoke put in the smaller subtree
            NodeValue result = put(node.smaller, key, value);
            if (node.smaller == null)
                node.smaller = result.node;
            if (result.value == null)
                result.node.count++;
            return result;
        } else {
            // if key is greater than node's key, we recursively invoke put in the larger subtree
            NodeValue result = put(node.larger, key, value);
            if (node.larger == null)
                node.larger = result.node;
            if (result.value == null)
                result.node.count++;
            return result;
        }
    }

    private Node delete(Node x, Key key) {
    	if (x == null) return null;
    	int cmp = key.compareTo(x.key);
    	
    	if (cmp < 0) x.smaller = delete(x.smaller, key);
    	else if (cmp > 0) x.larger = delete(x.larger, key);
    	
    	else { 
    	  if (x.larger == null) return x.smaller;
    	  if (x.smaller == null) return x.larger;
    	  Node t = x;
    	  x = min(t.larger);
    	  x.larger = deleteMin(t.larger);
    	  x.smaller = t.smaller;
    	} 
    	x.count = size(x.smaller) + size(x.larger) + 1;
    	return x;
    }
    
    private Node deleteRandom(Node x, Key key) {
    	int tt = (int)Math.random()>0.5?0:1;
    	//new Random().nextInt(2);//0 OR 1
    	
    	
    	if (x == null) return null;
    	int cmp = key.compareTo(x.key);
    	
    	if (cmp < 0) x.smaller = deleteRandom(x.smaller, key);
    	else if (cmp > 0) x.larger = deleteRandom(x.larger, key);
    	
    	else { 
    	  if (x.larger == null) return x.smaller;
    	  if (x.smaller == null) return x.larger;
    	  
    	  if(tt==1) {
    		  Node t = x;
        	  x = min(t.larger);
        	  x.larger = deleteMin(t.larger);
        	  x.smaller = t.smaller;
    	  }
    	  if(tt==0)  {
    		  Node t = x;
        	  x = max(t.smaller);
        	  x.smaller = deleteMax(t.smaller);
        	  x.larger = t.larger;
    	  }
    	  
    	} 
    	x.count = size(x.smaller) + size(x.larger) + 1;
    	return x;
    }

    private Node deleteMin(Node x) {
        if (x.smaller == null) return x.larger;
        x.smaller = deleteMin(x.smaller);
        x.count = 1 + size(x.smaller) + size(x.larger);
        return x;
    }
    
    private Node deleteMax(Node x) {
        if (x.larger == null) return x.smaller;
        x.larger = deleteMax(x.larger);
        x.count = 1 + size(x.larger) + size(x.smaller);
        return x;
    }

    private int size(Node x) {
        return x == null ? 0 : x.count;
    }

    private Node min(Node x) {
        if (x == null) throw new RuntimeException("min not implemented for null");
        else if (x.smaller == null) return x;
        else return min(x.smaller);
    }
    
    private Node max(Node x) {
        if (x == null) throw new RuntimeException("max not implemented for null");
        else if (x.larger == null) return x;
        else return min(x.larger);
    }
    
   


    /**
     * Do a generic traverse of the binary tree starting with node
     *
     * @param q    determines when the function f is invoked ( lt 0: pre, ==0: in, gt 0: post)
     * @param node the node
     * @param f    the function to be invoked
     */
    private void doTraverse(int q, Node node, BiFunction<Key, Value, Void> f) {
        if (node == null) return;
        if (q < 0) f.apply(node.key, node.value);
        doTraverse(q, node.smaller, f);
        if (q == 0) f.apply(node.key, node.value);
        doTraverse(q, node.larger, f);
        if (q > 0) f.apply(node.key, node.value);
    }

    /**
     * Yield the total depth of this BST. If root is null, then depth will be 0.
     *
     * @return the total number of levels in this BST.
     */
    public int depth() {
        return depth(root);
    }

    private int depth(Node node) {
        if (node == null) return 0;
        int depthS = depth(node.smaller);
        int depthL = depth(node.larger);
        return 1 + Math.max(depthL, depthS);
    }
    
    

    private class NodeValue {
        private final Node node;
        private final Value value;

        NodeValue(Node node, Value value) {
            this.node = node;
            this.value = value;
        }

        @Override
        public String toString() {
            return node + "<->" + value;
        }
    }

    class Node {
        Node(Key key, Value value, int depth) {
            this.key = key;
            this.value = value;
            this.depth = depth;
        }

        final Key key;
        Value value;
        final int depth;
        Node smaller = null;
        Node larger = null;
        int count = 0;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Node: " + key + ":" + value);
            if (smaller != null) sb.append(", smaller: ").append(smaller.key);
            if (larger != null) sb.append(", larger: ").append(larger.key);
            return sb.toString();
        }

    }

    private  Node makeNode(Key key, Value value, int depth) {
        return new Node(key, value, depth);
    }

    private Node getRoot() {
        return root;
    }

    private void setRoot(Node node) {
        if (root == null) {
            root = node;
            root.count++;
        } else
            root = node;
    }

    private void show(Node node, StringBuffer sb, int indent) {
        if (node == null) return;
        for (int i = 0; i < indent; i++) sb.append("  ");
        sb.append(node.key);
        sb.append(": ");
        sb.append(node.value);
        sb.append("\n");
        if (node.smaller != null) {
            for (int i = 0; i <= indent; i++) sb.append("  ");
            sb.append("smaller: ");
            show(node.smaller, sb, indent + 1);
        }
        if (node.larger != null) {
            for (int i = 0; i <= indent; i++) sb.append("  ");
            sb.append("larger: ");
            show(node.larger, sb, indent + 1);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        show(root, sb, 0);
        return sb.toString();
    }

    private int depth(Node node, Key key) throws DepthException {
        if (node == null) throw new DepthException();
        int cf = key.compareTo(node.key);
        if (cf < 0) return 1 + depth(node.smaller, key);
        else if (cf > 0) return 1 + depth(node.larger, key);
        else return 0;
    }

    private static class DepthException extends Exception {
        public DepthException() {
        }
    }
    
    public static void main(String[] args) {
    	//mix 1024 key additions randomly with 512 deletions
    	
 //delete Hibbard
    	System.out.println("Delete use Hibbard:"); 
    	
 //512I+256D???1024I+512D	each trail conduct 50 times, calculate average of max depth
    	for(int it=512;it<=Math.pow(2,20);it*=2) {
    		int dt = it/2;
    		int sumDepth = 0;
    		int sumN = 0;
    		
    		for(int tim=0;tim<50;tim++) {
    	    
    	    BSTSimple<Integer, Integer> bst = new BSTSimple<>();
    	    bst.put(500,100); //root ,N=1    
    	    int i=0;
    	    int j=0;
            while(i<it || j<dt) {
            	int tt = new Random().nextInt(2);//0 OR 1
            	
            	int keyy  = new Random().nextInt(it)+1;
            	int valuee  = new Random().nextInt(it)+1;
            	if(tt==1 && i<it) {
            		bst.put(keyy,valuee);
            		i++;
            	}if(tt==0 && j<dt) {
            		bst.delete(keyy);
            		j++;
            	}
            }
            sumDepth += bst.depth();
            sumN += bst.size();
            //System.out.println( "lgN = "+ String.format("%.2f",Math.log(bst.size())/Math.log(2)));
    		} 
    		
    		System.out.println("N="+String.format("%.2f",(float)sumN/50)+", lgN = "+String.format("%.2f",Math.log(sumN/50)/Math.log(2))+", Mean Max depth: "+ String.format("%.2f",(float)sumDepth/50) );  
        } 
    		
    	    
        System.out.println();
        
        
        
// delete use Arbitrary Substitution Principal
        System.out.println("Delete use Random Selection:"); 
        
        for(int it1=512;it1<=Math.pow(2,20);it1*=2) {
        	
        	int dt1 = it1/2;	
        	int sumDepth1 = 0;
    		int sumN1 = 0;
    		
    		for(int tim1=0;tim1<50;tim1++) {
    			BSTSimple<Integer, Integer> bst1 = new BSTSimple<>();
	            bst1.put(500,100); //root ,N=1
	            int i1=0;
	            int j1=0;
                while(i1<it1 || j1<dt1) {
        	       int tt1 = new Random().nextInt(2);//0 OR 1 
        	//(int)Math.random()>0.5?0:1;
        	       int keyy1  = new Random().nextInt(it1)+1;
        	       int valuee1  = new Random().nextInt(it1)+1;
        	
        	//System.out.println("tt:"+tt+", key:"+keyy+" value:"+valuee);
        	       if(tt1==1 && i1<it1) {
        		      bst1.put(keyy1,valuee1);
        		//System.out.println("insert:("+keyy+","+valuee+")!");
        		      i1++;
        	       }if(tt1==0 && j1<dt1) {
        		       bst1.deleteRandom(keyy1);
        		//System.out.println("delete:("+keyy+")!");
        		       j1++;
        	       }
                }
                sumDepth1 += bst1.depth();
                sumN1 += bst1.size();
           }
           System.out.println("N="+String.format("%.2f",(float)sumN1/50)+", lgN = "+String.format("%.2f",Math.log(sumN1/50)/Math.log(2))+", Mean Max depth: "+ String.format("%.2f",(float)sumDepth1/50) );  
       
        }
    }

	
}
