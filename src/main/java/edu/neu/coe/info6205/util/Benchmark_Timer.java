/*
 * Copyright (c) 2018. Phasmid Software
 */

package edu.neu.coe.info6205.util;

import edu.neu.coe.info6205.sort.simple.InsertionSort;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static edu.neu.coe.info6205.util.Utilities.formatWhole;

/**
 * This class implements a simple Benchmark utility for measuring the running time of algorithms.
 * It is part of the repository for the INFO6205 class, taught by Prof. Robin Hillyard
 * <p>
 * It requires Java 8 as it uses function types, in particular, UnaryOperator&lt;T&gt; (a function of T => T),
 * Consumer&lt;T&gt; (essentially a function of T => Void) and Supplier&lt;T&gt; (essentially a function of Void => T).
 * <p>
 * In general, the benchmark class handles three phases of a "run:"
 * <ol>
 *     <li>The pre-function which prepares the input to the study function (field fPre) (may be null);</li>
 *     <li>The study function itself (field fRun) -- assumed to be a mutating function since it does not return a result;</li>
 *     <li>The post-function which cleans up and/or checks the results of the study function (field fPost) (may be null).</li>
 * </ol>
 * <p>
 * Note that the clock does not run during invocations of the pre-function and the post-function (if any).
 *
 * @param <T> The generic type T is that of the input to the function f which you will pass in to the constructor.
 */
public class Benchmark_Timer<T> implements Benchmark<T> {

    /**
     * Calculate the appropriate number of warmup runs.
     *
     * @param m the number of runs.
     * @return at least 2 and at most m/10.
     */
    static int getWarmupRuns(int m) {
        return Integer.max(2, Integer.min(10, m / 10));
    }

    /**
     * Run function f m times and return the average time in milliseconds.
     *
     * @param supplier a Supplier of a T
     * @param m        the number of times the function f will be called.
     * @return the average number of milliseconds taken for each run of function f.
     */
    @Override
    public double runFromSupplier(Supplier<T> supplier, int m) {
        logger.info("Begin run: " + description + " with " + formatWhole(m) + " runs");
        // Warmup phase
        final Function<T, T> function = t -> {
            fRun.accept(t);
            return t;
        };
        new Timer().repeat(getWarmupRuns(m), supplier, function, fPre, null);

        // Timed phase
        return new Timer().repeat(m, supplier, function, fPre, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun, Consumer<T> fPost) {
        this.description = description;
        this.fPre = fPre;
        this.fRun = fRun;
        this.fPost = fPost;
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun) {
        this(description, fPre, fRun, null);
    }

    /**
     * Constructor for a Benchmark_Timer with only fRun and fPost Consumer parameters.
     *
     * @param description the description of the benchmark.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, Consumer<T> fRun, Consumer<T> fPost) {
        this(description, null, fRun, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer where only the (timed) run function is specified.
     *
     * @param description the description of the benchmark.
     * @param f           a Consumer function (i.e. a function of T => Void).
     *                    Function f is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, Consumer<T> f) {
        this(description, null, f, null);
    }

    private final String description;
    private final UnaryOperator<T> fPre;
    private final Consumer<T> fRun;
    private final Consumer<T> fPost;

    final static LazyLogger logger = new LazyLogger(Benchmark_Timer.class);

    public static void main(String[] args)  {

        //Integer[] a1 ={2, 4, 5, 8, 9, 1};
        //Integer[] a2 = {1,2,3,4,5,6};
        //Integer[] a3 = {1,2,3,10,17,22,29,55,60,18,9,90,11,7,78,10,9,1,13,77};
        //Integer[] a4 = {90,80,77,69,65,60,59,57,63,60,58,49,33,31,20,11,9,5,2,1};

        Integer[] a5=new Integer[1000];
        for(int i=0;i<a5.length;i++){
            a5[i]=(int)(Math.random()*100);
        }

        InsertionSort<Integer> sorter = new InsertionSort<>();
        Consumer<Integer[]> consumer = arr -> sorter.sort(arr, 0, arr.length);


//a1 random order array list:
        System.out.println("Using random order array:");
        for(int N=2000; N<=64000;N=N*2){

            Benchmark_Timer<Integer[]> bm = new Benchmark_Timer<>("Benchmarking Insertion Sort with N = "+N, consumer);

            double tt = bm.run(a5,N); //m=1000
            //String t = new DecimalFormat("0.00000").format(tt+"");
            System.out.println("Running time: "+tt+" milliseconds");

        }
        
        System.out.println();

    //a2 ordered array list:
        System.out.println("Using ordered array:");
        Arrays.sort(a5);
        for(int N=2000; N<=64000;N=N*2){

            Benchmark_Timer<Integer[]> bm = new Benchmark_Timer<>("Benchmarking Insertion Sort with N = "+N, consumer);

            double tt = bm.run(a5,N);
            System.out.println("Running time: "+tt+" milliseconds");

        }

        System.out.println();

        //a3 partially- order array list:
        Integer[] a7=new Integer[500];
        for(int i=0;i<a7.length;i++){
            a7[i]=(int)(Math.random()*100);
        }
        Integer[] a8=new Integer[1000];
        for(int i=0;i<500;i++){
            a8[i]=a7[i];
        }//0-499
        Arrays.sort(a7);
        for(int i=500;i<1000;i++){
            for(int j =0;j<a7.length;j++){
                a8[i]=a7[j];
            }

        }//500-999


        System.out.println("Using partially-ordered array:");
        for(int N=2000; N<=64000;N=N*2){

            Benchmark_Timer<Integer[]> bm = new Benchmark_Timer<>("Benchmarking Insertion Sort with N = "+N, consumer);

            double tt = bm.run(a8,N);
            System.out.println("Running time: "+tt+" milliseconds");

        }
        System.out.println();


        Mycomparator c = new Mycomparator();    // 实例化一个Comparator对象
        Arrays.sort(a5, c);
        System.out.println("Using reverse-ordered array:");
        for(int N=2000; N<=64000;N=N*2){

            Benchmark_Timer<Integer[]> bm = new Benchmark_Timer<>("Benchmarking Insertion Sort with N = "+N, consumer);

            double tt = bm.run(a5,N);
            System.out.println("Running time: "+tt+" milliseconds");

        }

    }

}
class Mycomparator implements Comparator<Integer> {
    @Override
    public int compare(Integer o1, Integer o2) {
        if(o1 > o2)
            return -1;
        if(o1 < o2)
            return 1;
        return 0;
    }
}