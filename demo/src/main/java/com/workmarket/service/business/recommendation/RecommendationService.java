package com.workmarket.service.business.recommendation;

import com.workmarket.business.recommendation.gen.Messages.RecommendTalentToWorkType;
import com.workmarket.business.recommendation.gen.Messages.Talent;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.Recommendation;

import java.util.List;

/**
 * Recommendation service.
 */
public interface RecommendationService {

	List<Talent> recommendTalentForTalentPool(final Long talentPoolId, final RequestContext context);

	Recommendation recommendTalentForWork(final Work work, final RecommendTalentToWorkType type, final RequestContext context);

}
