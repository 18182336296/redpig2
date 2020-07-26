package com.redpigmall.api.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import com.google.common.collect.Sets;
import com.redpigmall.api.beans.CollectionFactory;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CustomCollectionEditor extends PropertyEditorSupport {
	private final Class collectionType;
	private final boolean nullAsEmptyCollection;

	public CustomCollectionEditor(Class collectionType) {
		this(collectionType, false);
	}

	public CustomCollectionEditor(Class collectionType,
			boolean nullAsEmptyCollection) {
		if (collectionType == null) {
			throw new IllegalArgumentException("Collection type is required");
		}
		if (!Collection.class.isAssignableFrom(collectionType)) {
			throw new IllegalArgumentException("Collection type ["
					+ collectionType.getName()
					+ "] does not implement [java.util.Collection]");
		}
		this.collectionType = collectionType;
		this.nullAsEmptyCollection = nullAsEmptyCollection;
	}

	public void setAsText(String text) throws IllegalArgumentException {
		setValue(text);
	}

	public void setValue(Object value) {
		if ((value == null) && (this.nullAsEmptyCollection)) {
			super.setValue(createCollection(this.collectionType, 0));
		} else if ((value == null)
				|| ((this.collectionType.isInstance(value)) && (!alwaysCreateNewCollection()))) {
			super.setValue(value);
		} else if ((value instanceof Collection)) {
			Collection source = (Collection) value;
			Collection target = createCollection(this.collectionType,
					source.size());
			for (Iterator it = source.iterator(); it.hasNext();) {
				target.add(convertElement(it.next()));
			}
			super.setValue(target);
		} else if (value.getClass().isArray()) {
			int length = Array.getLength(value);
			Collection target = createCollection(this.collectionType, length);
			for (int i = 0; i < length; i++) {
				target.add(convertElement(Array.get(value, i)));
			}
			super.setValue(target);
		} else {
			Collection target = createCollection(this.collectionType, 1);
			target.add(convertElement(value));
			super.setValue(target);
		}
	}

	protected Collection createCollection(Class collectionType,
			int initialCapacity) {
		if (!collectionType.isInterface()) {
			try {
				return (Collection) collectionType.newInstance();
			} catch (Exception ex) {
				throw new IllegalArgumentException(
						"Could not instantiate collection class ["
								+ collectionType.getName() + "]: "
								+ ex.getMessage());
			}
		}
		if (List.class.equals(collectionType)) {
			return new ArrayList(initialCapacity);
		}
		if (SortedSet.class.equals(collectionType)) {
			return Sets.newTreeSet();
		}
		return CollectionFactory.createLinkedSetIfPossible(initialCapacity);
	}

	protected boolean alwaysCreateNewCollection() {
		return false;
	}

	protected Object convertElement(Object element) {
		return element;
	}

	public String getAsText() {
		return null;
	}
}
