package com.workmarket.dao.requirement;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.RequirementType;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static ch.lambdaj.function.matcher.HasArgumentWithValue.havingValue;
import static org.hamcrest.Matchers.equalTo;

@Repository
public class RequirementTypeDAOImpl implements RequirementTypeDAO {

	private final Reflections core = new Reflections(
		new ConfigurationBuilder()
			.setScanners(new SubTypesScanner(false), new ResourcesScanner())
			.setUrls(ClasspathHelper.forPackage("com.workmarket.domains.model.requirementset"))
			.filterInputsBy(
				new FilterBuilder()
					.include(FilterBuilder.prefix("com.workmarket.domains.model.requirementset"))
			)
	);

	private final Set<Class<? extends AbstractRequirement>> klasses = core.getSubTypesOf(AbstractRequirement.class);
	private final List<RequirementType> types = Lists.newArrayList();
	private boolean loaded = false;

	@Override
	public RequirementType findByName(String name) {
		return selectFirst(this.types, havingValue(on(RequirementType.class).getName(), equalTo(name)));
	}

	@Override
	public List<RequirementType> findAll() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
		this.loadTypes();
		return this.types;
	}

	private void loadTypes() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
		if (this.loaded) {return;} // Should only load once
		for (Class klass : klasses) {

			if ("AbstractExpirableRequirement".equals(klass.getSimpleName())) { continue; }

			// Note: This does look suspicious, but because klass is scoped above to AbstractRequirement,
			// this technically should never throw a run-time exception. And if it does, it should be exceptional
			RequirementType type = new RequirementType(
				klass.getSimpleName(),
				(String) klass.getDeclaredField("HUMAN_NAME").get(null),
				((AbstractRequirement) klass.newInstance()).allowMultiple(),
				(String[]) klass.getDeclaredField("FILTERS").get(null)
			);

			this.types.add(type);
		}
		this.loaded = true;
	}
}
