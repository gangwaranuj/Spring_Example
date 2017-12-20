package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.search.response.work.DashboardResource;
import com.workmarket.search.model.WorkSearchResponse;
import io.swagger.annotations.ApiModel;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * DTO extracted from original Map Response
 */
@ApiModel(value = "AssignmentSearchResponse")
@JsonDeserialize(builder = AssignmentSearchResponseDTO.Builder.class)
public class AssignmentSearchResponseDTO {

    private final Integer resultsCount;

    private final Integer totalResultsCount;

    private final boolean showBulkOps;

    private final List<String> resultIds;

    private final List<Map<String, Object>> data;

    private final Map<String, Object> statusCounts;

    private final Map<String, Object> subStatusCounts;

    private final Map<Long, DashboardResource> assignedResources;

    @JsonProperty("resultsCount")
    public Integer getResultsCount() {
        return resultsCount;
    }

    @JsonProperty("totalResultsCount")
    public Integer getTotalResultsCount() {
        return totalResultsCount;
    }

    @JsonProperty("showBulkOps")
    public boolean isShowBulkOps() {
        return showBulkOps;
    }

    @JsonProperty("resultIds")
    public List<String> getResultIds() {
        return resultIds;
    }

    @JsonProperty("data")
    public List<Map<String, Object>> getData() {
        return data;
    }

    @JsonProperty("statusCounts")
    public Map<String, Object> getStatusCounts() {
        return statusCounts;
    }

    @JsonProperty("subStatusCounts")
    public Map<String, Object> getSubStatusCounts() {
        return subStatusCounts;
    }

    @JsonProperty("assignedResources")
    public Map<Long, DashboardResource> getAssignedResources() {
        return assignedResources;
    }

    private AssignmentSearchResponseDTO(Builder builder) {
        this.resultsCount = builder.resultsCount;
        this.totalResultsCount = builder.totalResultsCount;
        this.showBulkOps = builder.showBulkOps;
        this.resultIds = builder.resultIds;
        this.data = builder.data;
        this.statusCounts = builder.statusCounts;
        this.subStatusCounts = builder.subStatusCounts;
        this.assignedResources = builder.assignedResources;
    }

    public static class Builder implements AbstractBuilder<AssignmentSearchResponseDTO> {
        private Integer resultsCount;
        private Integer totalResultsCount;
        private boolean showBulkOps;
        private List<String> resultIds;
        private List<Map<String, Object>> data;
        private Map<String, Object> statusCounts;
        private Map<String, Object> subStatusCounts;
        private Map<Long, DashboardResource> assignedResources;

        public Builder(WorkSearchResponse searchResponse) {
            this.resultsCount = searchResponse.getResultsCount();
            this.totalResultsCount = searchResponse.getTotalResultsCount();
            this.showBulkOps = searchResponse.isShowBulkOps();
            this.resultIds = searchResponse.getResultIds();
            this.data = searchResponse.getData();
            this.statusCounts = searchResponse.getStatusCounts();
            this.subStatusCounts = searchResponse.getSubStatusCounts();
            this.assignedResources = searchResponse.getAssignedResources();
        }

        public Builder() {}

        @JsonProperty("resultsCount")
        public Builder withResultsCount(Integer resultsCount) {
            this.resultsCount = resultsCount;
            return this;
        }

        @JsonProperty("totalResultsCount")
        public Builder withTotalResultsCount(Integer totalResultsCount) {
            this.totalResultsCount = totalResultsCount;
            return this;
        }

        @JsonProperty("showBulkOps")
        public Builder withShowBulkOps(boolean showBulkOps) {
            this.showBulkOps = showBulkOps;
            return this;
        }

        @JsonProperty("resultIds")
        public Builder withResultIds(List<String> resultIds) {
            this.resultIds = resultIds;
            return this;
        }

        @JsonProperty("data")
        public Builder withData(List<Map<String, Object>> data) {
            this.data = data;
            return this;
        }

        public List<Map<String, Object>> getData() {
            return data;
        }

        @JsonProperty("statusCounts")
        public Builder withStatusCounts(Map<String, Object> statusCounts) {
            this.statusCounts = statusCounts;
            return this;
        }

        @JsonProperty("subStatusCounts")
        public Builder withSubStatusCounts(Map<String, Object> subStatusCounts) {
            this.subStatusCounts = subStatusCounts;
            return this;
        }

        @JsonProperty("assignedResources")
        public Builder withAssignedResources(Map<Long, DashboardResource> assignedResource) {
            this.assignedResources = assignedResource;
            return this;
        }

        @Override
        public AssignmentSearchResponseDTO build() {
            return new AssignmentSearchResponseDTO(this);
        }
    }
}
