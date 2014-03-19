package com.femtioprocent.fpd.util;

import java.util.ArrayList;
import java.util.Iterator;

public class ExpandedArrayList<T> implements Iterable<T> {

    public ArrayList<T> li;

    public ExpandedArrayList() {
        li = new ArrayList<T>();
    }

    public ExpandedArrayList(ArrayList<T> li) {
        this.li = li;
    }

    public T get(int ix) {
        return li.get(ix);
    }

    public void set(int ix, T t) {
        int n = ix + 1 - li.size();  // make sure there is enough space in the list
        for (int i = 0; i < n; i++) // allocate the needed space
        {
            li.add(null);
        }
        li.set(ix, t);
    }

    public ArrayList<T> getArrayList() {
        return li;
    }

    public Iterator<T> iterator() {
        return li.iterator();
    }
}
