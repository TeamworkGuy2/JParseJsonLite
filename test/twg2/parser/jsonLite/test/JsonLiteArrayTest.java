package twg2.parser.jsonLite.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import twg2.parser.jsonLite.JsonLiteArray;
import twg2.parser.textParser.TextParserImpl;
import checks.CheckTask;

/**
 * @author TeamworkGuy2
 * @since 2015-2-1
 */
public class JsonLiteArrayTest {


	public String[] arrayParseInputs(boolean isArraySyntax) {
		String s = (isArraySyntax ? "[" : "");
		String e = (isArraySyntax ? "]" : "\n");

		String[] input = {
				"0. " + s + "stuff, other stuff, \"string containing \\\"quotes\\\" inside\"" + e,
				"1. " + s + "element" + e,
				"2. \t " + s + "1 2" + e + " ",
				"3. " + s + "\"quoted element\"" + e,
				"4. " + s + "" + e,
				"5. " + s + "abc.xyz, \"some-thing\", value, arguments, things-stuff: \"inside, quote's\"" + e,
				"6. " + s + " 123, alpha \"[str], \\\"5.43\\\" vin\", wubwub" + e
		};
		return input;
	}


	public List<String>[] arrayParseExpects() {
		@SuppressWarnings("unchecked")
		List<String>[] expect = new List[] {
				Arrays.asList("stuff", "other stuff", "string containing \"quotes\" inside"),
				Arrays.asList("element"),
				Arrays.asList("1 2"),
				Arrays.asList("quoted element"),
				Arrays.asList(),
				Arrays.asList("abc.xyz", "some-thing", "value", "arguments", "things-stuff: \"inside, quote's\""),
				Arrays.asList("123", "alpha \"[str], \"5.43\" vin\"", "wubwub")
		};
		return expect;
	}


	@Test
	public void arrayParseTest() {
		int offset = 3;
		String[] input = arrayParseInputs(true);
		List<String>[] expect = arrayParseExpects();

		Assert.assertEquals("inputs and expected result arrays must be the same length", input.length, expect.length);

		CheckTask.assertTests(input, expect, (str) -> {
			List<String> res = new ArrayList<>();
			JsonLiteArray.parseArray(TextParserImpl.of(str, offset, str.length() - offset), true, res);
			return res;
		});

		CheckTask.assertTests(input, expect, (str) -> {
			List<String> res = new ArrayList<>();
			TextParserImpl lineBuf = TextParserImpl.of(str);
			for(int i = 0; i < offset; i++) { lineBuf.nextChar(); }

			JsonLiteArray.parseArray(lineBuf, true, res);
			return res;
		});
	}


	@Test
	public void arrayLineParseTest() {
		int offset = 3;
		String[] input = arrayParseInputs(false);
		List<String>[] expect = arrayParseExpects();

		Assert.assertEquals("inputs and expected result arrays must be the same length", input.length, expect.length);

		CheckTask.assertTests(input, expect, null, (str) -> {
			List<String> res = new ArrayList<>();
			TextParserImpl lineBuf = TextParserImpl.of(str);
			for(int i = 0; i < offset; i++) { lineBuf.nextChar(); }

			JsonLiteArray.parseArrayLine(lineBuf, true, res);
			return res;
		});
	}


	@Test
	public void readJsonLiteArrayTest() {
		String[] strs = new String[] {
				"[]",
				"[a]",
				"[ JsonLite ]",
				"[ 1, 2, 3 ]",
				"[ abc \"with, z\", \"(2, 2)\", end]",
				"[ abc \"with, z\", \"(2, 2)\", [\"a,b\", end, 1 \"2, and 3\"] ]"
		};
		Object[][] expect = {
				{ },
				{ "a" },
				{ "JsonLite " },
				{ "1", "2", "3 " },
				{ "abc \"with, z\"", "(2, 2)", "end" },
				{ "abc \"with, z\"", "(2, 2)", Arrays.asList("a,b", "end", "1 \"2, and 3\"") },
		};

		Assert.assertEquals("array lengths", strs.length, expect.length);

		for(int i = 0, size = strs.length; i < size; i++) {
			List<Object> elems = new ArrayList<>();
			JsonLiteArray.parseArrayDeep(TextParserImpl.of(strs[i]), true, elems);
			CheckTask.assertTests(expect[i], elems.toArray(new Object[0]), (aryStrs) -> aryStrs);

			//System.out.println("parsing JsonLite array '" + strs[i] + "': " + elems.toString());
		}
	}

}
