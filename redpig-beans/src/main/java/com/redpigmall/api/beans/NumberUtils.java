package com.redpigmall.api.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
@SuppressWarnings({"rawtypes"})
public abstract class NumberUtils {
	public static Number convertNumberToTargetClass(Number number,
			Class targetClass) throws IllegalArgumentException {
		Assert.notNull(number, "Number must not be null");
		Assert.notNull(targetClass, "Target class must not be null");
		if (targetClass.isInstance(number)) {
			return number;
		}
		if (targetClass.equals(Byte.class)) {
			long value = number.longValue();
			if ((value < -128L) || (value > 127L)) {
				raiseOverflowException(number, targetClass);
			}
			return new Byte(number.byteValue());
		}
		if (targetClass.equals(Short.class)) {
			long value = number.longValue();
			if ((value < -32768L) || (value > 32767L)) {
				raiseOverflowException(number, targetClass);
			}
			return new Short(number.shortValue());
		}
		if (targetClass.equals(Integer.class)) {
			long value = number.longValue();
			if ((value < -2147483648L) || (value > 2147483647L)) {
				raiseOverflowException(number, targetClass);
			}
			return new Integer(number.intValue());
		}
		if (targetClass.equals(Long.class)) {
			return new Long(number.longValue());
		}
		if (targetClass.equals(Float.class)) {
			return new Float(number.floatValue());
		}
		if (targetClass.equals(Double.class)) {
			return new Double(number.doubleValue());
		}
		if (targetClass.equals(BigInteger.class)) {
			return BigInteger.valueOf(number.longValue());
		}
		if (targetClass.equals(BigDecimal.class)) {
			return new BigDecimal(number.toString());
		}
		throw new IllegalArgumentException("Could not convert number ["
				+ number + "] of type [" + number.getClass().getName()
				+ "] to unknown target class [" + targetClass.getName() + "]");
	}

	private static void raiseOverflowException(Number number, Class targetClass) {
		throw new IllegalArgumentException("Could not convert number ["
				+ number + "] of type [" + number.getClass().getName()
				+ "] to target class [" + targetClass.getName() + "]: overflow");
	}

	public static Number parseNumber(String text, Class targetClass) {
		Assert.notNull(text, "Text must not be null");
		Assert.notNull(targetClass, "Target class must not be null");

		String trimmed = text.trim();
		if (targetClass.equals(Byte.class)) {
			return Byte.decode(trimmed);
		}
		if (targetClass.equals(Short.class)) {
			return Short.decode(trimmed);
		}
		if (targetClass.equals(Integer.class)) {
			return Integer.decode(trimmed);
		}
		if (targetClass.equals(Long.class)) {
			return Long.decode(trimmed);
		}
		if (targetClass.equals(BigInteger.class)) {
			return decodeBigInteger(trimmed);
		}
		if (targetClass.equals(Float.class)) {
			return Float.valueOf(trimmed);
		}
		if (targetClass.equals(Double.class)) {
			return Double.valueOf(trimmed);
		}
		if ((targetClass.equals(BigDecimal.class))
				|| (targetClass.equals(Number.class))) {
			return new BigDecimal(trimmed);
		}
		throw new IllegalArgumentException("Cannot convert String [" + text
				+ "] to target class [" + targetClass.getName() + "]");
	}

	public static Number parseNumber(String text, Class targetClass,
			NumberFormat numberFormat) {
		if (numberFormat != null) {
			Assert.notNull(text, "Text must not be null");
			Assert.notNull(targetClass, "Target class must not be null");
			try {
				Number number = numberFormat.parse(text.trim());
				return convertNumberToTargetClass(number, targetClass);
			} catch (ParseException ex) {
				throw new IllegalArgumentException(ex.getMessage());
			}
		}
		return parseNumber(text, targetClass);
	}

	private static BigInteger decodeBigInteger(String value) {
		int radix = 10;
		int index = 0;
		boolean negative = false;
		if (value.startsWith("-")) {
			negative = true;
			index++;
		}
		if ((value.startsWith("0x", index)) || (value.startsWith("0X", index))) {
			index += 2;
			radix = 16;
		} else if (value.startsWith("#", index)) {
			index++;
			radix = 16;
		} else if ((value.startsWith("0", index))
				&& (value.length() > 1 + index)) {
			index++;
			radix = 8;
		}
		BigInteger result = new BigInteger(value.substring(index), radix);
		return negative ? result.negate() : result;
	}
}
