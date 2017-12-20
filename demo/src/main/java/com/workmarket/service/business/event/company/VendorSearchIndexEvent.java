package com.workmarket.service.business.event.company;

import com.workmarket.service.business.event.search.IndexerEvent;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Indexing event for vendor (a type of company).
 */
public class VendorSearchIndexEvent extends IndexerEvent {

    private static final long serialVersionUID = 1347564773896215115L;

    public VendorSearchIndexEvent() {}

    public VendorSearchIndexEvent(Long vendorId) {
        setId(vendorId);
    }

    public VendorSearchIndexEvent(List<Long> vendorIds) {
        setIds(vendorIds);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("VendorSearchIndexEvent{");
        if (this.isDelete()) {
            sb.append("DELETE: ");
        }
        if (CollectionUtils.isNotEmpty(getIds())) {
            sb.append("vendorIds=").append(getIds());
        }

        if (getFromId() != null && getToId() != null) {
            sb.append("from: ").append(getFromId()).append(" to: ").append(this.getToId());
        }

        if (CollectionUtils.isEmpty(getIds()) && getFromId() == null && getToId() == null) {
            sb.append("Reindex all");
        }

        sb.append("}");

        return sb.toString();
    }
}
