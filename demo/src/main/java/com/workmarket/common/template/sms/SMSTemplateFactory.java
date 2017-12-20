package com.workmarket.common.template.sms;

import com.workmarket.common.template.email.EmailTemplate;

public interface SMSTemplateFactory {
    PhoneVerificationSMSTemplate buildPhoneVerificationSMSTemplate(Long providerId, String toNumber, String msg);
    
    SMSTemplate buildSMSTemplateFromEmailTemplate(Long providerId, String toNumber, String msg, EmailTemplate template);
    
}
