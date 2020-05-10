package twg2.parser.jsonLite.test;

import org.junit.Assert;
import org.junit.Test;

import twg2.junitassist.checks.CheckTask;
import twg2.parser.jsonLite.JsonLite.NumberType;
import twg2.parser.jsonLite.JsonLiteNumber;

/**
 * @author TeamworkGuy2
 * @since 2020-05-09
 */
public class JsonLiteNumberTest {

	@Test
	public void constructorTest() {
		JsonLiteNumber num;
		num = new JsonLiteNumber();
		Assert.assertNull(num.getNumberType());

		num = new JsonLiteNumber((int)42);
		Assert.assertEquals(42, num.asInt());
		Assert.assertEquals(NumberType.INTEGER, num.getNumberType());

		num = new JsonLiteNumber(12.12f);
		Assert.assertEquals(12.12f, num.asFloat(), 0);
		Assert.assertEquals(NumberType.FLOAT, num.getNumberType());

		num = new JsonLiteNumber((long)343);
		Assert.assertEquals(343, num.asLong());
		Assert.assertEquals(NumberType.LONG, num.getNumberType());

		num = new JsonLiteNumber(1204.543d);
		Assert.assertEquals(1204.543d, num.asDouble(), 0);
		Assert.assertEquals(NumberType.DOUBLE, num.getNumberType());
	}


	@Test
	public void setTest() {
		JsonLiteNumber num = new JsonLiteNumber();

		num.setInt((int)42);
		Assert.assertEquals(42, num.asInt());
		Assert.assertEquals(NumberType.INTEGER, num.getNumberType());

		num.setFloat(12.12f);
		Assert.assertEquals(12.12f, num.asFloat(), 0);
		Assert.assertEquals(NumberType.FLOAT, num.getNumberType());
		CheckTask.assertException(() -> num.asInt());

		num.setLong((long)343);
		Assert.assertEquals(343, num.asLong());
		Assert.assertEquals(NumberType.LONG, num.getNumberType());

		num.setDouble(1204.543d);
		Assert.assertEquals(1204.543d, num.asDouble(), 0);
		Assert.assertEquals(NumberType.DOUBLE, num.getNumberType());
		CheckTask.assertException(() -> num.asLong());
	}

}
