package com.workmarket.data.solr.indexer.user;

import com.google.common.collect.Lists;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.event.company.VendorSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Implementation of solr vendor indexer.
 *
 * NOTE: Through out this code, we use vendorId, which is really companyId.
 */
@Service
public class SolrVendorIndexerImpl implements SolrVendorIndexer {

    private static final Log logger = LogFactory.getLog(SolrVendorIndexerImpl.class);

    @Qualifier("solrVendorUpdater")
    @Autowired private SolrVendorUpdater solrVendorUpdater;

    @Autowired private EventRouter eventRouter;
    @Autowired private CompanyService companyService;

    @Value(value = "${solr.buffer.size}")
    private int bufferSize;

    @Override
    public void reindexAll() {
        Integer maxCompanyId = companyService.getMaxCompanyId();
        Long limit = maxCompanyId.longValue();
        Long fromId = limit - bufferSize;
        do {
            eventRouter.sendEvent(new VendorSearchIndexEvent().setToId(limit).setFromId(fromId));
            limit = fromId;
            if (limit >= bufferSize) {
                fromId = limit - bufferSize;
            } else {
                fromId = 1L;
            }
        } while (fromId > 1);
        eventRouter.sendEvent(new VendorSearchIndexEvent().setFromId(fromId).setToId(limit));
    }

    @Override
    public void reindexByUUID(final Collection<String> uuids) {
        if (isNotEmpty(uuids)) {
            final List<Long> companyIds = companyService.getCompanyIdsByUuids(uuids);
            reindexById(companyIds);
        }
    }

    @Override
    public void reindexById(final Collection<Long> ids) {
        final List<Long> vendorIds = companyService.findVendorIdsFromCompanyIds(ids);
        indexVendors(Lists.newArrayList(vendorIds));
    }

    @Override
    public void reindexById(final Long vendorId) {
        indexVendors(Lists.newArrayList(vendorId));
    }

    @Override
    public void reindexBetweenIds(final long fromId, final long toId) {
        final List<Long> ids = Lists.newArrayList();
        for (long i = fromId; i <= toId; i++) {
            ids.add(i);
        }
        final List<Long> vendorIds = companyService.findVendorIdsFromCompanyIds(ids);
        indexVendors(vendorIds);
    }

    @Override
    public void deleteById(Collection<Long> vendorIds) {
        if (isNotEmpty(vendorIds)) {
            solrVendorUpdater.deleteById(new ArrayList<>(vendorIds));
        }
    }

    /**
     * indexes a list vendors, batching them by total team size.
     *
     * @param vendorIds a list of vendor ids
     */
    private void indexVendors(final List<Long> vendorIds) {
        if (isEmpty(vendorIds)) {
            return;
        }
        int teamSizeSum = 0;
        List<Long> idsToIndex = Lists.newArrayList();
        List<Long> idsToEventRouter = Lists.newArrayList();
        for (Long vendorId : vendorIds) {
            int teamSize = companyService.getTeamSize(vendorId);
            if (teamSize == 0) {
                continue;
            }
            if (teamSizeSum == 0 || teamSizeSum + teamSize <= bufferSize) {
                teamSizeSum += teamSize;
                idsToIndex.add(vendorId);
            } else {
                idsToEventRouter.add(vendorId);
            }
        }
        if (idsToIndex.size() > 0) {
            solrVendorUpdater.indexByIds(idsToIndex);
        }
        if (idsToEventRouter.size() > 0) {
            eventRouter.sendEvent(new VendorSearchIndexEvent().setIds(idsToEventRouter));
        }
    }
}
