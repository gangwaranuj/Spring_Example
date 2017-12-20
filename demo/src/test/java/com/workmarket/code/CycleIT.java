package com.workmarket.code;

import com.google.common.collect.Lists;
import com.workmarket.test.IntegrationTest;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTest.class)
public class CycleIT {

	private JDepend jdepend;

	@Before
	public void setup() throws Exception {
		jdepend = new JDepend();
		jdepend.addDirectory("target/classes");
		jdepend.setComponents("com.workmarket.domains.model");
		jdepend.analyze();
		List cycles = Lists.newArrayList();
		for (Object javaPackage : jdepend.getPackages()){
			((JavaPackage) javaPackage).collectAllCycles(cycles);
		}
	}

	@Test
	public void com_workmarket_utility_HasNoCycles() throws IOException {
		JavaPackage p = jdepend.getPackage("com.workmarket.utility");
		assertThat(p.containsCycle(), is(false));
	}

	@Test
	public void com_workmarket_configuration_HasNoCycles() throws IOException {
		JavaPackage p = jdepend.getPackage("com.workmarket.configuration");
		assertThat(p.containsCycle(), is(false));
	}

	@Test
	public void com_workmarket_domains_model_DoesNotDependOnDTOs() throws Exception {
		JavaPackage models = jdepend.getPackage("com.workmarket.domains.model");
		JavaPackage dtos = jdepend.getPackage("com.workmarket.service.business.dto");
		// Afferent Couplings (Ca)
		// Other packages that depend upon classes within this package. Indicates this package's responsibility.
		assertThat("DTOs should not be responsible for Models", dtos.getAfferents().contains(models), is(false));

		// Efferent Couplings (Ce)
		// Other packages that the classes in this package depend upon. Indicates the package's independence.
		assertThat("Models should not depend on DTOs", models.getEfferents().contains(dtos), is(false));
	}

	@Test
	public void com_workmarket_dao_DoesNotDependOnServices() throws Exception {
		JavaPackage daos = jdepend.getPackage("com.workmarket.dao");
		JavaPackage services = jdepend.getPackage("com.workmarket.service.business");
		// Afferent Couplings (Ca)
		// Other packages that depend upon classes within this package. Indicates this package's responsibility.
		assertThat("Services should not be responsible for DAOs", services.getAfferents().contains(daos), is(false));

		// Efferent Couplings (Ce)
		// Other packages that the classes in this package depend upon. Indicates the package's independence.
		assertThat("DAOs should not depend on Services", daos.getEfferents().contains(services), is(false));
	}
}
