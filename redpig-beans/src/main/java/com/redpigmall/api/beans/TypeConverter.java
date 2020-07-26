package com.redpigmall.api.beans;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@SuppressWarnings({"rawtypes","unchecked"})
class TypeConverter {
	private static final Logger logger = LoggerFactory
			.getLogger(TypeConverter.class);
	private final PropertyEditorRegistry propertyEditorRegistry;
	private final Object targetObject;

	public TypeConverter(PropertyEditorRegistry propertyEditorRegistry) {
		this(propertyEditorRegistry, null);
	}

	public TypeConverter(PropertyEditorRegistry propertyEditorRegistry,
			Object targetObject) {
		Assert.notNull(propertyEditorRegistry,
				"Property editor registry must not be null");
		this.propertyEditorRegistry = propertyEditorRegistry;
		this.targetObject = targetObject;
	}

	public Object convertIfNecessary(Object newValue, Class requiredType,
			MethodParameter methodParam) throws IllegalArgumentException {
		return convertIfNecessary(null, null, newValue, requiredType, null,
				methodParam);
	}

	public Object convertIfNecessary(String propertyName, Object oldValue,
			Object newValue, Class requiredType)
			throws IllegalArgumentException {
		return convertIfNecessary(propertyName, oldValue, newValue,
				requiredType, null, null);
	}

	public Object convertIfNecessary(Object oldValue, Object newValue,
			PropertyDescriptor descriptor) throws IllegalArgumentException {
		Assert.notNull(descriptor, "PropertyDescriptor must not be null");
		return convertIfNecessary(descriptor.getName(), oldValue, newValue,
				descriptor.getPropertyType(), descriptor, new MethodParameter(
						descriptor.getWriteMethod(), 0));
	}

	protected Object convertIfNecessary(String propertyName, Object oldValue,
			Object newValue, Class requiredType, PropertyDescriptor descriptor,
			MethodParameter methodParam) throws IllegalArgumentException {
		Object convertedValue = newValue;

		PropertyEditor pe = this.propertyEditorRegistry.findCustomEditor(
				requiredType, propertyName);
		if ((pe != null)
				|| ((requiredType != null) && (!ClassUtils.isAssignableValue(
						requiredType, convertedValue)))) {
			if ((pe == null) && (descriptor != null)) {
				if (JdkVersion.getJavaVersion() >= 2) {
					pe = descriptor.createPropertyEditor(this.targetObject);
				} else {
					Class editorClass = descriptor.getPropertyEditorClass();
					if (editorClass != null) {
						pe = (PropertyEditor) BeanUtils
								.instantiateClass(editorClass);
					}
				}
			}
			if ((pe == null) && (requiredType != null)) {
				pe = this.propertyEditorRegistry.getDefaultEditor(requiredType);
				if (pe == null) {
					pe = PropertyEditorManager.findEditor(requiredType);
				}
			}
			convertedValue = convertValue(convertedValue, requiredType, pe,
					oldValue);
		}
		if (requiredType != null) {
			if ((convertedValue != null) && (requiredType.isArray())) {
				return convertToTypedArray(convertedValue, propertyName,
						requiredType.getComponentType());
			}
			if (((convertedValue instanceof Collection))
					&& (Collection.class.isAssignableFrom(requiredType))) {
				return convertToTypedCollection((Collection) convertedValue,
						propertyName, methodParam);
			}
			if (((convertedValue instanceof Map))
					&& (Map.class.isAssignableFrom(requiredType))) {
				return convertToTypedMap((Map) convertedValue, propertyName,
						methodParam);
			}
			if (((convertedValue instanceof String))
					&& (!requiredType.isInstance(convertedValue))) {
				try {
					Field enumField = requiredType
							.getField((String) convertedValue);
					convertedValue = enumField.get(null);
				} catch (Exception ex) {
					logger.debug("Field [" + convertedValue
							+ "] isn't an enum value", ex);
				}
			}
			// if (!ClassUtils.isAssignableValue(requiredType, convertedValue))
			// {
			// throw new IllegalArgumentException(
			// "No matching editors or conversion strategy found");
			// }
		}
		return convertedValue;
	}

	protected Object convertValue(Object newValue, Class requiredType,
			PropertyEditor pe, Object oldValue) {
		Object convertedValue = newValue;
		if ((pe != null) && (!(convertedValue instanceof String))) {
			pe.setValue(convertedValue);
			Object newConvertedValue = pe.getValue();
			if (newConvertedValue != convertedValue) {
				convertedValue = newConvertedValue;

				pe = null;
			}
		}
		if ((requiredType != null) && (!requiredType.isArray())
				&& ((convertedValue instanceof String[]))) {
			logger.debug("Converting String array to comma-delimited String ["
					+ convertedValue + "]");

			convertedValue = StringUtils
					.arrayToCommaDelimitedString((String[]) convertedValue);
		}
		if ((pe != null) && ((convertedValue instanceof String))) {
			logger.debug("Converting String to [" + requiredType
					+ "] using property editor [" + pe + "]");

			pe.setValue(oldValue);
			pe.setAsText((String) convertedValue);
			convertedValue = pe.getValue();
		}
		return convertedValue;
	}

	protected Object convertToTypedArray(Object input, String propertyName,
			Class componentType) {
		if ((input instanceof Collection)) {
			Collection coll = (Collection) input;
			Object result = Array.newInstance(componentType, coll.size());
			int i = 0;
			for (Iterator it = coll.iterator(); it.hasNext(); i++) {
				Object value = convertIfNecessary(
						buildIndexedPropertyName(propertyName, i), null,
						it.next(), componentType);
				Array.set(result, i, value);
			}
			return result;
		}
		if (input.getClass().isArray()) {
			int arrayLength = Array.getLength(input);
			Object result = Array.newInstance(componentType, arrayLength);
			for (int i = 0; i < arrayLength; i++) {
				Object value = convertIfNecessary(
						buildIndexedPropertyName(propertyName, i), null,
						Array.get(input, i), componentType);
				Array.set(result, i, value);
			}
			return result;
		}
		Object result = Array.newInstance(componentType, 1);
		Object value = convertIfNecessary(
				buildIndexedPropertyName(propertyName, 0), null, input,
				componentType);
		Array.set(result, 0, value);
		return result;
	}

	protected Collection convertToTypedCollection(Collection original,
			String propertyName, MethodParameter methodParam) {
		Class elementType = null;
		if ((methodParam != null) && (JdkVersion.getJavaVersion() >= 2)) {
			elementType = GenericCollectionTypeResolver
					.getCollectionParameterType(methodParam);
		}
		Collection convertedCopy = CollectionFactory
				.createApproximateCollection(original.getClass(),
						original.size());

		boolean actuallyConverted = false;
		int i = 0;
		for (Iterator it = original.iterator(); it.hasNext(); i++) {
			Object element = it.next();
			String indexedPropertyName = buildIndexedPropertyName(propertyName,
					i);
			Object convertedElement = convertIfNecessary(indexedPropertyName,
					null, element, elementType);
			convertedCopy.add(convertedElement);
			actuallyConverted = (actuallyConverted)
					|| (element != convertedElement);
		}
		return actuallyConverted ? convertedCopy : original;
	}

	protected Map convertToTypedMap(Map original, String propertyName,
			MethodParameter methodParam) {
		Class keyType = null;
		Class valueType = null;
		if ((methodParam != null) && (JdkVersion.getJavaVersion() >= 2)) {
			keyType = GenericCollectionTypeResolver
					.getMapKeyParameterType(methodParam);
			valueType = GenericCollectionTypeResolver
					.getMapValueParameterType(methodParam);
		}
		Map convertedCopy = CollectionFactory.createApproximateMap(
				original.getClass(), original.size());
		boolean actuallyConverted = false;
		for (Iterator it = original.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			String keyedPropertyName = buildKeyedPropertyName(propertyName, key);
			Object convertedKey = convertIfNecessary(keyedPropertyName, null,
					key, keyType);
			Object convertedValue = convertIfNecessary(keyedPropertyName, null,
					value, valueType);
			convertedCopy.put(convertedKey, convertedValue);
			actuallyConverted = (actuallyConverted) || (key != convertedKey)
					|| (value != convertedValue);
		}
		return actuallyConverted ? convertedCopy : original;
	}

	private String buildIndexedPropertyName(String propertyName, int index) {
		return propertyName != null ? propertyName + "[" + index + "]" : null;
	}

	private String buildKeyedPropertyName(String propertyName, Object key) {
		return propertyName != null ? propertyName + "[" + key + "]" : null;
	}
}
