package com.workmarket.domains.reports.model;

import java.util.List;

/**
 * Created by ianha on 2/2/15.
 */
public class CustomReportCustomFieldGroupDTO {
   long id;
   String name;
   boolean deleted;
   List<CustomReportCustomFieldDTO> customFields;

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public boolean isDeleted() {
      return deleted;
   }

   public void setDeleted(boolean deleted) {
      this.deleted = deleted;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<CustomReportCustomFieldDTO> getCustomFields() {
      return customFields;
   }

   public void setCustomFields(List<CustomReportCustomFieldDTO> customFields) {
      this.customFields = customFields;
   }
}
