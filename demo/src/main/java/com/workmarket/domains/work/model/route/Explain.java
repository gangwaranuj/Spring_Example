package com.workmarket.domains.work.model.route;

/**
 * Represents an explain message for a routing strategy. Text is added by the implementor of the strategy
 * and provided as part of the recommendation result.
 */
public class Explain {
    private final StringBuilder explain = new StringBuilder();

    /**
     * Constructor.
     */
    public Explain() {
    }

    /**
     * Constructor.
     * @param txt The text of the explain
     */
    public Explain(final String txt) {
        explain.append(txt);
    }


    /**
     * Adds new text to our explain.
     * @param txt The new text for the explain
     * @return Explain The updated explain instance
     */
    public Explain add(final String txt) {
        if (explain.length() > 0) {
            explain.append(System.lineSeparator());
        }
        explain.append(txt);

        return this;

    }

    /**
     * Returns a string representation of our explain.
     * @return String The explain
     */
    public String toString() {
        return explain.toString();
    }
}
