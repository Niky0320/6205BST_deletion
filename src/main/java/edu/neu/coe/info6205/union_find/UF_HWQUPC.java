/**
 * Original code:
 * Copyright © 2000–2017, Robert Sedgewick and Kevin Wayne.
 * <p>
 * Modifications:
 * Copyright (c) 2017. Phasmid Software
 */
package edu.neu.coe.info6205.union_find;

import edu.neu.coe.info6205.util.Benchmark_Timer;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Height-weighted Quick Union with Path Compression
 */
public class UF_HWQUPC implements UF {
    /**
     * Ensure that site p is connected to site q,
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     */
    public void connect(int p, int q) {
        if (!isConnected(p, q)) union(p, q);
    }

    /**
     * Initializes an empty union–find data structure with {@code n} sites
     * {@code 0} through {@code n-1}. Each site is initially in its own
     * component.
     *
     * @param n               the number of sites
     * @param pathCompression whether to use path compression
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public UF_HWQUPC(int n, boolean pathCompression) {
        count = n;
        parent = new int[n];
        height = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            height[i] = 1;
        }
        this.pathCompression = pathCompression;
    }

    /**
     * Initializes an empty union–find data structure with {@code n} sites
     * {@code 0} through {@code n-1}. Each site is initially in its own
     * component.
     * This data structure uses path compression
     *
     * @param n the number of sites
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public UF_HWQUPC(int n) {
        this(n, true);
    }

    public void show() {
        for (int i = 0; i < parent.length; i++) {
            System.out.printf("%d: %d, %d\n", i, parent[i], height[i]);
        }
    }

    /**
     * Returns the number of components.
     *
     * @return the number of components (between {@code 1} and {@code n})
     */
    public int components() {
        return count;
    }

    /**
     * Returns the component identifier for the component containing site {@code p}.
     *
     * @param p the integer representing one site
     * @return the component identifier for the component containing site {@code p}
     * @throws IllegalArgumentException unless {@code 0 <= p < n}
     */
    public int find(int p) throws IllegalArgumentException{
        validate(p);
        int root = p;
        if(pathCompression==true){
            root = doPathCompression(root);
        }else{
            while (root != parent[root]) root = parent[root];
        }

        return root;



    }

    /**
     * Returns true if the the two sites are in the same component.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @return {@code true} if the two sites {@code p} and {@code q} are in the same component;
     * {@code false} otherwise
     * @throws IllegalArgumentException unless
     *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    /**
     * Merges the component containing site {@code p} with the
     * the component containing site {@code q}.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @throws IllegalArgumentException unless
     *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public void union(int p, int q) {
        // CONSIDER can we avoid doing find again?
        mergeComponents(find(p), find(q));


        count--;
    }

    @Override
    public int size() {
        return parent.length;
    }

    /**
     * Used only by testing code
     *
     * @param pathCompression true if you want path compression
     */
    public void setPathCompression(boolean pathCompression) {
        this.pathCompression = pathCompression;
    }

    @Override
    public String toString() {
        return "UF_HWQUPC:" + "\n  count: " + count +
                "\n  path compression? " + pathCompression +
                "\n  parents: " + Arrays.toString(parent) +
                "\n  heights: " + Arrays.toString(height);
    }

    // validate that p is a valid index
    private void validate(int p) {
        int n = parent.length;
        if (p < 0 || p >= n) {
            throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n - 1));
        }
    }

    private void updateParent(int p, int x) {
        parent[p] = x;
    }

    private void updateHeight(int p, int x) {
        height[p] += height[x];
    }
    private void updateDepth(int p, int x) {
        height[x] += height[p];
        //height[p] -= height[x];
    }

    /**
     * Used only by testing code
     *
     * @param i the component
     * @return the parent of the component
     */
    private int getParent(int i) {
        return parent[i];
    }

    private final int[] parent;   // parent[i] = parent of i
    private final int[] height;   // height[i] = height of subtree rooted at i
    private int count;  // number of components
    private boolean pathCompression;
    private static int ccc;

    private void mergeComponents(int i, int j) {
        // TO BE IMPLEMENTED make shorter j root point to taller one i

        if(i==j){
            return;
        }

        if(height[i]<height[j]){
            updateParent(i,j);

            updateHeight(j,i);

        }else{
            updateParent(j,i);

            updateHeight(i,j);
        }



    }

    private void mergeComponents1(int i, int j) {
        // TO BE IMPLEMENTED make shorter j root point to taller one i
        if(i==j){
            return;
        }
        if(height[i]<height[j]){
            updateParent(i,j);
            updateDepth(j,i);
        }else{
            updateParent(j,i);
            updateDepth(i,j);
        }

    }

    /**
     * This implements the single-pass path-halving mechanism of path compression
     */
    private int doPathCompression(int i) {
        // TO BE IMPLEMENTED update parent to value of grandparent
        while(i!=parent[i]){
            updateParent(i, parent[parent[i]]);
            //updateHeight(i, parent[i]);
            i = parent[i];
        }
        return i;
        //i = parent[i];

    }
    public Integer[] count(int n){

        ccc=n;
        Integer[] ttt=new Integer[n];

        for(int i=0;i<n;i++){
            ttt[i] = (int)(Math.random()*n);
        }
        //generate array numbers [0,n-1] random pairs
        for(int i=0; i<n; i++){
            for(int j=i+1;j<n; j++){
                if (!isConnected(ttt[i], ttt[j])){
                    union(ttt[i], ttt[j]);
                    ccc--;
                }
            }

        }
        //return c;
        return ttt;


    }

    public Integer[] count1(int n){

        ccc=n;
        Integer[] ttt=new Integer[n];

        for(int i=0;i<n;i++){
            ttt[i] = (int)(Math.random()*n);
        }
        //generate array numbers [0,n-1] random pairs
        for(int i=0; i<n; i++){
            for(int j=i+1;j<n; j++){
                if (!isConnected(ttt[i], ttt[j])){
                    mergeComponents1(ttt[i], ttt[j]);
                    ccc--;
                }
            }

        }

        return ttt;


    }
/*
     public static void main(String[] args) {
         for(int n = 10000;n>0;n-=2000){
             UF_HWQUPC uf = new UF_HWQUPC(n,false);
             System.out.println("N: "+n+", number of collection M: "+uf.count(n));
         }
         for(int n = 1800;n>0;n-=200){
             UF_HWQUPC uf = new UF_HWQUPC(n,false);
             System.out.println("N: "+n+", number of collection M: "+uf.count(n));
         }
        for(int n = 200;n>0;n-=50){
            UF_HWQUPC uf = new UF_HWQUPC(n,false);
            System.out.println("N: "+n+", number of collection M: "+uf.count(n));
        }
         for(int n = 40;n>0;n-=10){
             UF_HWQUPC uf = new UF_HWQUPC(n,false);
             System.out.println("N: "+n+", number of collection M: "+uf.count(n));
         }
         for(int n = 9;n>0;n-=1){
             UF_HWQUPC uf = new UF_HWQUPC(n,false);
             System.out.println("N: "+n+", number of collection M: "+uf.count(n));
         }


    }

 */
    public static void main(String[] args) {
        //int N = 1000; //array[1000] 大小100-1000变化？
        //UF_HWQUPC uf = new UF_HWQUPC(N,false);

        //System.out.println("array of "+N+" elements, number of collection M: "+ccc);
        //uf.count1(N);
        //System.out.println("array of "+N+" elements");
        //System.out.println("WQU-----");
        //int n = 10000;n>0;n-=2000
        //original WQU
        for(int n = 1000;n>0;n-=200){
            System.out.println("array of "+n+" elements");
            UF_HWQUPC uf = new UF_HWQUPC(n,false);
            int finalN = n;
            Consumer<Integer[]> consumer = arr ->uf.count(finalN);
            Benchmark_Timer<Integer[]> bm = new Benchmark_Timer<Integer[]>("WQU", consumer );
            double tt = bm.run(uf.count(finalN),1000);
            System.out.println("Running time: "+tt+" milliseconds");
        }

        System.out.println();
// WQU use depth instead of size(height)
        for(int n = 1000;n>0;n-=200){
            System.out.println("array of "+n+" elements");
            UF_HWQUPC uf = new UF_HWQUPC(n,false);
            int finalN = n;
            Consumer<Integer[]> consumer = arr ->uf.count1(finalN);
            Benchmark_Timer<Integer[]> bm = new Benchmark_Timer<Integer[]>("WQU", consumer );
            double tt = bm.run(uf.count1(finalN),1000);
            System.out.println("Running time: "+tt+" milliseconds");
        }

        System.out.println();
//WQUPC
        for(int n = 1000;n>0;n-=200){
            System.out.println("array of "+n+" elements");
            UF_HWQUPC uf1 = new UF_HWQUPC(n,true);
            int finalN = n;
            Consumer<Integer[]> consumer1 = arr ->uf1.count(finalN);
            Benchmark_Timer<Integer[]> bm = new Benchmark_Timer<Integer[]>("WQUPC", consumer1 );
            double tt = bm.run(uf1.count1(finalN),1000);
            System.out.println("Running time: "+tt+" milliseconds");
        }
/*
        UF_HWQUPC uf1 = new UF_HWQUPC(N,true);
        Consumer<Integer[]> consumer1 = arr ->uf1.count(N);
        System.out.println("WQUPC-----");

        for(int n=2000; n<=64000;n=n*2){
            //2000-64000 times for n

            Benchmark_Timer<Integer[]> bm = new Benchmark_Timer<Integer[]>("WQUPC", consumer1 );
            double tt = bm.run(uf1.count(N),n);
            System.out.println("Running time: "+tt+" milliseconds");
        }
*/
    }
}
