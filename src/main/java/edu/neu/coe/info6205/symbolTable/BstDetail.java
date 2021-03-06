package edu.neu.coe.info6205.symbolTable;

import java.util.Map;
import java.util.function.BiFunction;

public interface BstDetail<Key extends Comparable<Key>, Value> extends BST<Key, Value> {

    Boolean contains(Key key);

    void putAll(Map<Key, Value> map);

    void delete(Key key);
    
    void deleteRandom(Key key);

    int size();

    void inOrderTraverse(BiFunction<Key, Value, Void> f);

    void deleteMin();

	void deleteMax();
}
