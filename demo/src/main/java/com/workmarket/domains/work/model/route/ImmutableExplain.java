package com.workmarket.domains.work.model.route;

/**
 * Essentially a wrapper around our Explain object making it immutable.
 */
public class ImmutableExplain {

    private final Explain explain;

    /**
     * Constructor.
     * @param explain The explain we are wrapping as an immutable object
     */
    public ImmutableExplain(final Explain explain) {
        this.explain = explain;
    }

    /**
     * Returns a string representation of our explain.
     * @return String The explain
     */
    public String toString() {
        return explain.toString();
    }
}
