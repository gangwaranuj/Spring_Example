package com.workmarket.testutils;

import com.google.api.client.util.Sets;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Collection;

/**
 * Created by nick on 3/13/15 6:52 PM
 */
public class TestReflectionUtils {

	/**
	 * Returns all loaded classes under the given package. Useful for parameterized tests.
	 * @param packageName
	 * @return
	 */
	public static Collection<Object[]> getClasses(String packageName) {

		Collection<Object[]> result = Sets.newHashSet();

		try {
			ImmutableSet<ClassPath.ClassInfo> classes = ClassPath
					.from(URLClassLoader.getSystemClassLoader())
					.getTopLevelClassesRecursive(packageName);
			for (ClassPath.ClassInfo clazz : classes) {
				result.add(new Object[]{Class.forName(clazz.getName())});
			}
		} catch (IOException | ClassNotFoundException e) {
			// Ignore.
		}

		return result;
	}
}
