package com.workmarket.domains.search.solr;

import org.apache.commons.lang3.StringUtils;

/**
 * Thread local wrapper used to provide a "directed towards" value in to a Solr wrappers.
 * We don't want to change the API since we are overriding Solr's API so we are using this
 * to provide some additional details.
 */
public class SolrThreadLocal {

    private static class Container {
        private String directedTowards;

        public String getDirectedTowards() {
            return directedTowards;
        }

        public void setDirectedTowards(String directedTowards) {
            this.directedTowards = directedTowards;
        }
    }

    static final ThreadLocal<Container> threadLocalStore = new ThreadLocal<>();

    private static Container getContainer() {
        if (threadLocalStore.get() == null) {
            threadLocalStore.set(new Container());
        }

        return threadLocalStore.get();
    }

    public static String getDirectedTowards() {
        return getContainer().getDirectedTowards();
    }

    public static void setDirectedTowards(String directedTowards) {
        getContainer().setDirectedTowards(directedTowards);
    }

    public static boolean isDirectedTowards(String directedTowards) {
        return StringUtils.equalsIgnoreCase(getDirectedTowards(), directedTowards);
    }

    public static boolean isDirected() {
        return getDirectedTowards() != null;
    }

    public static void clear() {
        threadLocalStore.remove();
    }

}
