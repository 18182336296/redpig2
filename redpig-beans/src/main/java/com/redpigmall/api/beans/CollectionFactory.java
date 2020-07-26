package com.redpigmall.api.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.collections.set.ListOrderedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
@SuppressWarnings({"rawtypes"})
public class CollectionFactory {
	private static final Logger logger = LoggerFactory
			.getLogger(CollectionFactory.class);
	private static boolean commonsCollections3xAvailable;

	static {
		if (JdkVersion.getJavaVersion() >= 1) {
			logger.info("JDK 1.4+ collections available");
		}
		try {
			Class.forName("org.apache.commons.collections.map.LinkedMap");
			commonsCollections3xAvailable = true;
			logger.info("Commons Collections 3.x available");
		} catch (ClassNotFoundException ex) {
			commonsCollections3xAvailable = false;
		}
	}

	public static Set createLinkedSetIfPossible(int initialCapacity) {
		if (JdkVersion.getJavaVersion() >= 1) {
			logger.debug("Creating [java.util.LinkedHashSet]");
			return Jdk14CollectionFactory.createLinkedHashSet(initialCapacity);
		}
		if (commonsCollections3xAvailable) {
			logger.debug("Creating [org.apache.commons.collections.set.ListOrderedSet]");
			return CommonsCollectionFactory
					.createListOrderedSet(initialCapacity);
		}
		logger.debug("Falling back to [java.util.HashSet] for linked set");
		return new HashSet(initialCapacity);
	}

	public static Map createLinkedMapIfPossible(int initialCapacity) {
		if (JdkVersion.getJavaVersion() >= 1) {
			logger.debug("Creating [java.util.LinkedHashMap]");
			return Jdk14CollectionFactory.createLinkedHashMap(initialCapacity);
		}
		if (commonsCollections3xAvailable) {
			logger.debug("Creating [org.apache.commons.collections.map.LinkedMap]");
			return CommonsCollectionFactory.createLinkedMap(initialCapacity);
		}
		logger.debug("Falling back to [java.util.HashMap] for linked map");
		return new HashMap(initialCapacity);
	}

	public static Map createLinkedCaseInsensitiveMapIfPossible(
			int initialCapacity) {
		if (commonsCollections3xAvailable) {
			logger.debug("Creating [org.apache.commons.collections.map.ListOrderedMap/CaseInsensitiveMap]");
			return CommonsCollectionFactory
					.createListOrderedCaseInsensitiveMap(initialCapacity);
		}
		if (JdkVersion.getJavaVersion() >= 1) {
			logger.debug("Falling back to [java.util.LinkedHashMap] for linked case-insensitive map");
			return Jdk14CollectionFactory.createLinkedHashMap(initialCapacity);
		}
		logger.debug("Falling back to [java.util.HashMap] for linked case-insensitive map");
		return new HashMap(initialCapacity);
	}

	public static Map createIdentityMapIfPossible(int initialCapacity) {
		if (JdkVersion.getJavaVersion() >= 1) {
			logger.debug("Creating [java.util.IdentityHashMap]");
			return Jdk14CollectionFactory
					.createIdentityHashMap(initialCapacity);
		}
		if (commonsCollections3xAvailable) {
			logger.debug("Creating [org.apache.commons.collections.map.IdentityMap]");
			return CommonsCollectionFactory.createIdentityMap(initialCapacity);
		}
		logger.debug("Falling back to [java.util.HashMap] for identity map");
		return new HashMap(initialCapacity);
	}

	public static Collection createApproximateCollection(Class collectionClass,
			int initialCapacity) {
		Assert.notNull(collectionClass, "Collection class must not be null");
		if (!collectionClass.isInterface()) {
			try {
				return (Collection) collectionClass.newInstance();
			} catch (Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Could not instantiate collection type ["
							+ collectionClass.getName() + "]: "
							+ ex.getMessage());
				}
			}
		}
		if (List.class.isAssignableFrom(collectionClass)) {
			return new ArrayList(initialCapacity);
		}
		if (SortedSet.class.isAssignableFrom(collectionClass)) {
			return Sets.newTreeSet();
		}
		return createLinkedSetIfPossible(initialCapacity);
	}

	public static Map createApproximateMap(Class mapClass, int initialCapacity) {
		Assert.notNull(mapClass, "Map class must not be null");
		if (!mapClass.isInterface()) {
			try {
				return (Map) mapClass.newInstance();
			} catch (Exception ex) {
				logger.debug("Could not instantiate map type ["
						+ mapClass.getName() + "]: " + ex.getMessage());
			}
		}
		if (SortedMap.class.isAssignableFrom(mapClass)) {
			return new TreeMap();
		}
		return createLinkedMapIfPossible(initialCapacity);
	}

	private static abstract class Jdk14CollectionFactory {
		private static Set createLinkedHashSet(int initialCapacity) {
			return new LinkedHashSet(initialCapacity);
		}

		private static Map createLinkedHashMap(int initialCapacity) {
			return new LinkedHashMap(initialCapacity);
		}

		private static Map createIdentityHashMap(int initialCapacity) {
			return new IdentityHashMap(initialCapacity);
		}
	}

	private static abstract class CommonsCollectionFactory {
		private static Set createListOrderedSet(int initialCapacity) {
			return ListOrderedSet.decorate(new HashSet(initialCapacity));
		}

		private static Map createLinkedMap(int initialCapacity) {
			return new LinkedMap(initialCapacity == 0 ? 1 : initialCapacity);
		}

		private static Map createListOrderedCaseInsensitiveMap(
				int initialCapacity) {
			return ListOrderedMap.decorate(new CaseInsensitiveMap(
					initialCapacity == 0 ? 1 : initialCapacity));
		}

		private static Map createIdentityMap(int initialCapacity) {
			return new IdentityMap(initialCapacity == 0 ? 1 : initialCapacity);
		}
	}
}
