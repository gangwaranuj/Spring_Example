package com.workmarket.web.forms;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class CompanyCreditsForm
{
    private Integer credits;
    private Integer confirmCredits;
    private String workCreditType;
    private String reason;

    public Integer getCredits()
    {
        return credits;
    }

    public void setCredits(Integer credits)
    {
        this.credits = credits;
    }

    public Integer getConfirmCredits()
    {
        return confirmCredits;
    }

    public void setConfirmCredits(Integer confirmCredits)
    {
        this.confirmCredits = confirmCredits;
    }

    public String getWorkCreditType()
    {
        return workCreditType;
    }

    public void setWorkCreditType(String workCreditType)
    {
        this.workCreditType = workCreditType;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
