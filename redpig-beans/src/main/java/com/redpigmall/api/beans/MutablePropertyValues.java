package com.redpigmall.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
@SuppressWarnings({"rawtypes","unchecked", "serial"})
public class MutablePropertyValues implements PropertyValues, Serializable {
	private final List propertyValueList;

	public MutablePropertyValues() {
		this.propertyValueList = Lists.newArrayList();
	}

	public MutablePropertyValues(PropertyValues source) {
		if (source != null) {
			PropertyValue[] pvs = source.getPropertyValues();
			this.propertyValueList = new ArrayList(pvs.length);
			for (int i = 0; i < pvs.length; i++) {
				PropertyValue newPv = new PropertyValue(pvs[i]);
				this.propertyValueList.add(newPv);
			}
		} else {
			this.propertyValueList = new ArrayList(0);
		}
	}

	public MutablePropertyValues(Map source) {
		if (source != null) {
			this.propertyValueList = new ArrayList(source.size());
			Iterator it = source.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				PropertyValue newPv = new PropertyValue(
						(String) entry.getKey(), entry.getValue());
				this.propertyValueList.add(newPv);
			}
		} else {
			this.propertyValueList = new ArrayList(0);
		}
	}

	public MutablePropertyValues addPropertyValues(PropertyValues source) {
		if (source != null) {
			PropertyValue[] pvs = source.getPropertyValues();
			for (int i = 0; i < pvs.length; i++) {
				PropertyValue newPv = new PropertyValue(pvs[i].getName(),
						pvs[i].getValue());
				addPropertyValue(newPv);
			}
		}
		return this;
	}

	public MutablePropertyValues addPropertyValues(Map source) {
		if (source != null) {
			Iterator it = source.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				PropertyValue newPv = new PropertyValue(
						(String) entry.getKey(), entry.getValue());
				addPropertyValue(newPv);
			}
		}
		return this;
	}

	public MutablePropertyValues addPropertyValue(PropertyValue pv) {
		for (int i = 0; i < this.propertyValueList.size(); i++) {
			PropertyValue currentPv = (PropertyValue) this.propertyValueList
					.get(i);
			if (currentPv.getName().equals(pv.getName())) {
				setPropertyValueAt(pv, i);
				return this;
			}
		}
		this.propertyValueList.add(pv);
		return this;
	}

	public void addPropertyValue(String propertyName, Object propertyValue) {
		addPropertyValue(new PropertyValue(propertyName, propertyValue));
	}

	public void setPropertyValueAt(PropertyValue pv, int i) {
		this.propertyValueList.set(i, pv);
	}

	public void removePropertyValue(String propertyName) {
		removePropertyValue(getPropertyValue(propertyName));
	}

	public void removePropertyValue(PropertyValue pv) {
		this.propertyValueList.remove(pv);
	}

	public void clear() {
		this.propertyValueList.clear();
	}

	public PropertyValue[] getPropertyValues() {
		return (PropertyValue[]) this.propertyValueList
				.toArray(new PropertyValue[this.propertyValueList.size()]);
	}

	public PropertyValue getPropertyValue(String propertyName) {
		for (int i = 0; i < this.propertyValueList.size(); i++) {
			PropertyValue pv = (PropertyValue) this.propertyValueList.get(i);
			if (pv.getName().equals(propertyName)) {
				return pv;
			}
		}
		return null;
	}

	public boolean contains(String propertyName) {
		return getPropertyValue(propertyName) != null;
	}

	public boolean isEmpty() {
		return this.propertyValueList.isEmpty();
	}

	public PropertyValues changesSince(PropertyValues old) {
		MutablePropertyValues changes = new MutablePropertyValues();
		if (old == this) {
			return changes;
		}
		for (Iterator it = this.propertyValueList.iterator(); it.hasNext();) {
			PropertyValue newPv = (PropertyValue) it.next();

			PropertyValue pvOld = old.getPropertyValue(newPv.getName());
			if (pvOld == null) {
				changes.addPropertyValue(newPv);
			} else if (!pvOld.equals(newPv)) {
				changes.addPropertyValue(newPv);
			}
		}
		return changes;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MutablePropertyValues)) {
			return false;
		}
		MutablePropertyValues that = (MutablePropertyValues) other;
		return this.propertyValueList.equals(that.propertyValueList);
	}

	public int hashCode() {
		return this.propertyValueList.hashCode();
	}

	public String toString() {
		PropertyValue[] pvs = getPropertyValues();
		StringBuffer sb = new StringBuffer("PropertyValues: length="
				+ pvs.length + "; ");
		sb.append(StringUtils.arrayToDelimitedString(pvs, "; "));
		return sb.toString();
	}
}
