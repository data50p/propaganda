package com.femtioprocent.fpd.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Various functions for List.
 */
@SuppressWarnings("unchecked")
public class ListEvaluator {

    /**
     * Return a reversed list. First element last.
     */
    public static <T> List<T> reverse(List<T> t_li) {
        ArrayList<T> t2_li = new ArrayList<T>();
        t2_li.addAll(t_li);
        Collections.reverse(t2_li);
        return t2_li;
    }

    /**
     * Return a new list with values that has elements fun(item) where item is taken from incomming argument list t_li.
     *
     * @return [fun(t_li:get(0)), fun(t_li:get(1)), fun(t_li:get(2)), ...]
     */
    public static <T, T2> List<T2> map(List<T> t_li, Function2<T2, T> fun) {
        List<T2> t2_li = new ArrayList();
        for (T t : t_li) {
            T2 t2 = fun.eval(t);
            t2_li.add(t2);
        }
        return t2_li;
    }

    /**
     * Return a new list with values that has elements fun(item, ta) where item is taken from incomming argument list t_li.
     *
     * @return [fun(t_li:get(0, ta)), fun(t_li:get(1, ta)), fun(t_li:get(2, ta)), ...]
     */
    public static <T, T2, TA> List<T2> map(List<T> t_li, Function3<T2, T, TA> fun, TA ta) {
        List<T2> t2_li = new ArrayList();
        for (T t : t_li) {
            T2 t2 = fun.eval(t, ta);
            t2_li.add(t2);
        }
        return t2_li;
    }

    /**
     * Return a new list with values coming from input argument list t_li. But keep only those element where the predicate fileter returns true.
     *
     * @return a new list with some or all elements from t_li according to if filter(elem) returns true.
     */
    public static <T> List<T> filter(List<T> t_li, Filter<T> filter) {
        List<T> t2_li = new ArrayList();
        for (T t : t_li) {
            if (filter.eval(t)) {
                t2_li.add(t);
            }
        }
        return t2_li;
    }

    /**
     * Return a new list with values that has elements fun(item) where item is taken from incomming argument list t_li. Keep only elements where the result is
     * not null.
     *
     * @return [fun(t_li:get(0)), fun(t_li:get(1)), fun(t_li:get(2)), ...]; where fun(_) != null
     */
    public static <T, T2> List<T2> mapfilter(List<T> t_li, Function2<T2, T> fun) {
        List<T2> t2_li = new ArrayList();
        for (T t : t_li) {
            T2 t2 = fun.eval(t);
            if (t2 != null) {
                t2_li.add(t2);
            }
        }
        return t2_li;
    }

    /**
     * Return a new list with values that has elements fun(item, ta) where item is taken from incomming argument list t_li. Keep only elements where the result
     * is not null.
     *
     * @return [fun(t_li:get(0, ta)), fun(t_li:get(1, ta)), fun(t_li:get(2, ta)), ...]; where fun(_, _) != null
     */
    public static <T, T2, TA> List<T2> mapfilter(List<T> t_li, Function3<T2, T, TA> fun, TA ta) {
        List<T2> t2_li = new ArrayList();
        for (T t : t_li) {
            T2 t2 = fun.eval(t, ta);
            if (t2 != null) {
                t2_li.add(t2);
            }
        }
        return t2_li;
    }

    /**
     * Return fun(fun(fun(ta, t_li[0]), t_li[1]), t_li[2])...
     */
    public static <T> T iterate(List<T> t_li, Function1<T> fun, T ta) {
        for (T t : t_li) {
            ta = fun.eval(ta, t);
        }
        return ta;
    }

    /**
     * Return fun(t_li[n-2], fun(t_li[n-1], fun(t_li[n], ta)))...
     */
    public static <T> T reviterate(List<T> t_li, Function1<T> fun, T ta) {
        List<T> rt_li = reverse(t_li);
        for (T t : rt_li) {
            ta = fun.eval(t, ta);
        }
        return ta;
    }
}
