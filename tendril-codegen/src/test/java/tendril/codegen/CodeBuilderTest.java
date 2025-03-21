/*
 * Copyright 2024 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tendril.codegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.test.AbstractUnitTest;
import tendril.util.TendrilStringUtil;

/**
 * Test case for {@link CodeBuilder}
 */
public class CodeBuilderTest extends AbstractUnitTest {

	// Instance to test
	private CodeBuilder builder;
	private final List<String> expectedLines = new ArrayList<>();

	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		expectedLines.clear();
		builder = new CodeBuilder();
	}

	/**
	 * Verify that by default the builder produces an empty string
	 */
	@Test
	public void testEmptyBuilder() {
		Assertions.assertEquals("", builder.get());
	}
	
	/**
	 * Verify that a builder with initial lines accounts for those initial lines
	 */
	@Test
	public void testInitialLines() {
	    builder = new CodeBuilder("a", "b", "c", "d");
        expectedLines.add("a");
        expectedLines.add("b");
        expectedLines.add("c");
        expectedLines.add("d");
	    assertBuilder();
	}

	/**
	 * Verify a code without indents can be created properly
	 */
	@Test
	public void testNoIndents() {
		append(0, "Line1", "Line2", "This is another line");
		appendBlank();
		append(0, "A");
		appendBlank();
		append(0, "acbd"," abc123");
		appendBlank();
		appendBlank();
		appendBlank();
		assertBuilder();
	}
	
	/**
	 * Verify that a code with a single indentation is created properly
	 */
	@Test
	public void testSingleIndent() {
		append(0, "a", "b", "c");
		builder.indent();
		append(1, "d", "e", "f");
		appendBlank();
		append(1, "1", "2", "3", "4");
		appendBlank();
		builder.deIndent();
		append(0, "qwerty");
		assertBuilder();
	}
	
	/**
	 * Verify that a code with multiple indentations is created properly
	 */
	@Test
	public void testMultipleIndents() {
		appendBlank();
		builder.indent();
		builder.indent();
		builder.indent();
		appendBlank();
		append(3, "q", "w", "e");
		builder.indent();
		append(4, "a");
		builder.deIndent();
		appendBlank();
		append(3, "1", "2", "3");
		builder.deIndent();
		append(2, "p");
		builder.deIndent();
		append(1, "b", "sdf");
		builder.deIndent();
		appendBlank();
		append(0, "zxc", "vbn", "mnb");
		builder.deIndent();
		builder.deIndent();
		builder.deIndent();
		append(0, "abcdef");
		builder.indent();
		append(1, "ghijk");
		assertBuilder();
		
	}
	
	/**
	 * Verify that multi-line Strings can be added properly
	 */
	@Test
	public void testMultiLineAppend() {
	    builder.appendMultiLine(TendrilStringUtil.join(Arrays.asList("abc", "def", "ghi"), System.lineSeparator()));
        expectedLines.add("abc");
        expectedLines.add("def");
        expectedLines.add("ghi");
        assertBuilder();
        
        builder.indent();
        builder.appendMultiLine(TendrilStringUtil.join(Arrays.asList("jkl", "mno", "pqr"), System.lineSeparator()));
        expectedLines.add("    jkl");
        expectedLines.add("    mno");
        expectedLines.add("    pqr");
        assertBuilder();
        
        builder.indent();
        builder.appendMultiLine(TendrilStringUtil.join(Arrays.asList("stu", "vwx", "yz"), System.lineSeparator()));
        expectedLines.add("        stu");
        expectedLines.add("        vwx");
        expectedLines.add("        yz");
        assertBuilder();
        
        builder.deIndent();
        builder.appendMultiLine("123");
        expectedLines.add("    123");
        assertBuilder();
        
        builder.deIndent();
        builder.appendMultiLine("");
        expectedLines.add("");
        assertBuilder();
	}
    
    /**
     * Verify that multi-line Strings can be added properly
     */
    @Test
    public void testMultiLineAppendWithInitialLines() {
        builder = new CodeBuilder("qwe", "asd", "zxc");
        expectedLines.add("qwe");
        expectedLines.add("asd");
        expectedLines.add("zxc");
        
        builder.appendMultiLine(TendrilStringUtil.join(Arrays.asList("abc", "def", "ghi"), System.lineSeparator()));
        expectedLines.add("abc");
        expectedLines.add("def");
        expectedLines.add("ghi");
        assertBuilder();
        
        builder.indent();
        builder.appendMultiLine(TendrilStringUtil.join(Arrays.asList("jkl", "mno", "pqr"), System.lineSeparator()));
        expectedLines.add("    jkl");
        expectedLines.add("    mno");
        expectedLines.add("    pqr");
        assertBuilder();
        
        builder.indent();
        builder.appendMultiLine(TendrilStringUtil.join(Arrays.asList("stu", "vwx", "yz"), System.lineSeparator()));
        expectedLines.add("        stu");
        expectedLines.add("        vwx");
        expectedLines.add("        yz");
        assertBuilder();
        
        builder.deIndent();
        builder.appendMultiLine("123");
        expectedLines.add("    123");
        assertBuilder();
        
        builder.deIndent();
        builder.appendMultiLine("");
        expectedLines.add("");
        assertBuilder();
    }

	/**
	 * Appends the lines to the builder, and build up the expected text
	 * 
	 * @param indent int the number of indents expected
	 * @param lines String... the lines that are to be appended
	 */
	private void append(int indent, String... lines) {
		String indentation = StringUtils.repeat(' ', indent * 4);

		for (String s : lines) {
			builder.append(s);
			expectedLines.add(indentation + s);
		}
	}

	/**
	 * Append a blank line to the builder as well as the expected text
	 */
	private void appendBlank() {
		builder.blankLine();
		expectedLines.add("");
	}
	
	/**
	 * Assert that the text generated by the builder matches expectations
	 */
	private void assertBuilder() {
		Assertions.assertEquals(StringUtils.join(expectedLines, System.lineSeparator()) + System.lineSeparator(), builder.get());
	}
}
