package twg2.parser.jsonLite;


/**
 * @author TeamworkGuy2
 * @since 2014-9-2
 */
public class JsonLite {
	/**
	 * @author TeamworkGuy2
	 * @since 2014-9-2
	 */
	public enum AllTypes {
		OBJECT,
		ARRAY,
		STRING,
		NUMBER,
		TRUE,
		FALSE,
		NULL;
	}


	/**
	 * @author TeamworkGuy2
	 * @since 2014-9-2
	 */
	public enum NumberType {
		INTEGER("int"),
		LONG("long"),
		FLOAT("float"),
		DOUBLE("double");

		final String primitiveTypeName;


		NumberType(String primitiveTypeName) {
			this.primitiveTypeName = primitiveTypeName;
		}


		public static final boolean isInt(JsonLite.NumberType numType) {
			return numType != null && numType == NumberType.INTEGER;
		}


		public static final boolean isLong(JsonLite.NumberType numType) {
			return numType != null && numType == NumberType.LONG;
		}


		public static final boolean isFloat(JsonLite.NumberType numType) {
			return numType != null && numType == NumberType.FLOAT;
		}


		public static final boolean isDouble(JsonLite.NumberType numType) {
			return numType != null && numType == NumberType.DOUBLE;
		}


		public static final boolean isIntLike(JsonLite.NumberType numType) {
			return numType != null && (numType == NumberType.INTEGER);
		}


		public static final boolean isLongLike(JsonLite.NumberType numType) {
			return numType != null && (numType == NumberType.LONG || numType == NumberType.INTEGER);
		}


		public static final boolean isFloatLike(JsonLite.NumberType numType) {
			return numType != null && (numType == NumberType.FLOAT || numType == NumberType.INTEGER);
		}


		public static final boolean isDoubleLike(JsonLite.NumberType numType) {
			return numType != null && (numType == NumberType.DOUBLE || numType == NumberType.FLOAT || numType == NumberType.INTEGER || numType == NumberType.LONG);
		}


		public String getPrimitiveTypeName() {
			return primitiveTypeName;
		}

	}

}
