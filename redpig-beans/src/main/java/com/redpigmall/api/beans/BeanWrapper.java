package com.redpigmall.api.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.redpigmall.api.beans.exception.BeansException;
import com.redpigmall.api.beans.exception.MethodInvocationException;
import com.redpigmall.api.beans.exception.NotReadablePropertyException;
import com.redpigmall.api.beans.exception.NotWritablePropertyException;
import com.redpigmall.api.beans.exception.NullValueInNestedPathException;
import com.redpigmall.api.beans.exception.PropertyException;
import com.redpigmall.api.beans.exception.TypeMismatchException;
@SuppressWarnings({"rawtypes","unchecked"})
public class BeanWrapper extends AbstractPropertyAccessor {
	private static final Logger logger = LoggerFactory
			.getLogger(BeanWrapper.class);
	private Object object;
	private String nestedPath = "";
	private Object rootObject;
	private TypeConverter typeConverterDelegate;
	private CachedIntrospectionResults cachedIntrospectionResults;
	private Map nestedBeanWrappers;

	public BeanWrapper() {
		this(true);
	}

	public BeanWrapper(boolean registerDefaultEditors) {
		if (registerDefaultEditors) {
			registerDefaultEditors();
		}
		this.typeConverterDelegate = new TypeConverter(this);
	}

	public BeanWrapper(Object object) {
		registerDefaultEditors();
		setWrappedInstance(object);
	}

	public BeanWrapper(Class clazz) {
		registerDefaultEditors();
		setWrappedInstance(BeanUtils.instantiateClass(clazz));
	}

	public BeanWrapper(Object object, String nestedPath, Object rootObject) {
		registerDefaultEditors();
		setWrappedInstance(object, nestedPath, rootObject);
	}

	private BeanWrapper(Object object, String nestedPath, BeanWrapper superBw) {
		setWrappedInstance(object, nestedPath, superBw.getWrappedInstance());
	}

	public void setWrappedInstance(Object object) {
		setWrappedInstance(object, "", null);
	}

	public void setWrappedInstance(Object object, String nestedPath,
			Object rootObject) {
		Assert.notNull(object, "Bean object must not be null");
		this.object = object;
		this.nestedPath = (nestedPath != null ? nestedPath : "");
		this.rootObject = (!"".equals(this.nestedPath) ? rootObject : object);
		this.nestedBeanWrappers = null;
		this.typeConverterDelegate = new TypeConverter(this, object);
		setIntrospectionClass(object.getClass());
	}

	public Object getWrappedInstance() {
		return this.object;
	}

	public Class getWrappedClass() {
		return this.object.getClass();
	}

	public String getNestedPath() {
		return this.nestedPath;
	}

	public Object getRootInstance() {
		return this.rootObject;
	}

	public Class getRootClass() {
		return this.rootObject != null ? this.rootObject.getClass() : null;
	}

	protected void setIntrospectionClass(Class clazz) {
		if ((this.cachedIntrospectionResults == null)
				|| (!this.cachedIntrospectionResults.getBeanClass().equals(
						clazz))) {
			this.cachedIntrospectionResults = CachedIntrospectionResults
					.forClass(clazz);
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors() {
		return this.cachedIntrospectionResults.getBeanInfo()
				.getPropertyDescriptors();
	}

	public PropertyDescriptor getPropertyDescriptor(String propertyName)
			throws BeansException {
		Assert.notNull(propertyName, "Property name must not be null");
		PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
		if (pd != null) {
			return pd;
		}
		throw new PropertyException(getRootClass(), this.nestedPath
				+ propertyName, "No property '" + propertyName + "' found");
	}

	protected PropertyDescriptor getPropertyDescriptorInternal(
			String propertyName) throws BeansException {
		Assert.state(this.object != null,
				"BeanWrapper does not hold a bean instance");
		BeanWrapper nestedBw = getBeanWrapperForPropertyPath(propertyName);
		return nestedBw.cachedIntrospectionResults
				.getPropertyDescriptor(getFinalPath(nestedBw, propertyName));
	}

	public boolean isReadableProperty(String propertyName) {
		Assert.notNull(propertyName, "Property name must not be null");
		try {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd != null) {
				if (pd.getReadMethod() != null) {
					return true;
				}
			} else {
				getPropertyValue(propertyName);
				return true;
			}
		} catch (PropertyException localPropertyException) {
		}
		return false;
	}

	public boolean isWritableProperty(String propertyName) {
		Assert.notNull(propertyName, "Property name must not be null");
		try {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd != null) {
				if (pd.getWriteMethod() != null) {
					return true;
				}
			} else {
				getPropertyValue(propertyName);
				return true;
			}
		} catch (PropertyException localPropertyException) {
		}
		return false;
	}

	public Class getPropertyType(String propertyName) throws BeansException {
		try {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd != null) {
				return pd.getPropertyType();
			}
			Object value = getPropertyValue(propertyName);
			if (value != null) {
				return value.getClass();
			}
			Class editorType = guessPropertyTypeFromEditors(propertyName);
			if (editorType != null) {
				return editorType;
			}
		} catch (PropertyException localPropertyException) {
		}
		return null;
	}

	public Object convertIfNecessary(Object value, Class requiredType)
			throws TypeMismatchException {
		return convertIfNecessary(value, requiredType, null);
	}

	public Object convertIfNecessary(Object value, Class requiredType,
			MethodParameter methodParam) throws TypeMismatchException {
		try {
			return this.typeConverterDelegate.convertIfNecessary(value,
					requiredType, methodParam);
		} catch (IllegalArgumentException ex) {
			throw new TypeMismatchException(value, requiredType, ex);
		}
	}

	private String getFinalPath(BeanWrapper bw, String nestedPath) {
		if (bw == this) {
			return nestedPath;
		}
		return nestedPath.substring(PropertyAccessorUtils
				.getLastNestedPropertySeparatorIndex(nestedPath) + 1);
	}

	protected BeanWrapper getBeanWrapperForPropertyPath(String propertyPath)
			throws BeansException {
		int pos = PropertyAccessorUtils
				.getFirstNestedPropertySeparatorIndex(propertyPath);
		if (pos > -1) {
			String nestedProperty = propertyPath.substring(0, pos);
			String nestedPath = propertyPath.substring(pos + 1);
			BeanWrapper nestedBw = getNestedBeanWrapper(nestedProperty);
			return nestedBw.getBeanWrapperForPropertyPath(nestedPath);
		}
		return this;
	}

	private BeanWrapper getNestedBeanWrapper(String nestedProperty)
			throws BeansException {
		if (this.nestedBeanWrappers == null) {
			this.nestedBeanWrappers = Maps.newHashMap();
		}
		PropertyTokenHolder tokens = getPropertyNameTokens(nestedProperty);
		String canonicalName = tokens.canonicalName;
		Object propertyValue = getPropertyValue(tokens);
		if (propertyValue == null) {
			throw new NullValueInNestedPathException(getRootClass(),
					this.nestedPath + canonicalName);
		}
		BeanWrapper nestedBw = (BeanWrapper) this.nestedBeanWrappers
				.get(canonicalName);
		if ((nestedBw == null)
				|| (nestedBw.getWrappedInstance() != propertyValue)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Creating new nested BeanWrapper for property '"
						+ canonicalName + "'");
			}
			nestedBw = newNestedBeanWrapper(propertyValue, this.nestedPath
					+ canonicalName + ".");

			copyDefaultEditorsTo(nestedBw);
			copyCustomEditorsTo(nestedBw, canonicalName);
			this.nestedBeanWrappers.put(canonicalName, nestedBw);
		} else if (logger.isDebugEnabled()) {
			logger.debug("Using cached nested BeanWrapper for property '"
					+ canonicalName + "'");
		}
		return nestedBw;
	}

	protected BeanWrapper newNestedBeanWrapper(Object object, String nestedPath) {
		return new BeanWrapper(object, nestedPath, this);
	}

	private PropertyTokenHolder getPropertyNameTokens(String propertyName) {
		PropertyTokenHolder tokens = new PropertyTokenHolder();
		String actualName = null;
		List keys = new ArrayList(2);
		int searchIndex = 0;
		while (searchIndex != -1) {
			int keyStart = propertyName.indexOf(PROPERTY_KEY_PREFIX,
					searchIndex);
			searchIndex = -1;
			if (keyStart != -1) {
				int keyEnd = propertyName.indexOf(PROPERTY_KEY_SUFFIX, keyStart
						+ PROPERTY_KEY_PREFIX.length());
				if (keyEnd != -1) {
					if (actualName == null) {
						actualName = propertyName.substring(0, keyStart);
					}
					String key = propertyName.substring(keyStart
							+ PROPERTY_KEY_PREFIX.length(), keyEnd);
					if ((key.startsWith("'") && key.endsWith("'"))
							|| (key.startsWith("\"") && key.endsWith("\""))) {
						key = key.substring(1, key.length() - 1);
					}
					keys.add(key);
					searchIndex = keyEnd + PROPERTY_KEY_SUFFIX.length();
				}
			}
		}
		tokens.actualName = (actualName != null ? actualName : propertyName);
		tokens.canonicalName = tokens.actualName;
		if (!keys.isEmpty()) {
			tokens.canonicalName += PROPERTY_KEY_PREFIX
					+ StringUtils.collectionToDelimitedString(keys,
							PROPERTY_KEY_SUFFIX + PROPERTY_KEY_PREFIX)
					+ PROPERTY_KEY_SUFFIX;
			tokens.keys = StringUtils.toStringArray(keys);
		}
		return tokens;
	}

	public Object getPropertyValue(String propertyName) throws BeansException {
		BeanWrapper nestedBw = getBeanWrapperForPropertyPath(propertyName);
		PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(
				nestedBw, propertyName));
		return nestedBw.getPropertyValue(tokens);
	}

	private Object getPropertyValue(PropertyTokenHolder tokens)
			throws BeansException {
		String propertyName = tokens.canonicalName;
		String actualName = tokens.actualName;
		PropertyDescriptor pd = getPropertyDescriptorInternal(tokens.actualName);
		if ((pd == null) || (pd.getReadMethod() == null)) {
			throw new NotReadablePropertyException(getRootClass(),
					this.nestedPath + propertyName);
		}
		Method readMethod = pd.getReadMethod();
		logger.debug("About to invoke read method [" + readMethod
				+ "] on object of class [" + this.object.getClass().getName()
				+ "]");
		try {
			if (!Modifier.isPublic(readMethod.getDeclaringClass()
					.getModifiers())) {
				readMethod.setAccessible(true);
			}
			Object value = readMethod.invoke(this.object);
			if (tokens.keys != null) {
				for (int i = 0; i < tokens.keys.length; i++) {
					String key = tokens.keys[i];
					if (value == null) {
						throw new NullValueInNestedPathException(
								getRootClass(), this.nestedPath + propertyName,
								"Cannot access indexed value of property referenced in indexed property path '"
										+ propertyName + "': returned null");
					}
					if (value.getClass().isArray()) {
						value = Array.get(value, Integer.parseInt(key));
					} else if ((value instanceof List)) {
						List list = (List) value;
						value = list.get(Integer.parseInt(key));
					} else if ((value instanceof Set)) {
						Set set = (Set) value;
						int index = Integer.parseInt(key);
						if ((index < 0) || (index >= set.size())) {
							throw new PropertyException(
									getRootClass(),
									this.nestedPath + propertyName,
									"Cannot get element with index "
											+ index
											+ " from Set of size "
											+ set.size()
											+ ", accessed using property path '"
											+ propertyName + "'");
						}
						Iterator it = set.iterator();
						for (int j = 0; it.hasNext(); j++) {
							Object elem = it.next();
							if (j == index) {
								value = elem;
								break;
							}
						}
					} else if ((value instanceof Map)) {
						Map<String, Object> map = (Map) value;
						Class mapKeyType = null;
						if (JdkVersion.getJavaVersion() >= 2) {
							mapKeyType = GenericCollectionTypeResolver
									.getMapKeyReturnType(pd.getReadMethod());
						}
						Object convertedMapKey = this.typeConverterDelegate
								.convertIfNecessary(null, null, key, mapKeyType);

						value = map.get(convertedMapKey);
					} else {
						throw new PropertyException(
								getRootClass(),
								this.nestedPath + propertyName,
								"Property referenced in indexed property path '"
										+ propertyName
										+ "' is neither an array nor a List nor a Set nor a Map; returned value was ["
										+ value + "]");
					}
				}
			}
			return value;
		} catch (InvocationTargetException ex) {
			throw new PropertyException(getRootClass(), this.nestedPath
					+ propertyName, "Getter for property '" + actualName
					+ "' threw exception", ex);
		} catch (IllegalAccessException ex) {
			throw new PropertyException(getRootClass(), this.nestedPath
					+ propertyName, "Illegal attempt to get property '"
					+ actualName + "' threw exception", ex);
		} catch (IndexOutOfBoundsException ex) {
			throw new PropertyException(getRootClass(), this.nestedPath
					+ propertyName, "Index of out of bounds in property path '"
					+ propertyName + "'", ex);
		} catch (NumberFormatException ex) {
			throw new PropertyException(getRootClass(), this.nestedPath
					+ propertyName, "Invalid index in property path '"
					+ propertyName + "'", ex);
		}
	}

	public void setPropertyValue(String propertyName, Object value)
			throws BeansException {
		BeanWrapper nestedBw = null;
		try {
			nestedBw = getBeanWrapperForPropertyPath(propertyName);
		} catch (NotReadablePropertyException ex) {
			throw new NotWritablePropertyException(getRootClass(),
					this.nestedPath + propertyName, "Nested property in path '"
							+ propertyName + "' does not exist", ex);
		}
		PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(
				nestedBw, propertyName));
		nestedBw.setPropertyValue(tokens, value);
	}

	private void setPropertyValue(PropertyTokenHolder tokens, Object newValue)
			throws BeansException {
		String propertyName = tokens.canonicalName;
		if (tokens.keys != null) {
			PropertyTokenHolder getterTokens = new PropertyTokenHolder();
			getterTokens.canonicalName = tokens.canonicalName;
			getterTokens.actualName = tokens.actualName;
			getterTokens.keys = new String[tokens.keys.length - 1];
			System.arraycopy(tokens.keys, 0, getterTokens.keys, 0,
					tokens.keys.length - 1);
			Object propValue = null;
			try {
				propValue = getPropertyValue(getterTokens);
			} catch (NotReadablePropertyException ex) {
				throw new NotWritablePropertyException(getRootClass(),
						this.nestedPath + propertyName,
						"Cannot access indexed value in property referenced in indexed property path '"
								+ propertyName + "'", ex);
			}
			String key = tokens.keys[(tokens.keys.length - 1)];
			if (propValue == null) {
				throw new NullValueInNestedPathException(getRootClass(),
						this.nestedPath + propertyName,
						"Cannot access indexed value in property referenced in indexed property path '"
								+ propertyName + "': returned null");
			}
			if (propValue.getClass().isArray()) {
				Class requiredType = propValue.getClass().getComponentType();
				
				Object oldValue = null;
				try {
					Object convertedValue = this.typeConverterDelegate
							.convertIfNecessary(propertyName, oldValue,
									newValue, requiredType);
					Array.set(propValue, Integer.parseInt(key), convertedValue);
				} catch (IllegalArgumentException ex) {
					PropertyChangeEvent pce = new PropertyChangeEvent(
							this.rootObject, this.nestedPath + propertyName,
							oldValue, newValue);
					throw new TypeMismatchException(pce, requiredType, ex);
				} catch (IndexOutOfBoundsException ex) {
					throw new PropertyException(getRootClass(), this.nestedPath
							+ propertyName,
							"Invalid array index in property path '"
									+ propertyName + "'", ex);
				}
			} else if ((propValue instanceof List)) {
				PropertyDescriptor pd = getPropertyDescriptorInternal(tokens.actualName);
				Class requiredType = null;
				if (JdkVersion.getJavaVersion() >= 2) {
					requiredType = GenericCollectionTypeResolver
							.getCollectionReturnType(pd.getReadMethod());
				}
				List list = (List) propValue;
				int index = Integer.parseInt(key);
				Object oldValue = null;
				try {
					Object convertedValue = this.typeConverterDelegate
							.convertIfNecessary(propertyName, oldValue,
									newValue, requiredType);
					if (index < list.size()) {
						list.set(index, convertedValue);
					} else if (index >= list.size()) {
						for (int i = list.size(); i < index; i++) {
							try {
								list.add(null);
							} catch (NullPointerException ex) {
								throw new PropertyException(
										getRootClass(),
										this.nestedPath + propertyName,
										"Cannot set element with index "
												+ index
												+ " in List of size "
												+ list.size()
												+ ", accessed using property path '"
												+ propertyName
												+ "': List does not support filling up gaps with null elements");
							}
						}
						list.add(convertedValue);
					}
				} catch (IllegalArgumentException ex) {
					PropertyChangeEvent pce = new PropertyChangeEvent(
							this.rootObject, this.nestedPath + propertyName,
							oldValue, newValue);
					throw new TypeMismatchException(pce, requiredType, ex);
				}
			} else if ((propValue instanceof Map)) {
				PropertyDescriptor pd = getPropertyDescriptorInternal(tokens.actualName);
				Class mapKeyType = null;
				Class mapValueType = null;
				if (JdkVersion.getJavaVersion() >= 2) {
					mapKeyType = GenericCollectionTypeResolver
							.getMapKeyReturnType(pd.getReadMethod());
					mapValueType = GenericCollectionTypeResolver
							.getMapValueReturnType(pd.getReadMethod());
				}
				Map map = (Map) propValue;
				Object oldValue = null;
				Object convertedMapKey = null;
				Object convertedMapValue = null;
				try {
					convertedMapKey = this.typeConverterDelegate
							.convertIfNecessary(null, null, key, mapKeyType);
				} catch (IllegalArgumentException ex) {
					PropertyChangeEvent pce = new PropertyChangeEvent(
							this.rootObject, this.nestedPath + propertyName,
							oldValue, newValue);
					throw new TypeMismatchException(pce, mapKeyType, ex);
				}
				try {
					convertedMapValue = this.typeConverterDelegate
							.convertIfNecessary(propertyName, oldValue,
									newValue, mapValueType);
				} catch (IllegalArgumentException ex) {
					PropertyChangeEvent pce = new PropertyChangeEvent(
							this.rootObject, this.nestedPath + propertyName,
							oldValue, newValue);
					throw new TypeMismatchException(pce, mapValueType, ex);
				}
				map.put(convertedMapKey, convertedMapValue);
			} else {
				throw new PropertyException(
						getRootClass(),
						this.nestedPath + propertyName,
						"Property referenced in indexed property path '"
								+ propertyName
								+ "' is neither an array nor a List nor a Map; returned value was ["
								+ newValue + "]");
			}
		} else {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if ((pd == null) || (pd.getWriteMethod() == null)) {
				PropertyMatches matches = PropertyMatches.forProperty(
						propertyName, getRootClass());
				throw new NotWritablePropertyException(getRootClass(),
						this.nestedPath + propertyName,
						matches.buildErrorMessage(),
						matches.getPossibleMatches());
			}
			Method readMethod = pd.getReadMethod();
			Method writeMethod = pd.getWriteMethod();
			Object oldValue = null;
			if (readMethod != null) {
				if (!Modifier.isPublic(readMethod.getDeclaringClass()
						.getModifiers())) {
					readMethod.setAccessible(true);
				}
				try {
					oldValue = readMethod.invoke(this.object, new Object[0]);
				} catch (Exception ex) {
					if (logger.isDebugEnabled()) {
						logger.debug(
								"Could not read previous value of property '"
										+ this.nestedPath + propertyName + "'",
								ex);
					}
				}
			}
			try {
				boolean isProx = false;
				if ((newValue != null)
						&& (newValue.getClass().getSimpleName()
								.indexOf("$$Enhancer") > 0)) {
					isProx = true;
				}
				Object convertedValue = isProx ? newValue
						: this.typeConverterDelegate.convertIfNecessary(
								oldValue, newValue, pd);
				if ((pd.getPropertyType().isPrimitive())
						&& ((convertedValue == null) || (""
								.equals(convertedValue)))) {
					throw new IllegalArgumentException("Invalid value ["
							+ newValue + "] for property '" + pd.getName()
							+ "' of primitive type [" + pd.getPropertyType()
							+ "]");
				}
				if (logger.isDebugEnabled()) {
					logger.debug("About to invoke write method [" + writeMethod
							+ "] on object of class ["
							+ this.object.getClass().getName() + "]");
				}
				if (!Modifier.isPublic(writeMethod.getDeclaringClass()
						.getModifiers())) {
					writeMethod.setAccessible(true);
				}
				writeMethod
						.invoke(this.object, new Object[] { convertedValue });
				if (logger.isDebugEnabled()) {
					logger.debug("Invoked write method [" + writeMethod
							+ "] with value of type ["
							+ pd.getPropertyType().getName() + "]");
				}
			} catch (InvocationTargetException ex) {
				PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(
						this.rootObject, this.nestedPath + propertyName,
						oldValue, newValue);
				if ((ex.getTargetException() instanceof ClassCastException)) {
					throw new TypeMismatchException(propertyChangeEvent,
							pd.getPropertyType(), ex.getTargetException());
				}
				throw new MethodInvocationException(propertyChangeEvent,
						ex.getTargetException());
			} catch (IllegalArgumentException ex) {
				PropertyChangeEvent pce = new PropertyChangeEvent(
						this.rootObject, this.nestedPath + propertyName,
						oldValue, newValue);
				throw new TypeMismatchException(pce, pd.getPropertyType(), ex);
			} catch (IllegalAccessException ex) {
				PropertyChangeEvent pce = new PropertyChangeEvent(
						this.rootObject, this.nestedPath + propertyName,
						oldValue, newValue);
				throw new MethodInvocationException(pce, ex);
			}
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("BeanWrapperImpl: wrapping class [");
		sb.append(getWrappedClass().getName()).append("]");
		return sb.toString();
	}

	private static class PropertyTokenHolder {
		private String canonicalName;
		private String actualName;
		private String[] keys;
	}
}
