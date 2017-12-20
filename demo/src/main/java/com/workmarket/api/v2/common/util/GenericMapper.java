package com.workmarket.api.v2.common.util;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic mapper class used in place of custom JSON serializers.
 *
 * Created by ianha on 4/3/15.
 */
public abstract class GenericMapper<S, D> {
    public List<D> map(List<S> source) {
        List<D> results = new ArrayList<D>();

        if (CollectionUtils.isEmpty(source)) {
            return results;
        }

        for (S s : source) {
            results.add(map(s));
        }

        return results;
    }

    public abstract D map(S source);
}
