package twg2.parser.jsonLite;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import twg2.parser.textParser.TextIteratorParser;
import twg2.parser.textParser.TextParser;
import twg2.parser.textParserUtils.ReadIsMatching;
import twg2.parser.textParserUtils.ReadMatching;
import twg2.parser.textParserUtils.ReadWhitespace;
import twg2.text.stringEscape.StringEscape;
import twg2.text.stringUtils.StringCheck;

/**
 * @author TeamworkGuy2
 * @since 2014-9-2
 */
public class JsonLiteArray {
	private static final char ARRAY_START = '[';
	private static final char ARRAY_END = ']';
	//private static final char[] notStringArrayLike = new char[] {' '/*space*/, '	'/*tab*/, ',', ARRAY_END};


	/** Convert an array of strings to a JSON styled array of strings (i.e. {@code ["a", "b", "c"]})
	 */
	public static final String stringifyArray(List<String> ary) throws IOException {
		StringBuilder strB = new StringBuilder(ary.size() > 10 ? ary.size() > 20 ? 128 : 64 : 32);
		strB.append(ARRAY_START);
		if(ary.size() > 0) {
			for(int i = 0, size = ary.size() - 1; i < size; i++) {
				strB.append('"');
				// replace \ and " with \\ and \"
				StringEscape.escape(ary.get(i), '\\', '\\', '"', strB);
				strB.append("\", ");
			}
			strB.append('"');
			StringEscape.escape(ary.get(ary.size()-1), '\\', '\\', '"', strB);
			strB.append('"');
		}
		strB.append(ARRAY_END);

		return strB.toString();
	}


	/**
	 * @see #parseArray(String, List)
	 */
	public static final List<String> parseArray(String arrayString) {
		List<String> ary = new ArrayList<>();
		parseArray(arrayString, ary);
		return ary;
	}


	/**
	 * @see JsonLiteArray#parseArray(TextParser, boolean, List)
	 */
	public static final void parseArray(String arrayString, List<String> dst) {
		parseArray(TextIteratorParser.of(arrayString), true, dst);
	}


	/** Parses a JSON style array where the strings do not need to be quoted.
	 * Supports the following formats:<br>
	 * {@code [multiple words, 123, false]} parses to {@code ["multiple words", "123", "false"]}<br>
	 * {@code [string with "comma, separator", last]} parses to {@code ["string with "comma, separator"", "last"]}<br>
	 * {@code ["bilbo baggins", middle earth]} parses to {@code ["bilbo baggins", "middle earth"]}
	 * 
	 * @param in the {@link TextParser} to read from
	 * @param readLeadingArrayWhitespace true to read leading whitespace before the array, false to not
	 * @param dst the destination list to store the parsed array elements in
	 */
	public static final void parseArray(TextParser in, boolean readLeadingArrayWhitespace, List<String> dst) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<Object> dstList = (List)dst;
		parseArrayLike(in, readLeadingArrayWhitespace, true, ',', false, StringCheck.SIMPLE_WHITESPACE, true, ARRAY_START, true, ARRAY_END, false, dstList);
	}


	/** Read a JSON array with leading element whitespace, required '[' ']' start and end chars,
	 * and {@link StringCheck#SIMPLE_WHITESPACE} whitespace characters 
	 * @see #parseArrayLike
	 */
	public static final void parseArrayDeep(TextParser in, boolean readLeadingArrayWhitespace, List<Object> dst) {
		parseArrayLike(in, readLeadingArrayWhitespace, true, ',', false, StringCheck.SIMPLE_WHITESPACE, true, ARRAY_START, true, ARRAY_END, true, dst);
	}


	/** Read a JSON style array with no start mark, with leading array and element whitespace,
	 * no newlines inside elements, and {@link StringCheck#SIMPLE_WHITESPACE} whitespace characters
	 * @see #parseArrayLike
	 */
	public static final void parseArrayLine(TextParser in, boolean readLeadingArrayWhitespace, List<String> dst) {
		parseArrayLikeLine(in, readLeadingArrayWhitespace, true, false, StringCheck.SIMPLE_WHITESPACE, dst);
	}


	/** Read a JSON array with leading element whitespace, required '[' ']' start and end chars,
	 * and {@link StringCheck#SIMPLE_WHITESPACE} whitespace characters 
	 * @see #parseArrayLike
	 */
	public static final void parseArrayLineDeep(TextParser in, boolean readLeadingArrayWhitespace, boolean readLeadingElementWhitespace, List<Object> dst) {
		parseArrayLike(in, readLeadingArrayWhitespace, readLeadingElementWhitespace, ',', false, StringCheck.SIMPLE_WHITESPACE, false, ARRAY_START, false, ARRAY_END, true, dst);
	}


	/** Read a JSON style array with no start mark that ends at the end of the current line
	 * @see #parseArrayLike
	 */
	public static final void parseArrayLikeLine(TextParser in, boolean readLeadingArrayWhitespace, boolean readLeadingElementWhitespace, boolean allowNewlineInElement,
			char[] whitespace, List<String> dst) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<Object> dstList = (List)dst;
		parseArrayLike(in, readLeadingArrayWhitespace, readLeadingElementWhitespace, ',', false, whitespace, false, '\0', false, '\n', false, dstList);
	}


	/** Read JSON like array with options to read a single line, customize the element separator char, allow newlines, etc.<br>
	 * For example, to read a JSON style array with leading whitespace, call:<br>
	 * <pre>parseArrayLike(in, true, true, ',', false, new char[] { ' ', '\t', '\n' }, true, '[', ']', dst);</pre>
	 * 
	 * @param in the char source to read from
	 * @param readLeadingArrayWhitespace true to read leading {@code whitespace} chars before checking for the {@code startChar}
	 * @param readLeadingElementWhitespace true to read leading {@code whitespace} chars before reading each array element
	 * @param elementSeparator the char that separates array elements, does not apply if it appears inside a quoted string element
	 * @param allowNewlineInElement true if newlines are allowed inside element strings/text
	 * @param whitespace the legal whitespace characters when {@code readLeadingArrayWhitespace}
	 * or {@code readLeadingElementWhitespace} are true
	 * @param requireStartChar true if a start character is required before the first array element is read
	 * @param startChar the start character to read if {@code requireStartChar} is true
	 * @param requireEndChar true if the end character is required after the last element is read
	 * @param endChar the required end char to read (this character marks the end of the
	 * array only when it appears between elements where an {@code elementSeparator} is expected)
	 * @param dst the parsed element strings or nested lists of strings are added to the end of this list
	 */
	public static final void parseArrayLike(TextParser in, boolean readLeadingArrayWhitespace, boolean readLeadingElementWhitespace,
			char elementSeparator, boolean allowNewlineInElement, char[] whitespace,
			boolean requireStartChar, char startChar, boolean requireEndChar, char endChar, boolean allowNestedArrays, List<Object> dst) {
		char escapeChar = '"';
		char replaceChar = '\\';
		char newlineChar = '\n';
		StringBuilder strDst = new StringBuilder();
		boolean foundEnd = false;
		if(readLeadingArrayWhitespace) {
			ReadWhitespace.readWhitespaceCustom(in, whitespace);
		}
		// require the first character to be an array start
		if(requireStartChar && !in.nextIf(startChar)) {
			throw new IllegalStateException("an array must start with '" + startChar + "'");
		}
		if(requireEndChar && !in.hasNext()) { throw new IllegalStateException("could not find end of array '" + endChar + "'"); }

		for(int i = 0; in.hasNext(); i++) {
			int startElemPos = in.getPosition();

			if(readLeadingElementWhitespace) {
				ReadWhitespace.readWhitespaceCustom(in, whitespace);
			}

			if((i == 0 && in.nextIf(endChar)) || (!requireEndChar && !in.hasNext())) {
				foundEnd = true;
				break;
			}

			strDst.setLength(0);
			// read nested array
			if(allowNestedArrays && ReadIsMatching.isNext(in, startChar)) {
				ArrayList<Object> nestedDst = new ArrayList<>();
				JsonLiteArray.parseArrayLike(in, readLeadingArrayWhitespace, readLeadingElementWhitespace, elementSeparator, allowNewlineInElement, whitespace,
						true, startChar, true, endChar, allowNestedArrays, nestedDst);
				dst.add(nestedDst);
				// the nested array is required to have a closing char so can safely read trailing whitespace, for example in "[a, [b] ]"
				ReadWhitespace.readWhitespaceCustom(in, whitespace);
			}
			// read normal array element (string or partially quoted string)
			else {
				JsonLiteArray.readArrayElementLike(in, replaceChar, escapeChar, elementSeparator, endChar, allowNewlineInElement, newlineChar, strDst);
				dst.add(strDst.toString());
			}

			if(in.nextIf(endChar)) {
				// leave the ending char and mark end of array reached
				foundEnd = true;
				break;
			}
			// read end char since readArrayElementLike does not read the ending char (it could be either ',' or ']')
			in.unread(1);
			boolean prevCharWasElemSeparator = false;
			if(!(prevCharWasElemSeparator = in.nextIf(elementSeparator)) || startElemPos == in.getPosition()) {
				if(!prevCharWasElemSeparator) {
					in.nextChar();
				}
				in.nextIf(elementSeparator);
			}
		}

		if(requireEndChar && !foundEnd) { throw new IllegalStateException("could not find end of array '" + endChar + "'"); }
	}


	/**
	 * @param in
	 * @param chReplace
	 * @param chEsc
	 * @param chEnd
	 * @param allowNewline
	 * @param newlineChar
	 * @param dst
	 * @return true if the read string contained a quoted section part way through it,
	 * false if it contained no quotes or was fully quoted
	 */
	public static final boolean readArrayElementLike(TextParser in, char chReplace, char chEsc, char chEnd, char chEnd2, boolean allowNewline, char newlineChar, Appendable dst) {
		boolean partialQuoted = false;
		try {
			int readUnescaped = in.nextIfNot(chEsc, chEnd, chEnd2, 0, dst);
			if(in.nextIf(chEsc)) {
				partialQuoted = true;
				if(readUnescaped > 0) {
					dst.append(chEsc);
				}
				ReadMatching.readUnescape(in, chReplace, chEsc, allowNewline, newlineChar, dst);
				if(readUnescaped > 0) {
					dst.append(chEsc);
				}
				in.nextIf(chEnd);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return partialQuoted;
	}

}
