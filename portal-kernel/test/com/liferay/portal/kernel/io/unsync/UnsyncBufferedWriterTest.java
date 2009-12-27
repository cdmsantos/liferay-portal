/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.kernel.io.unsync;

import com.liferay.portal.kernel.test.TestCase;

import java.io.IOException;
import java.io.StringWriter;

/**
 * <a href="UnsyncBufferedWriterTest.java.html"><b><i>View Source</i></b></a>
 *
 * @author Shuyang Zhou
 */
public class UnsyncBufferedWriterTest extends TestCase {

	public void testBlockWrite() throws IOException {
		StringWriter stringWriter = new StringWriter();

		UnsyncBufferedWriter unsyncBufferedWriter = new UnsyncBufferedWriter(
			stringWriter, 3);

		// Normal

		unsyncBufferedWriter.write("ab".toCharArray());

		assertEquals(2, unsyncBufferedWriter.count);
		assertEquals('a', unsyncBufferedWriter.buffer[0]);
		assertEquals('b', unsyncBufferedWriter.buffer[1]);
		assertEquals(0, stringWriter.getBuffer().length());

		// Auto flush

		unsyncBufferedWriter.write("cd".toCharArray());

		assertEquals(2, unsyncBufferedWriter.count);
		assertEquals('c', unsyncBufferedWriter.buffer[0]);
		assertEquals('d', unsyncBufferedWriter.buffer[1]);
		assertEquals(2, stringWriter.getBuffer().length());
		assertEquals("ab", stringWriter.getBuffer().toString());

		// Direct with auto flush

		unsyncBufferedWriter.write("efg".toCharArray());

		assertEquals(0, unsyncBufferedWriter.count);
		assertEquals(7, stringWriter.getBuffer().length());
		assertEquals("abcdefg", stringWriter.getBuffer().toString());

		// Direct without auto flush

		unsyncBufferedWriter.write("hij".toCharArray());

		assertEquals(0, unsyncBufferedWriter.count);
		assertEquals(10, stringWriter.getBuffer().length());
		assertEquals("abcdefghij", stringWriter.getBuffer().toString());
	}

	public void testClose() throws IOException {
		UnsyncBufferedWriter unsyncBufferedWriter = new UnsyncBufferedWriter(
			new StringWriter());

		assertNotNull(unsyncBufferedWriter.buffer);
		assertNotNull(unsyncBufferedWriter.writer);

		unsyncBufferedWriter.close();

		assertNull(unsyncBufferedWriter.buffer);
		assertNull(unsyncBufferedWriter.writer);
	}

	public void testConstructor() {
		UnsyncBufferedWriter unsyncBufferedWriter = new UnsyncBufferedWriter(
			new StringWriter());

		assertEquals(8192, unsyncBufferedWriter.buffer.length);
		assertEquals(0, unsyncBufferedWriter.count);

		unsyncBufferedWriter = new UnsyncBufferedWriter(new StringWriter(), 10);

		assertEquals(10, unsyncBufferedWriter.buffer.length);
		assertEquals(0, unsyncBufferedWriter.count);
	}

	public void testNewLine() throws IOException{
		StringWriter stringWriter = new StringWriter();

		UnsyncBufferedWriter unsyncBufferedWriter = new UnsyncBufferedWriter(
			stringWriter, 10);

		unsyncBufferedWriter.newLine();

		String lineSeparator = System.getProperty("line.separator");

		assertEquals(lineSeparator.length(), unsyncBufferedWriter.count);

		unsyncBufferedWriter.write('a');

		assertEquals(lineSeparator.length() + 1, unsyncBufferedWriter.count);

		unsyncBufferedWriter.newLine();

		assertEquals(
			lineSeparator.length() * 2 + 1, unsyncBufferedWriter.count);

		unsyncBufferedWriter.flush();

		assertEquals(
			lineSeparator + "a" + lineSeparator, stringWriter.toString());
	}

	public void testStringWrite() throws IOException {
		StringWriter stringWriter = new StringWriter();

		UnsyncBufferedWriter unsyncBufferedWriter = new UnsyncBufferedWriter(
			stringWriter, 3);

		// Normal

		unsyncBufferedWriter.write("ab");

		assertEquals(2, unsyncBufferedWriter.count);
		assertEquals('a', unsyncBufferedWriter.buffer[0]);
		assertEquals('b', unsyncBufferedWriter.buffer[1]);
		assertEquals(0, stringWriter.getBuffer().length());

		// Auto flush

		unsyncBufferedWriter.write("cd");

		assertEquals(1, unsyncBufferedWriter.count);
		assertEquals('d', unsyncBufferedWriter.buffer[0]);
		assertEquals(3, stringWriter.getBuffer().length());
		assertEquals("abc", stringWriter.getBuffer().toString());

		// Cycle

		unsyncBufferedWriter.write("efghi".toCharArray());

		assertEquals(0, unsyncBufferedWriter.count);
		assertEquals(9, stringWriter.getBuffer().length());
		assertEquals("abcdefghi", stringWriter.getBuffer().toString());
	}

	public void testWrite() throws IOException {
		StringWriter stringWriter = new StringWriter();

		UnsyncBufferedWriter unsyncBufferedWriter = new UnsyncBufferedWriter(
			stringWriter, 3);

		// Normal

		unsyncBufferedWriter.write('a');

		assertEquals(1, unsyncBufferedWriter.count);
		assertEquals('a', unsyncBufferedWriter.buffer[0]);
		assertEquals(0, stringWriter.getBuffer().length());

		unsyncBufferedWriter.write('b');

		assertEquals(2, unsyncBufferedWriter.count);
		assertEquals('b', unsyncBufferedWriter.buffer[1]);
		assertEquals(0, stringWriter.getBuffer().length());

		unsyncBufferedWriter.write('c');

		assertEquals(3, unsyncBufferedWriter.count);
		assertEquals('c', unsyncBufferedWriter.buffer[2]);
		assertEquals(0, stringWriter.getBuffer().length());

		// Auto flush

		unsyncBufferedWriter.write('d');

		assertEquals(1, unsyncBufferedWriter.count);
		assertEquals('d', unsyncBufferedWriter.buffer[0]);
		assertEquals(3, stringWriter.getBuffer().length());
		assertEquals("abc", stringWriter.getBuffer().toString());
	}

}