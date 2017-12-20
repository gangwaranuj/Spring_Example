package com.workmarket.domains.compliance.dao;

import com.google.common.collect.Lists;
import com.workmarket.domains.compliance.model.AbstractComplianceRule;
import com.workmarket.domains.compliance.model.ComplianceRuleType;
import com.workmarket.domains.model.requirementset.RequirementType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static ch.lambdaj.function.matcher.HasArgumentWithValue.havingValue;
import static org.hamcrest.Matchers.equalTo;

@Repository
public class ComplianceRuleTypeDAOImpl implements ComplianceRuleTypeDAO {
	private static final Log logger = LogFactory.getLog(ComplianceRuleTypeDAOImpl.class);
	private static final String COMPLIANCE_PACKAGE = "com.workmarket.domains.compliance.model";

	private final Set<Class<? extends AbstractComplianceRule>> klasses;
	private final List<ComplianceRuleType> types = Lists.newArrayList();

	public ComplianceRuleTypeDAOImpl() {
		ResourcesScanner rs = new ResourcesScanner();
		SubTypesScanner sts = new SubTypesScanner(false);
		FilterBuilder    fb = new FilterBuilder().include(FilterBuilder.prefix(COMPLIANCE_PACKAGE));

		ConfigurationBuilder cb = new ConfigurationBuilder()
			.setScanners(sts, rs)
			.setUrls(ClasspathHelper.forPackage(COMPLIANCE_PACKAGE))
			.filterInputsBy(fb);

		Reflections core = new Reflections(cb);

		klasses = core.getSubTypesOf(AbstractComplianceRule.class);
	}

	@PostConstruct
	@SuppressWarnings("unchecked")
	private void loadTypes() {
		for (Class klass : klasses) {
			if (Modifier.isAbstract(klass.getModifiers())) { continue; }

			String humanName = "NoName";
			boolean allowMultiple = false;
			try {
				humanName = (String) klass.getMethod("getHumanTypeName").invoke(null);
				allowMultiple = (boolean) klass.getMethod("allowMultiple").invoke(null);
			} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				logger.error("The unthinkable has happened!", e);
			}
			ComplianceRuleType type = new ComplianceRuleType(klass.getSimpleName(), humanName, allowMultiple);

			this.types.add(type);
		}
	}

	@Override
	public ComplianceRuleType findByName(String name) {
		return selectFirst(this.types, havingValue(on(RequirementType.class).getName(), equalTo(name)));
	}

	@Override
	public List<ComplianceRuleType> findAll() { return this.types; }
}
