package com.workmarket.dao.session;


import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.session.Impression;
import com.workmarket.domains.model.session.ImpressionType;
import com.workmarket.dto.CampaignStatisticsDTO;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class ImpressionDAOImpl extends AbstractDAO<Impression> implements ImpressionDAO {

	protected Class<Impression> getEntityClass() {
        return Impression.class;
    }

    @Override
    public CampaignStatisticsDTO newCampaignStatistics(Long impressionTypeId, Long campaignId) {
        Assert.notNull(impressionTypeId);
        Assert.notNull(campaignId);

        CampaignStatisticsDTO campaignStatisticsDTO = new CampaignStatisticsDTO();

        // TODO AP Refactor these queries into JDBC or MongoDB
        Query query = getFactory().getCurrentSession()
                .createQuery("select count(*) from impression where impressionType = :impressionType and campaignId = :campaignId")
                .setParameter("impressionType", ImpressionType.values()[impressionTypeId.intValue()])
                .setParameter("campaignId", campaignId);

        campaignStatisticsDTO.setTotalImpressionCount((Long)query.uniqueResult());

        query = getFactory().getCurrentSession()
                .createQuery("select count(*) from impression where impressionType = :impressionType and campaignId = :campaignId")
                .setParameter("impressionType", ImpressionType.values()[impressionTypeId.intValue()])
                .setParameter("campaignId", campaignId);

        campaignStatisticsDTO.setUniqueImpressionCount((Long)query.uniqueResult());

        query = getFactory().getCurrentSession()
                .createQuery("select count(*) from impression where impressionType = :impressionType and campaignId = :campaignId and userId is null")
                .setParameter("impressionType", ImpressionType.values()[impressionTypeId.intValue()])
                .setParameter("campaignId", campaignId);

        campaignStatisticsDTO.setAnonymousTotalImpressionCount((Long)query.uniqueResult());

        query = getFactory().getCurrentSession()
                .createQuery("select count(*) from impression where impressionType = :impressionType and campaignId = :campaignId and userId is null")
                .setParameter("impressionType", ImpressionType.values()[impressionTypeId.intValue()])
                .setParameter("campaignId", campaignId);

        campaignStatisticsDTO.setAnonymousUniqueImpressionCount((Long)query.uniqueResult());

        return campaignStatisticsDTO;
    }
}
