/*
 * Copyright (c) 2021-2022 Bradley Larrick. All rights reserved.
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.larrick.sample;

import java.nio.charset.StandardCharsets;

/**
 * Provides basic encoding and decoding for hexidecimal strings.
 */
public class Base16 {

  /**
   * Hexidecimal alphabet, uppercase.
   */
  private static final byte[] UPPERCASE = "0123456789ABCDEF".getBytes(StandardCharsets.UTF_8);

  /**
   * Hexidecimal alphabet, lowercase.
   */
  private static final byte[] LOWERCASE = "0123456789abcdef".getBytes(StandardCharsets.UTF_8);

  /**
   * Mask for the lower 4 bits.
   */
  private static final short MASK_4BITS = 0x0F;

  /**
   * Mask for the lower 8 bits.
   */
  private static final short MASK_8BITS = 0xFF;

  /**
   * Constructs a hexadecimal encoder/decoder.
   */
  public Base16() {
    // Included to define javadoc.
  }

  /**
   * Converts an array of bytes into an array of characters representing the hexadecimal values of
   * each byte in order. Defaults to uppercase hexadecimal digits.
   *
   * @param data a {@code byte[]} to convert
   *
   * @return a {@code byte[]} containing hexadecimal characters
   */
  public byte[] encode(final byte[] data) {

    return encode(data, false);
  }

  /**
   * Converts an array of bytes into an array of characters representing the hexadecimal values of
   * each byte in order.
   *
   * @param data        a {@code byte[]} to convert
   * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
   *
   * @return a {@code byte[]} containing hexadecimal characters
   */
  public byte[] encode(final byte[] data, final boolean toLowerCase) {

    return encode(data, toLowerCase ? LOWERCASE : UPPERCASE);
  }

  /**
   * Converts an array of bytes into an array of characters representing the hexadecimal values of
   * each byte in order.
   *
   * @param data     a {@code byte[]} to convert
   * @param toDigits the hexidecimal alphabet to use
   *
   * @return a {@code byte[]} containing hexadecimal characters
   */
  byte[] encode(final byte[] data, final byte[] toDigits) {

    return encode(data, 0, data.length, toDigits);
  }

  /**
   * Converts an array of {@code len} bytes, starting at {@code offset}, into an array of characters
   * representing the hexadecimal values of each byte in order.
   *
   * @param data        a {@code byte[]} to convert
   * @param offset      the starting point within the array
   * @param len         the number of bytes to encode
   * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
   *
   * @return a {@code byte[]} containing hexadecimal characters
   */
  public byte[] encode(final byte[] data, final int offset, final int len,
                       final boolean toLowerCase) {

    return encode(data, offset, len, toLowerCase ? LOWERCASE : UPPERCASE);
  }

  /**
   * Converts an array of {@code len} bytes, starting at {@code offset}, into an array of characters
   * representing the hexadecimal values of each byte in order.
   *
   * @param data     a {@code byte[]} to convert
   * @param offset   the starting point within the array
   * @param len      the number of bytes to encode
   * @param toDigits the hexidecimal alphabet to use
   *
   * @return a {@code byte[]} containing hexadecimal characters
   */
  byte[] encode(final byte[] data, final int offset, final int len, final byte[] toDigits) {

    byte[] buffer    = new byte[2 * len];
    var    buflen    = len;
    var    bufoffset = offset;
    for (int ndx = 0; buflen > 0; ndx += 2, buflen--) {
      final byte hexValue = data[bufoffset++];
      buffer[ndx]     = toDigits[(hexValue >> 4) & MASK_4BITS];
      buffer[ndx + 1] = toDigits[hexValue & MASK_4BITS];
    }

    return buffer;
  }

  /**
   * Converts a {@code String} of hexadecimal digits into an array of bytes of those same values. An
   * exception is thrown if the passed String contains an odd number of characters or a
   * non-hexadecimal digit.
   *
   * @param hexVal a {@code String} containing hexidecimal digits
   *
   * @return a {@code byte[]} of the decoded hexadecimal value
   */
  public byte[] decode(final String hexVal) {

    return decode(hexVal.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Converts an array of character bytes representing hexadecimal values into an array of bytes of
   * those same values. The returned array will be half the length of the passed array, as it takes
   * two characters to represent any given byte. An exception is thrown if the passed char array has
   * an odd number of elements or contains a non-hexadecimal digit.
   *
   * @param data An array of character bytes containing hexidecimal digits
   *
   * @return a {@code byte[]} of the decoded hexadecimal value
   */
  public byte[] decode(final byte[] data) {

    return decode(data, 0, data.length);
  }

  /**
   * Converts an array of character bytes representing hexadecimal values, starting and
   * {@code offset} and continuing for {@code len} bytes, into an array of bytes of those same
   * values. The returned array will be half the given {@code len}, as it takes two characters to
   * represent any given byte. An exception is thrown if {@code len} is an odd number or@{code data}
   * contains a non-hexadecimal digit.
   *
   * @param data   An array of character bytes containing hexidecimal digits
   * @param offset the position within the array to start decoding
   * @param len    the number of bytes to decode
   *
   * @return a {@code byte[]} of the decoded hexadecimal value
   */
  public byte[] decode(final byte[] data, final int offset, final int len) {

    if (len % 2 != 0) {
      throw new IllegalArgumentException("Odd number of characters");
    }

    byte[] buffer    = new byte[len / 2];
    var    bufLen    = len;
    var    bufOffset = offset;
    for (int i = 0; bufLen > 0; i++, bufLen -= 2) {
      var newVal = toDigit(data[bufOffset], bufOffset) << 4;
      bufOffset++;
      newVal += toDigit(data[bufOffset], bufOffset);
      bufOffset++;
      buffer[i] = (byte) (newVal & MASK_8BITS);
    }

    return buffer;
  }

  /**
   * Converts the given hexadecimal character to in integer. An exception is thrown if the given
   * character is an invalid hexadecimal digit.
   *
   * @param ch    the character to convert
   * @param index the position of the character in its source
   *
   * @return the integer value of the character
   */
  private int toDigit(final byte ch, final int index) {

    final var digit = Character.digit(ch, 16);
    if (digit < 0) {
      throw new IllegalArgumentException(
          "Invalid hexadecimal character '" + (char) ch + "' at position " + index);
    }

    return digit;
  }
}
