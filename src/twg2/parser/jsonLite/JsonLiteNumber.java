package twg2.parser.jsonLite;

import twg2.parser.jsonLite.JsonLite.NumberType;
import twg2.parser.textParser.TextParser;

/**
 * @author TeamworkGuy2
 * @since 2014-9-2
 */
public class JsonLiteNumber {
	private NumberType numberType;
	// TODO all int and float values can be represented by longs and doubles, remove the unnecessary fields
	private int intVal;
	private long longVal;
	private float floatVal;
	private double doubleVal;


	public JsonLiteNumber() {
	}


	public JsonLiteNumber(int intVal) {
		this.setInt(intVal);
	}


	public JsonLiteNumber(long longVal) {
		this.setLong(longVal);
	}


	public JsonLiteNumber(float floatVal) {
		this.setFloat(floatVal);
	}


	public JsonLiteNumber(double doubleVal) {
		this.setDouble(doubleVal);
	}


	public void setInt(int intVal) {
		this.numberType = NumberType.INTEGER;
		this.intVal = intVal;
		this.longVal = intVal;
		this.floatVal = intVal;
		this.doubleVal = intVal;
	}


	public void setLong(long longVal) {
		this.numberType = NumberType.LONG;
		this.longVal = longVal;
		this.doubleVal = longVal;
	}


	public void setFloat(float floatVal) {
		this.numberType = NumberType.FLOAT;
		this.floatVal = floatVal;
		this.doubleVal = floatVal;
	}


	public void setDouble(double doubleVal) {
		this.numberType = NumberType.DOUBLE;
		this.doubleVal = doubleVal;
	}


	public NumberType getNumberType() {
		return numberType;
	}


	public int asInt() {
		if(!NumberType.isIntLike(numberType)) {
			throw new IllegalStateException("cannot get int value from " + (numberType != null ? numberType.getPrimitiveTypeName() : "number") + " without precision loss");
		}
		if(numberType == NumberType.INTEGER) {
			return intVal;
		}
		else {
			throw new IllegalStateException(numberType.getPrimitiveTypeName() + " cannot be cast to double");
		}
	}


	public long asLong() {
		if(!NumberType.isLongLike(numberType)) {
			throw new IllegalStateException("cannot get long value from " + (numberType != null ? numberType.getPrimitiveTypeName() : "null") + " without precision loss");
		}
		switch(numberType) {
		case INTEGER:
			return intVal;
		case LONG:
			return longVal;
		default:
			throw new IllegalStateException(numberType.getPrimitiveTypeName() + " cannot be cast to double");
		}
	}


	public float asFloat() {
		if(!NumberType.isFloatLike(numberType)) {
			throw new IllegalStateException("cannot get float value from " + (numberType != null ? numberType.getPrimitiveTypeName() : "null") + " without precision loss");
		}
		switch(numberType) {
		case INTEGER:
			return intVal;
		case FLOAT:
			return floatVal;
		default:
			throw new IllegalStateException(numberType.getPrimitiveTypeName() + " cannot be cast to float");
		}
	}


	public double asDouble() {
		if(!NumberType.isDoubleLike(numberType)) {
			throw new IllegalStateException("cannot get double value from " + (numberType != null ? numberType.getPrimitiveTypeName() : "null") + " without precision loss");
		}
		switch(numberType) {
		case INTEGER:
			return intVal;
		case LONG:
			return longVal;
		case FLOAT:
			return floatVal;
		case DOUBLE:
			return doubleVal;
		default:
			throw new IllegalStateException(numberType.getPrimitiveTypeName() + " cannot be cast to double");
		}
	}


	@Override
	public String toString() {
		return (numberType != null ? (NumberType.isLongLike(numberType) ? (numberType.getPrimitiveTypeName() + " " + asLong()) : (numberType.getPrimitiveTypeName() + " " + asDouble())) : "null numeric");
	}


	public static final JsonLiteNumber readNumber(TextParser in) {
		JsonLiteNumber number = new JsonLiteNumber();
		JsonLiteNumber.readNumberCustom(in, true, false, false, true, true, number);
		return number;
	}


	/** 
	 * @param in
	 * @param allowSign true to allow '-' or '+', false to disallow
	 * @param readIntOnly true to only read an integer, no decimal ({@code allowSign} and {@code allowExponent} apply), false to read decimal
	 * @param readDecimalOnly true to only read a decimal ({@code allowSign}, {@code readDecimalPoint}, and {@code allowExponent} apply), false to read full decimal with integer portion
	 * @param readDecimalPoint true to require a decimal point '.' (only applies if {@code readDecimalOnly} is true), false to read a decimal without a leading decimal point
	 * @param allowExponent true to attempt to read any exponent at the end of the value, false to disallow
	 * @param dst set the value/numeric-type of this object based on the parsed numeric value
	 */
	public static final void readNumberCustom(TextParser in, boolean allowSign, boolean readIntOnly, boolean readDecimalOnly, boolean readDecimalPoint, boolean allowExponent, JsonLiteNumber dst) {
		boolean containsDecimalPoint = false;
		boolean containsExponent = false;
		String integerPart = null;
		String fractionalPart = null;
		String exponent = null;
		StringBuilder strB = new StringBuilder();

		// sign
		if(allowSign && in.nextIf('-')) {
			if(!in.hasNext()) {
				throw new NumberFormatException();
			}
			strB.append('-');
		}

		// integer part
		if(!readDecimalOnly) {
			if(in.nextIf('0')) {
				strB.append('0');
			}
			else if(in.nextBetween('1', '9', 1, strB) == 1) {
				in.nextBetween('0', '9', 0, strB);
			}
			else {
				throw new NumberFormatException();
			}

			integerPart = strB.toString();
		}

		// fractional part
		if((!readIntOnly && in.nextIf('.')) || (readDecimalOnly && (!readDecimalPoint || in.nextIf('.')))) {
			if(readDecimalOnly) {
				integerPart = "0";
			}
			containsDecimalPoint = true;
			strB.setLength(0);

			in.nextBetween('0', '9', 0, strB);

			fractionalPart = strB.toString();
		}

		// exponent
		if(allowExponent && in.nextIf('e', 'E', 1, strB) == 1) {
			containsExponent = true;
			strB.setLength(0);

			in.nextIf('-', '+', 1, strB);
			in.nextBetween('0', '9', 0, strB);

			exponent = strB.toString();
		}

		if(containsDecimalPoint || containsExponent) {
			strB.setLength(0);
			if(containsDecimalPoint && containsExponent) {
				strB.append(integerPart).append('.').append(fractionalPart).append('e').append(exponent);
				toSmallestType(Double.parseDouble(strB.toString()), dst);
				return;
			}
			else if(containsDecimalPoint) {
				strB.append(integerPart).append('.').append(fractionalPart);
				toSmallestType(Double.parseDouble(strB.toString()), dst);
				return;
			}
			else if(containsExponent) {
				strB.append(integerPart).append('e').append(exponent);
				toSmallestType(Double.parseDouble(strB.toString()), dst);
				return;
			}
			else {
				throw new AssertionError(containsDecimalPoint + " or " + containsExponent + " must have been true to reach this point");
			}
		}
		else {
			if("-0".equals(integerPart)) {
				toSmallestType(Double.parseDouble(integerPart), dst);
				return;
			}
			toSmallestType(Long.parseLong(integerPart), dst);
		}
	}


	public static final void toSmallestType(double value, JsonLiteNumber dst) {
		long longVal = (long)value;

		if(value == longVal) {
			toSmallestType(longVal, dst);
			return;
		}

		float floatVal = (float)value;
		if(value - floatVal < Float.MIN_VALUE) {
			dst.setFloat(floatVal);
			return;
		}
		dst.setDouble(value);
	}


	public static final void toSmallestType(long value, JsonLiteNumber dst) {
		if(value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
			dst.setInt((int)value);
			return;
		}
		dst.setLong(value);
	}

}
