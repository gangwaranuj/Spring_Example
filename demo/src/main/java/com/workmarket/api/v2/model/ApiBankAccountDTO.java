package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import com.workmarket.domains.banking.util.BankRoutingUtil;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.model.BankAccountDTO;
import com.workmarket.integration.autotask.util.StringUtil;
import com.workmarket.utility.StringUtilities;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.text.SimpleDateFormat;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Bank Account DTO.
 */
@ApiModel(value = "BankAccount")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ApiBankAccountDTO.Builder.class)
public class ApiBankAccountDTO {

    private final Long id;
    private final Type type;
    private final String name;
    private final String country;
    private final String bankName;
    private final Boolean verified;
    private final String accountHolder;
    private final String accountNumber;
    private final String routingNumber;
    private final AccountType accountType;
    private final String transitBranchNumber;
    private final String financialInstNumber;
    private final Long confirmedOn;
    private final Long createdOn;

    private ApiBankAccountDTO(final Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.country = builder.country;
        this.bankName = builder.bankName;
        this.verified = builder.verified;
        this.accountType = builder.accountType;
        this.accountHolder = builder.accountHolder;
        this.routingNumber = builder.routingNumber;
        this.accountNumber = builder.accountNumber;
        this.transitBranchNumber = builder.transitBranchNumber;
        this.financialInstNumber = builder.financialInstNumber;
        this.confirmedOn = builder.confirmedOn;
        this.createdOn = builder.createdOn;
    }


    @JsonProperty("id")
    @ApiModelProperty(name = "id")
    public Long getId() {
        return id;
    }

    @JsonProperty("type")
    @ApiModelProperty(name = "type")
    public Type getType() {
        return type;
    }

    @JsonProperty("name")
    @ApiModelProperty(name = "name")
    public String getName() {
        return name;
    }

    @JsonProperty("country")
    @ApiModelProperty(name = "country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("bankName")
    @ApiModelProperty(name = "bankName")
    public String getBankName() {
        return bankName;
    }

    @JsonProperty("verified")
    @ApiModelProperty(name = "verified")
    public Boolean getVerified() {
        return verified;
    }

    @JsonProperty("accountType")
    @ApiModelProperty(name = "accountType")
    public AccountType getAccountType() {
        return accountType;
    }

    @JsonProperty("accountHolder")
    @ApiModelProperty(name = "accountHolder")
    public String getAccountHolder() {
        return accountHolder;
    }

    @JsonProperty("routingNumber")
    @ApiModelProperty(name = "routingNumber")
    public String getRoutingNumber() {
        return routingNumber;
    }

    @JsonProperty("accountNumber")
    @ApiModelProperty(name = "accountNumber")
    public String getAccountNumber() {
        return accountNumber;
    }

    @JsonProperty("transitBranchNumber")
    @ApiModelProperty(name = "transitBranchNumber")
    public String getTransitBranchNumber() {
        return transitBranchNumber;
    }

    @JsonProperty("financialInstNumber")
    @ApiModelProperty(name = "financialInstNumber")
    public String getFinancialInstNumber() {
        return financialInstNumber;
    }

    @JsonProperty("confirmedOn")
    @ApiModelProperty(name = "confirmedOn")
    public Long getConfirmedOn() {
        return confirmedOn;
    }

    @JsonProperty("createdOn")
    @ApiModelProperty(name = "createdOn")
    public Long getCreatedOn() {
        return createdOn;
    }

    public BankAccountDTO toBankAccountDTO() {
        final BankAccountDTO dto = new BankAccountDTO();

        dto.setCountry(this.country);
        dto.setCountryCode(this.country);
        dto.setType(this.type != null ? this.type.type() : null);
        dto.setNameOnAccount(!StringUtil.isNullOrEmpty(this.accountHolder) ? this.accountHolder : this.name);

        if (Type.ACH.equals(this.type)) {
            dto.setBankName(this.bankName);
            dto.setRoutingNumber(this.routingNumber);
            dto.setAccountNumber(this.accountNumber);
            dto.setAccountNumberConfirm(this.accountNumber);
            dto.setBankAccountTypeCode(this.accountType != null ? this.accountType.type() : null);

            // add canada only properties
            if (Country.CANADA.equals(this.country)) {
                dto.setBranchNumber(this.transitBranchNumber);
                dto.setInstitutionNumber(this.financialInstNumber);
            }
        }

        if (Type.PPA.equals(this.type)) {
            dto.setEmailAddress(this.name);
            dto.setBankName("PayPal");
        }

        return dto;
    }

    public Map<String, Serializable> toMap() {
        return new HashMap<String, Serializable>(){{
            put("id", id);
            put("name", name);
            put("country", country);
            put("bankName", bankName);
            put("verified", verified);
            put("routingNumber", routingNumber);
            put("accountNumber", accountNumber);
            put("transitBranchNumber", transitBranchNumber);
            put("financialInstNumber", financialInstNumber);
            put("type", type != null ? type.type() : null);
            put("accountType", accountType != null ? accountType.type() : null);
            put("accountHolder", (!StringUtil.isNullOrEmpty(accountHolder) ? accountHolder : name));
        }};
    }

    public enum Type {
        ACH(AbstractBankAccount.ACH),
        GCC(AbstractBankAccount.GCC),
        PPA(AbstractBankAccount.PAYPAL);

        private final String type;

        Type(final String type) {
            this.type = type;
        }

        public String type() {
            return this.type;
        }

        @JsonCreator
        public static Type of(final String code) {
            for (final Type e : values()) {
                if (e.type().equalsIgnoreCase(code)) {
                    return e;
                }
            }

            throw new RuntimeException(String.format("Unknown type %s", code));
        }
    }

    public enum AccountType {
        CHECKING(BankAccountType.CHECKING),
        SAVINGS(BankAccountType.SAVINGS);

        private final String type;

        AccountType(final String type) {
            this.type = type;
        }

        public String type() {
            return this.type;
        }

        @JsonCreator
        public static AccountType of(final String code) {
            for (final AccountType e : values()) {
                if (e.type().equalsIgnoreCase(code)) {
                    return e;
                }
            }

            throw new RuntimeException(String.format("Unknown account type %s", code));
        }
    }

    public static class Builder implements AbstractBuilder<ApiBankAccountDTO> {
        private Long id;
        private Type type;
        private String name;
        private String country;
        private String bankName;
        private Boolean verified;
        private String accountHolder;
        private String routingNumber;
        private String accountNumber;
        private AccountType accountType;
        private String transitBranchNumber;
        private String financialInstNumber;
        private Long confirmedOn;
        private Long createdOn;

        public Builder() { }

        public Builder(final ApiBankAccountDTO dto) {
            this.id = dto.id;
            this.name = dto.name;
            this.type = dto.type;
            this.country = dto.country;
            this.bankName = dto.bankName;
            this.verified = dto.verified;
            this.accountType = dto.accountType;
            this.routingNumber = dto.routingNumber;
            this.accountNumber = dto.accountNumber;
            this.transitBranchNumber = dto.transitBranchNumber;
            this.financialInstNumber = dto.financialInstNumber;
            this.confirmedOn =  dto.confirmedOn;
            this.createdOn = dto.createdOn;
        }

        public Builder(final AbstractBankAccount entity) {

            this.id = entity.getId();
            this.bankName = entity.getBankName();
            this.verified = entity.getConfirmedFlag();
            this.name = entity.getAccountDescription();
            this.type = Type.valueOf(entity.getType());

            if (entity.getCountry() != null) {
                this.country = entity.getCountry().getId();
            }

            if (entity.getCreatedOn() != null) {
                this.createdOn = entity.getCreatedOn().getTimeInMillis();
            }

            if (entity instanceof BankAccount) {
                final BankAccount bankAccount = (BankAccount) entity;

                this.accountHolder = bankAccount.getNameOnAccount();
                this.accountNumber = "XXXXXXXX" + StringUtilities.getBankAccountLastFourDigits(bankAccount.getAccountNumber());

                if (bankAccount.getConfirmedOn() != null) {
                    this.confirmedOn = bankAccount.getConfirmedOn().getTimeInMillis();
                }

                // add canada only properties
                if (Country.CANADA_COUNTRY.equals(Country.valueOf(this.country))) {
                    this.transitBranchNumber = BankRoutingUtil.getBranchNumber(bankAccount.getRoutingNumber());
                    this.financialInstNumber = BankRoutingUtil.getInstitutionNumber(bankAccount.getRoutingNumber());
                }

                // add usa only properties
                if (Country.USA_COUNTRY.equals(Country.valueOf(this.country))) {
                    this.routingNumber = bankAccount.getRoutingNumber();
                }

                if (bankAccount.getBankAccountType() != null) {
                    this.accountType = AccountType.of(bankAccount.getBankAccountType().getCode());
                }
            }
        }

        @JsonProperty("id")
        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        @JsonProperty("type")
        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        @JsonProperty("country")
        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        @JsonProperty("name")
        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        @JsonProperty("accountType")
        public Builder setAccountType(AccountType accountType) {
            this.accountType = accountType;
            return this;
        }

        @JsonProperty("accountHolder")
        public Builder setAccountHolder(String accountHolder) {
            this.accountHolder = accountHolder;
            return this;
        }

        @JsonProperty("routingNumber")
        public Builder setRoutingNumber(String routingNumber) {
            this.routingNumber = routingNumber;
            return this;
        }

        @JsonProperty("accountNumber")
        public Builder setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        @JsonProperty("transitBranchNumber")
        public Builder setTransitBranchNumber(String transitBranchNumber) {
            this.transitBranchNumber = transitBranchNumber;
            return this;
        }

        @JsonProperty("financialInstNumber")
        public Builder setFinancialInstNumber(String financialInstNumber) {
            this.financialInstNumber = financialInstNumber;
            return this;
        }

        @JsonProperty("bankName")
        public Builder setBankName(String bankName) {
            this.bankName = bankName;
            return this;
        }

        @JsonProperty("verified")
        public Builder setVerified(Boolean verified) {
            this.verified = verified;
            return this;
        }

        @JsonProperty("confirmedOn")
        public Builder setConfirmedOn(Long confirmedOn) {
            this.confirmedOn = confirmedOn;
            return this;
        }

        @JsonProperty("createdOn")
        public Builder setCreatedOn(Long createdOn) {
        	this.createdOn = createdOn;
        	return this;
        }

        @Override
        public ApiBankAccountDTO build() {
            return new ApiBankAccountDTO(this);
        }
    }
}
