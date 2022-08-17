/*
 * Copyright (c) 2020-2022 Bradley Larrick. All rights reserved.
 *
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Program for computing the message digest hash value for a file. Explicitly supported algorithms
 * include MD5, SHA-1 (default) and SHA-256. Additional algorithms are supported via the
 * {@code --algorithm} argument.
 *
 * <p>The program usage is:<br>
 *
 * <pre>
 * checksum [-hms] &lt;filename&gt; [&lt;checksum&gt;]
 *      &lt;filename&gt;     The file whose checksum to calculate
 *      [&lt;checksum&gt;]   the expected checksum value
 *  -h, --help         display this help message
 *  -m, --md5          use MD5 algorithm
 *  -s, --sha256       use SHA-256 algorithm
 * </pre>
 *
 * <p>If a checksum value is provided, the program compares the computed checksum to the given
 * value and reports a match or non-match.
 */
@Command(name = "checksum")
@SuppressWarnings("PMD.SystemPrintln")
public final class Checksum implements Callable<Integer> {

  @Option(names = {"-a", "--algorithm"}, description = "the hash algorithm to use",
      defaultValue = "SHA-1")
  String algorithm;

  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
  boolean usageHelp;

  @Option(names = {"-m", "--md5"}, description = "use MD5 algorithm")
  boolean useMd5;

  @Option(names = {"-s", "--sha256"}, description = "use SHA-256 algorithm")
  boolean useSha256;

  @Parameters(index = "0", description = "the file whose checksum to calculate")
  String filename;

  @Parameters(index = "1", arity = "0..1", description = "the expected checksum value")
  String checksum;

  /**
   * Protected constructor to prevent instantiation.
   */
  Checksum() {
    // Defined for javadoc.
  }

  /**
   * Actual program execution.
   *
   * @return 0 if successful
   */
  @Override
  public Integer call() {

    if (useSha256) {
      algorithm = "SHA-256";
    } else if (useMd5) {
      algorithm = "MD5";
    }

    var       ret  = 0;
    final var file = new File(filename);
    try {
      final var digest = MessageDigest.getInstance(algorithm);
      final var buffer = Files.readAllBytes(Path.of(filename));
      digest.update(buffer);

      final var hexValue = new Base16().encode(digest.digest());
      final var newcs = new String(hexValue, Charset.defaultCharset()).toUpperCase(
          Locale.getDefault());
      if (checksum == null) {
        System.out.printf("%s %d %s%n", newcs, file.length(), filename);
      } else if (checksum.equalsIgnoreCase(newcs)) {
        System.out.println("Checksum values match.");
      } else {
        System.err.printf("%s does not match %s%n", newcs, checksum);
        ret = 3;
      }
    } catch (IOException | NoSuchAlgorithmException ex) {
      //noinspection ThrowablePrintedToSystemOut
      System.err.println(ex);
      ret = 1;
    }

    return ret;
  }

  /**
   * The entry point for the Checksum program.
   *
   * @param args command line arguments
   */
  public static void main(final String[] args) {

    System.exit(new CommandLine(new Checksum()).setUsageHelpLongOptionsMaxWidth(24).execute(args));
  }
}
