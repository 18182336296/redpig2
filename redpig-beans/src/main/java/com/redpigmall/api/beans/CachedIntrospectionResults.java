package com.redpigmall.api.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.redpigmall.api.beans.exception.BeansException;
@SuppressWarnings({"rawtypes","unchecked"})
final class CachedIntrospectionResults {
	private static final Logger logger = LoggerFactory
			.getLogger(CachedIntrospectionResults.class);
	private static final Map classCache = Collections
			.synchronizedMap(new WeakHashMap());
	private final BeanInfo beanInfo;
	private final Map propertyDescriptorCache;

	public static CachedIntrospectionResults forClass(Class beanClass)
			throws BeansException {
		CachedIntrospectionResults results = null;
		Object value = classCache.get(beanClass);
		if ((value instanceof Reference)) {
			Reference ref = (Reference) value;
			results = (CachedIntrospectionResults) ref.get();
		} else {
			results = (CachedIntrospectionResults) value;
		}
		if (results == null) {
			results = new CachedIntrospectionResults(beanClass);
			boolean cacheSafe = isCacheSafe(beanClass);

			logger.debug("Class [" + beanClass.getName() + "] is "
					+ (!cacheSafe ? "not " : "") + "cache-safe");
			if (cacheSafe) {
				classCache.put(beanClass, results);
			} else {
				classCache.put(beanClass, new WeakReference(results));
			}
		} else {
			logger.debug("Using cached introspection results for class ["
					+ beanClass.getName() + "]");
		}
		return results;
	}

	private static boolean isCacheSafe(Class clazz) {
		ClassLoader cur = CachedIntrospectionResults.class.getClassLoader();
		ClassLoader target = clazz.getClassLoader();
		if ((target == null) || (cur == target)) {
			return true;
		}
		while (cur != null) {
			cur = cur.getParent();
			if (cur == target) {
				return true;
			}
		}
		return false;
	}

	private CachedIntrospectionResults(Class clazz) throws BeansException {
		try {
			logger.debug("Getting BeanInfo for class [" + clazz.getName() + "]");

			this.beanInfo = Introspector.getBeanInfo(clazz);

			Class classToFlush = clazz;
			do {
				Introspector.flushFromCaches(classToFlush);
				classToFlush = classToFlush.getSuperclass();
			} while (classToFlush != null);
			this.propertyDescriptorCache = Maps.newHashMap();

			PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
			for (int i = 0; i < pds.length; i++) {
				this.propertyDescriptorCache.put(pds[i].getName(), pds[i]);
			}
		} catch (IntrospectionException ex) {
			throw new BeansException(
					"Cannot get BeanInfo for object of class ["
							+ clazz.getName() + "]", ex);
		}
	}

	public BeanInfo getBeanInfo() {
		return this.beanInfo;
	}

	public Class getBeanClass() {
		return this.beanInfo.getBeanDescriptor().getBeanClass();
	}

	public PropertyDescriptor getPropertyDescriptor(String propertyName) {
		return (PropertyDescriptor) this.propertyDescriptorCache
				.get(propertyName);
	}
}
