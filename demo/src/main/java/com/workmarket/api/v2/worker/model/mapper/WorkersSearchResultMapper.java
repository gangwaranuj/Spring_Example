package com.workmarket.api.v2.worker.model.mapper;

import com.workmarket.api.v2.worker.model.WorkersSearchResult;
import com.workmarket.api.v2.common.util.GenericMapper;
import com.workmarket.search.response.user.PeopleSearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * Created by ianha on 4/3/15.
 */
@Deprecated
@Service
public class WorkersSearchResultMapper extends GenericMapper<PeopleSearchResult, WorkersSearchResult> {
    /**
     * The reason we map by hand is because Dozer and Oritz do not have
     * non-buggy capabilities to map nulls to empty objects or "". Rewriting a
     * JsonSerializer is the same amount of work. The added benefit of this is
     * that it's faster.
     *
     * @param source
     * @return
     */
    @Override
    public WorkersSearchResult map(PeopleSearchResult source) {
        WorkersSearchResult.Builder builder = new WorkersSearchResult.Builder();

        if (source.getEmail() != null) {
            builder.withEmail(source.getEmail());
        }
        if (source.getJobTitle() != null) {
            builder.withJobTitle(source.getJobTitle());
        }
        if (source.getUserNumber() != null) {
            builder.withUserNumber(source.getUserNumber());
        }
        if (source.getName().getFirstName() != null) {
            builder.withFirstName(source.getName().getFirstName());
        }
        if (source.getName().getLastName() != null) {
            builder.withLastName(source.getName().getLastName());
        }
        if (source.getCompanyName() != null) {
            builder.getCompany().withName(source.getCompanyName());
        }
        if (source.getLastBackgroundCheckDate() != null) {
            builder.withLastBackgroundCheckDate(source.getLastBackgroundCheckDate());
        }
        if (source.getLastDrugTestDate() != null) {
            builder.withLastDrugTestDate(source.getLastDrugTestDate());
        }
        if (source.getRating() != null) {
            builder.getScoreCard().withRating(source.getRating().getRating());
        }
        builder.getScoreCard().withWorkCancelledCount(source.getWorkCancelledCount());
        builder.getScoreCard().withWorkAbandonedCount(source.getWorkAbandonedCount());
        builder.getScoreCard().withOnTimePercentage(source.getOnTimePercentage());
        builder.getScoreCard().withDeliverableOnTimePercentage(source.getDeliverableOnTimePercentage());
        if (source.getLastAssignedWorkDate() != null) {
            builder.withLastAssignedWorkDate(source.getLastAssignedWorkDate());
        }
        if (source.getCountry() != null) {
            builder.getAddress().withCountry(source.getCountry());
        }
        if (source.getWorkPhone() != null) {
            builder.withWorkPhone(source.getWorkPhone());
        }
        if (source.getMobilePhone() != null) {
            builder.withMobilePhone(source.getMobilePhone());
        }
        if (source.getAddress().getAddressLine1() != null) {
            builder.getAddress().withAddressLine1(source.getAddress().getAddressLine1());
        }
        if (source.getAddress().getAddressLine2() != null) {
            builder.getAddress().withAddressLine2(source.getAddress().getAddressLine2());
        }
        if (source.getAddress().getCity() != null) {
            builder.getAddress().withCity(source.getAddress().getCity());
        }
        if (source.getAddress().getZip() != null) {
            builder.getAddress().withZip(source.getAddress().getZip());
        }
        if (source.getAddress() != null && source.getAddress().getPoint() != null) {
            builder.getAddress().getGeo().withLatitude(source.getAddress().getPoint().getLatitude());
        }
        if (source.getAddress() != null && source.getAddress().getPoint() != null) {
            builder.getAddress().getGeo().withLongitude(source.getAddress().getPoint().getLongitude());
        }
        if (source.getSmallAvatarAssetUri() != null) {
            builder.withSmallAvatarAssetUri(source.getSmallAvatarAssetUri());
        }
        if (CollectionUtils.isNotEmpty(source.getCertifications())) {
            builder.withCertifications(source.getCertifications());
        }
        if (CollectionUtils.isNotEmpty(source.getInsurances())) {
            builder.withInsurances(source.getInsurances());
        }
        if (CollectionUtils.isNotEmpty(source.getGroups())) {
            builder.withGroups(source.getGroups());
        }
        if (CollectionUtils.isNotEmpty(source.getSkillNames())) {
            builder.withSkillNames(source.getSkillNames());
        }
        if (CollectionUtils.isNotEmpty(source.getToolNames())) {
            builder.withToolNames(source.getToolNames());
        }
        return builder.build();
    }
}
