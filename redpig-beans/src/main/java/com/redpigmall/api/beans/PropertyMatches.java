package com.redpigmall.api.beans;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
@SuppressWarnings({"rawtypes","unchecked"})
final class PropertyMatches {
	public static final int DEFAULT_MAX_DISTANCE = 2;
	private final String propertyName;
	private String[] possibleMatches;

	public static PropertyMatches forProperty(String propertyName,
			Class beanClass) {
		return forProperty(propertyName, beanClass, 2);
	}

	public static PropertyMatches forProperty(String propertyName,
			Class beanClass, int maxDistance) {
		return new PropertyMatches(propertyName, beanClass, maxDistance);
	}

	private PropertyMatches(String propertyName, Class beanClass,
			int maxDistance) {
		this.propertyName = propertyName;
		this.possibleMatches = calculateMatches(
				BeanUtils.getPropertyDescriptors(beanClass), maxDistance);
	}

	public String[] getPossibleMatches() {
		return this.possibleMatches;
	}

	public String buildErrorMessage() {
		StringBuffer buf = new StringBuffer();
		buf.append("Bean property '");
		buf.append(this.propertyName);
		buf.append("' is not writable or has an invalid setter method. ");
		if (ObjectUtils.isEmpty(this.possibleMatches)) {
			buf.append("Does the parameter type of the setter match the return type of the getter?");
		} else {
			buf.append("Did you mean ");
			for (int i = 0; i < this.possibleMatches.length; i++) {
				buf.append('\'');
				buf.append(this.possibleMatches[i]);
				if (i < this.possibleMatches.length - 2) {
					buf.append("', ");
				} else if (i == this.possibleMatches.length - 2) {
					buf.append("', or ");
				}
			}
			buf.append("'?");
		}
		return buf.toString();
	}

	private String[] calculateMatches(PropertyDescriptor[] propertyDescriptors,
			int maxDistance) {
		List candidates = Lists.newArrayList();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			if (propertyDescriptors[i].getWriteMethod() != null) {
				String possibleAlternative = propertyDescriptors[i].getName();
				if (calculateStringDistance(this.propertyName,
						possibleAlternative) <= maxDistance) {
					candidates.add(possibleAlternative);
				}
			}
		}
		Collections.sort(candidates);
		return StringUtils.toStringArray(candidates);
	}

	private int calculateStringDistance(String s1, String s2) {
		if (s1.length() == 0) {
			return s2.length();
		}
		if (s2.length() == 0) {
			return s1.length();
		}
		int[][] d = new int[s1.length() + 1][s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			d[i][0] = i;
		}
		for (int j = 0; j <= s2.length(); j++) {
			d[0][j] = j;
		}
		for (int i = 1; i <= s1.length(); i++) {
			char s_i = s1.charAt(i - 1);
			for (int j = 1; j <= s2.length(); j++) {
				char t_j = s2.charAt(j - 1);
				int cost;
				if (s_i == t_j) {
					cost = 0;
				} else {
					cost = 1;
				}
				d[i][j] = Math.min(
						Math.min(d[(i - 1)][j] + 1, d[i][(j - 1)] + 1),
						d[(i - 1)][(j - 1)] + cost);
			}
		}
		return d[s1.length()][s2.length()];
	}
}
