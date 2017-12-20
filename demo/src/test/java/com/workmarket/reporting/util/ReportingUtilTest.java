package com.workmarket.reporting.util;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.reporting.CustomFieldEntity;
import com.workmarket.domains.model.reporting.Entity;
import com.workmarket.domains.model.reporting.ReportRequestData;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by nick on 7/5/12 3:54 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class ReportingUtilTest {

	@Test
	public void testGetRootProperty() throws Exception {
		String result = ReportingUtil.getRootProperty("theRootPropertyIs.this");
		Assert.assertTrue("this".equals(result));
	}

	@Test
	public void testBuildSortedEntities() throws Exception {

		ReportRequestData entityRequest = new ReportRequestData();

		String key1 = "key one";
		String key2 = "key two";
		String key3 = "key three";

		Set<String> displayKeys = Sets.newHashSet();
		displayKeys.add(key1);
		displayKeys.add(key2);
		displayKeys.add(key3);
		entityRequest.setDisplayKeys(displayKeys);

		entityRequest.setReportName("Sort Test");

		Entity entity1 = new Entity();
		Entity entity2 = new Entity();
		CustomFieldEntity entity3 = new CustomFieldEntity();

		entity1.setOrderBy(2);
		entity2.setOrderBy(0);
		entity3.setOrderBy(1);

		HashMap<String, Entity> entityMap = new HashMap<String, Entity>();
		entityMap.put(key1, entity1);
		entityMap.put(key2, entity2);
		entityRequest.getCallM().put(key3, entity3); // entities are accessed in two different ways

		List<Entity> sortedEntities = ReportingUtil.buildSortedEntities(entityRequest, entityMap);

		Assert.assertTrue(CollectionUtils.isNotEmpty(sortedEntities));
		Assert.assertTrue(Ordering.natural().isOrdered(sortedEntities));
		Assert.assertTrue(sortedEntities.get(0).getOrderBy() == 0);
	}
}
