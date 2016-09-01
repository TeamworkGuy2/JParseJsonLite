package twg2.parser.jsonLite.test;

import org.junit.Assert;
import org.junit.Test;

import twg2.parser.jsonLite.JsonLiteNumber;
import twg2.parser.jsonLite.JsonLite.NumberType;
import twg2.parser.textParser.TextParserImpl;

/**
 * @author TeamworkGuy2
 * @since 2015-1-1
 */
public class JsonLiteTest {

	@Test
	public void readJsonLiteNumberTest() {
		String[] strs = new String[] {
				"-0", "123", "0.3", "5.6e2", "1e3",
				"9876543210", "2.000002e6", "2.000002e007", "3.1415926", "3.1415926535897932384626433832795e24",
				"12345.67890", "98765.43210"
		};
		Object[] expected = new Object[] {
				Integer.valueOf(-0), Integer.valueOf(123), Float.valueOf((float)0.3), Integer.valueOf((int)5.6e2), Integer.valueOf((int)1e3),
				Long.valueOf(9876543210L), Integer.valueOf((int)2.000002e6), Integer.valueOf((int)2.000002e007), Double.valueOf(3.1415926), Double.valueOf(3.1415926535897932384626433832795e24),
				Double.valueOf(12345.67890), Double.valueOf(98765.43210),
		};

		Assert.assertEquals("array lengths", strs.length, expected.length);

		for(int i = 0, size = strs.length; i < size; i++) {
			String s = strs[i];
			Object expect = expected[i];
			JsonLiteNumber num = JsonLiteNumber.readNumber(TextParserImpl.of(s));
			if(num.getNumberType() == NumberType.DOUBLE) {
				Assert.assertEquals(num.asDouble(), expect);
			}
			else if(num.getNumberType() == NumberType.LONG) {
				Assert.assertEquals(num.asLong(), expect);
			}
			else if(num.getNumberType() == NumberType.FLOAT) {
				Assert.assertEquals(num.asFloat(), expect);
			}
			else if(num.getNumberType() == NumberType.INTEGER) {
				Assert.assertEquals(num.asInt(), expect);
			}
			else {
				Assert.assertTrue("unexpected JSON number type '" + num.getNumberType() + "' at " + i, false);
			}

			//System.out.println("parsing JsonLite number '" + s + "' (expected: " + expect + "): " + num.toString());
		}
	}

}
