package com.workmarket.service.business;

import com.workmarket.web.forms.RegisterUserForm;

public interface SalesforceLeadService {
  String authenticateToken();

  void generateBuyerLead(String authToken, RegisterUserForm form);

  void generateBuyerLead(String authToken,
                         String firstName,
                         String lastName,
                         String companyName,
                         String workPhone,
                         String city,
                         String country,
                         String postalCode,
                         String state,
                         String address,
                         String userEmail,
                         Long companyId,
                         String title,
                         String function);
}
