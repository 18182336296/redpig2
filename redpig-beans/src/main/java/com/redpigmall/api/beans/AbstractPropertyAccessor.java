package com.redpigmall.api.beans;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.redpigmall.api.beans.exception.BeansException;
import com.redpigmall.api.beans.exception.NotWritablePropertyException;
import com.redpigmall.api.beans.exception.NullValueInNestedPathException;
import com.redpigmall.api.beans.exception.PropertyAccessException;
import com.redpigmall.api.beans.exception.PropertyAccessExceptionsException;

@SuppressWarnings({"rawtypes","unchecked"})
public abstract class AbstractPropertyAccessor extends PropertyEditorRegistry
		implements PropertyAccessor {
	public void setPropertyValue(PropertyValue pv) throws BeansException {
		setPropertyValue(pv.getName(), pv.getValue());
	}

	public void setPropertyValues(Map map) throws BeansException {
		setPropertyValues(new MutablePropertyValues(map));
	}

	public void setPropertyValues(PropertyValues pvs) throws BeansException {
		setPropertyValues(pvs, false, false);
	}

	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown)
			throws BeansException {
		setPropertyValues(pvs, ignoreUnknown, false);
	}

	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown,
			boolean ignoreInvalid) throws BeansException {
		List propertyAccessExceptions = Lists.newLinkedList();
		PropertyValue[] pvArray = pvs.getPropertyValues();
		for (int i = 0; i < pvArray.length; i++) {
			try {
				setPropertyValue(pvArray[i]);
			} catch (NotWritablePropertyException ex) {
				if (!ignoreUnknown) {
					throw ex;
				}
			} catch (NullValueInNestedPathException ex) {
				if (!ignoreInvalid) {
					throw ex;
				}
			} catch (PropertyAccessException ex) {
				propertyAccessExceptions.add(ex);
			}
		}
		if (!propertyAccessExceptions.isEmpty()) {
			Object[] paeArray = propertyAccessExceptions
					.toArray(new PropertyAccessException[propertyAccessExceptions
							.size()]);
			throw new PropertyAccessExceptionsException(
					(PropertyAccessException[]) paeArray);
		}
	}

	public Class getPropertyType(String propertyPath) {
		return null;
	}

	public abstract Object getPropertyValue(String paramString)
			throws BeansException;

	public abstract void setPropertyValue(String paramString, Object paramObject)
			throws BeansException;
}
