package com.workmarket.web.editors;

import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.service.business.CRMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;

@Component("clientCompanyEditor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientCompanyEditor extends PropertyEditorSupport {
    @Autowired CRMService crmService;

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        ClientCompany clientCompany = null;
        
        if (StringUtils.hasText(text)) {
            try {
                Long id = Long.valueOf(text);
                clientCompany = crmService.findClientCompanyById(id);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Unable to parse '"+text+"' to Long!", ex);
            }
        }
        
        setValue(clientCompany);
    }

    @Override
    public String getAsText() {
        ClientCompany clientCompany = (ClientCompany)getValue();
        return (clientCompany == null) ? null : String.valueOf(clientCompany.getId());
    }
}
