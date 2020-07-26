package com.redpigmall.api.beans;

import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.beans.propertyeditors.ByteArrayPropertyEditor;
import com.redpigmall.api.beans.propertyeditors.CharArrayPropertyEditor;
import com.redpigmall.api.beans.propertyeditors.CharacterEditor;
import com.redpigmall.api.beans.propertyeditors.ClassArrayEditor;
import com.redpigmall.api.beans.propertyeditors.ClassEditor;
import com.redpigmall.api.beans.propertyeditors.CustomBooleanEditor;
import com.redpigmall.api.beans.propertyeditors.CustomCollectionEditor;
import com.redpigmall.api.beans.propertyeditors.CustomNumberEditor;
import com.redpigmall.api.beans.propertyeditors.DateEditor;
import com.redpigmall.api.beans.propertyeditors.LocaleEditor;
import com.redpigmall.api.beans.propertyeditors.PropertiesEditor;
@SuppressWarnings({"rawtypes","unchecked"})
public class PropertyEditorRegistry {
	private static Map defaultEditors;
	private Map customEditors;
	private Map customEditorCache;

	protected void registerDefaultEditors() {
		if (defaultEditors != null) {
			return;
		}
		defaultEditors = new HashMap(29);

		defaultEditors.put(Class.class, new ClassEditor());
		defaultEditors.put(Class[].class, new ClassArrayEditor());
		defaultEditors.put(Locale.class, new LocaleEditor());
		defaultEditors.put(Properties.class, new PropertiesEditor());

		defaultEditors.put(Collection.class, new CustomCollectionEditor(
				Collection.class));
		defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
		defaultEditors.put(SortedSet.class, new CustomCollectionEditor(
				SortedSet.class));
		defaultEditors.put(List.class, new CustomCollectionEditor(List.class));

		defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
		defaultEditors.put(char[].class, new CharArrayPropertyEditor());

		PropertyEditor characterEditor = new CharacterEditor(false);
		PropertyEditor booleanEditor = new CustomBooleanEditor(false);

		defaultEditors.put(Character.TYPE, characterEditor);
		defaultEditors.put(Character.class, characterEditor);

		defaultEditors.put(Boolean.TYPE, booleanEditor);
		defaultEditors.put(Boolean.class, booleanEditor);

		PropertyEditor byteEditor = new CustomNumberEditor(Byte.class, true);
		PropertyEditor shortEditor = new CustomNumberEditor(Short.class, true);
		PropertyEditor integerEditor = new CustomNumberEditor(Integer.class,
				true);
		PropertyEditor longEditor = new CustomNumberEditor(Long.class, true);
		PropertyEditor floatEditor = new CustomNumberEditor(Float.class, true);
		PropertyEditor doubleEditor = new CustomNumberEditor(Double.class, true);

		defaultEditors.put(Byte.TYPE, byteEditor);
		defaultEditors.put(Byte.class, byteEditor);

		defaultEditors.put(Short.TYPE, shortEditor);
		defaultEditors.put(Short.class, shortEditor);

		defaultEditors.put(Integer.TYPE, integerEditor);
		defaultEditors.put(Integer.class, integerEditor);

		defaultEditors.put(Long.TYPE, longEditor);
		defaultEditors.put(Long.class, longEditor);

		defaultEditors.put(Float.TYPE, floatEditor);
		defaultEditors.put(Float.class, floatEditor);

		defaultEditors.put(Double.TYPE, doubleEditor);
		defaultEditors.put(Double.class, doubleEditor);

		defaultEditors.put(BigDecimal.class, new CustomNumberEditor(
				BigDecimal.class, true));
		defaultEditors.put(BigInteger.class, new CustomNumberEditor(
				BigInteger.class, true));
		defaultEditors.put(Date.class, new DateEditor());
	}

	protected PropertyEditor getDefaultEditor(Class requiredType) {
		if (defaultEditors == null) {
			return null;
		}
		return (PropertyEditor) defaultEditors.get(requiredType);
	}
	
	@SuppressWarnings("static-access")
	protected void copyDefaultEditorsTo(PropertyEditorRegistry target) {
		defaultEditors = target.defaultEditors;
	}
	
	public void registerCustomEditor(Class requiredType,
			PropertyEditor propertyEditor) {
		registerCustomEditor(requiredType, null, propertyEditor);
	}

	public void registerCustomEditor(Class requiredType, String propertyPath,
			PropertyEditor propertyEditor) {
		if ((requiredType == null) && (propertyPath == null)) {
			throw new IllegalArgumentException(
					"Either requiredType or propertyPath is required");
		}
		if (this.customEditors == null) {
			this.customEditors = CollectionFactory
					.createLinkedMapIfPossible(16);
		}
		if (propertyPath != null) {
			this.customEditors.put(propertyPath, new CustomEditorHolder(
					propertyEditor, requiredType));
		} else {
			this.customEditors.put(requiredType, propertyEditor);
			this.customEditorCache = null;
		}
	}

	public PropertyEditor findCustomEditor(Class requiredType,
			String propertyPath) {
		if (this.customEditors == null) {
			return null;
		}
		if (propertyPath != null) {
			PropertyEditor editor = getCustomEditor(propertyPath, requiredType);
			if (editor == null) {
				List strippedPaths = Lists.newLinkedList();
				addStrippedPropertyPaths(strippedPaths, "", propertyPath);
				for (Iterator it = strippedPaths.iterator(); (it.hasNext())
						&& (editor == null);) {
					String strippedPath = (String) it.next();
					editor = getCustomEditor(strippedPath, requiredType);
				}
			}
			if (editor != null) {
				return editor;
			}
			if (requiredType == null) {
				requiredType = getPropertyType(propertyPath);
			}
		}
		return getCustomEditor(requiredType);
	}

	protected Class getPropertyType(String propertyPath) {
		return null;
	}

	private PropertyEditor getCustomEditor(String propertyName,
			Class requiredType) {
		CustomEditorHolder holder = (CustomEditorHolder) this.customEditors
				.get(propertyName);
		return holder != null ? holder.getPropertyEditor(requiredType) : null;
	}

	private PropertyEditor getCustomEditor(Class requiredType) {
		if (requiredType == null) {
			return null;
		}
		PropertyEditor editor = (PropertyEditor) this.customEditors
				.get(requiredType);
		if (editor == null) {
			if (this.customEditorCache != null) {
				editor = (PropertyEditor) this.customEditorCache
						.get(requiredType);
			}
			if (editor == null) {
				for (Iterator it = this.customEditors.keySet().iterator(); (it
						.hasNext()) && (editor == null);) {
					Object key = it.next();
					if (((key instanceof Class))
							&& (((Class) key).isAssignableFrom(requiredType))) {
						editor = (PropertyEditor) this.customEditors.get(key);
						if (this.customEditorCache == null) {
							this.customEditorCache = Maps.newHashMap();
						}
						this.customEditorCache.put(requiredType, editor);
					}
				}
			}
		}
		return editor;
	}

	protected Class guessPropertyTypeFromEditors(String propertyName) {
		if (this.customEditors != null) {
			CustomEditorHolder editorHolder = (CustomEditorHolder) this.customEditors
					.get(propertyName);
			if (editorHolder == null) {
				List strippedPaths = Lists.newLinkedList();
				addStrippedPropertyPaths(strippedPaths, "", propertyName);
				for (Iterator it = strippedPaths.iterator(); (it.hasNext())
						&& (editorHolder == null);) {
					String strippedName = (String) it.next();
					editorHolder = (CustomEditorHolder) this.customEditors
							.get(strippedName);
				}
			}
			if (editorHolder != null) {
				return editorHolder.getRegisteredType();
			}
		}
		return null;
	}

	protected void copyCustomEditorsTo(PropertyEditorRegistry target,
			String nestedProperty) {
		String actualPropertyName = nestedProperty != null ? PropertyAccessorUtils
				.getPropertyName(nestedProperty) : null;
		if (this.customEditors != null) {
			for (Iterator it = this.customEditors.entrySet().iterator(); it
					.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				if ((entry.getKey() instanceof Class)) {
					Class requiredType = (Class) entry.getKey();
					PropertyEditor editor = (PropertyEditor) entry.getValue();
					target.registerCustomEditor(requiredType, editor);
				} else if ((entry.getKey() instanceof String & nestedProperty != null)) {
					String editorPath = (String) entry.getKey();
					int pos = PropertyAccessorUtils
							.getFirstNestedPropertySeparatorIndex(editorPath);
					if (pos != -1) {
						String editorNestedProperty = editorPath.substring(0,
								pos);
						String editorNestedPath = editorPath.substring(pos + 1);
						if ((editorNestedProperty.equals(nestedProperty))
								|| (editorNestedProperty
										.equals(actualPropertyName))) {
							CustomEditorHolder editorHolder = (CustomEditorHolder) entry
									.getValue();
							target.registerCustomEditor(
									editorHolder.getRegisteredType(),
									editorNestedPath,
									editorHolder.getPropertyEditor());
						}
					}
				}
			}
		}
	}

	private void addStrippedPropertyPaths(List strippedPaths,
			String nestedPath, String propertyPath) {
		int startIndex = propertyPath.indexOf('[');
		if (startIndex != -1) {
			int endIndex = propertyPath.indexOf(']');
			if (endIndex != -1) {
				String prefix = propertyPath.substring(0, startIndex);
				String key = propertyPath.substring(startIndex, endIndex + 1);
				String suffix = propertyPath.substring(endIndex + 1,
						propertyPath.length());

				strippedPaths.add(nestedPath + prefix + suffix);

				addStrippedPropertyPaths(strippedPaths, nestedPath + prefix,
						suffix);

				addStrippedPropertyPaths(strippedPaths, nestedPath + prefix
						+ key, suffix);
			}
		}
	}

	private static class CustomEditorHolder {
		private final PropertyEditor propertyEditor;
		private final Class registeredType;

		private CustomEditorHolder(PropertyEditor propertyEditor,
				Class registeredType) {
			this.propertyEditor = propertyEditor;
			this.registeredType = registeredType;
		}

		private PropertyEditor getPropertyEditor() {
			return this.propertyEditor;
		}

		private Class getRegisteredType() {
			return this.registeredType;
		}

		private PropertyEditor getPropertyEditor(Class requiredType) {
			if ((this.registeredType == null)
					|| ((requiredType != null) && ((ClassUtils.isAssignable(
							this.registeredType, requiredType)) || (ClassUtils
							.isAssignable(requiredType, this.registeredType))))
					|| ((requiredType == null)
							&& (!Collection.class
									.isAssignableFrom(this.registeredType)) && (!this.registeredType
								.isArray()))) {
				return this.propertyEditor;
			}
			return null;
		}
	}
}
