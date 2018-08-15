/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Matt Fellows (OneGeek)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package au.com.onegeek.sbtdotenv

import java.io.File
import org.scalatest.{ Matchers, WordSpec }

/**
 * Created by mfellows on 20/07/2014.
 */
class SbtDotenvSpec extends WordSpec with Matchers {
  "The plugin parser" should {
    "do nothing if no .env file exists" in {
      val file = new File("thisfilecannotexistunlessyoucreateit")
      val map = SbtDotenv.parseFile(file)
      map should equal(None)
    }

    "read .env file into an environment Map" in {
      val file = new File("./src/test/resources/.dotenv.valid")
      val map = SbtDotenv.parseFile(file)

      map.get("MONGO_PORT") should equal("17017")
      map.get("COVERALLS_REPO_TOKEN") should equal("aoeucaPDc2rvkFugUGlNaCGu3EOeoaeu63WLo5")
    }

    "not accept empty lines" in {
      SbtDotenv.parseLine("") should equal(None)
    }

    "not accept numeric variable names" in {
      SbtDotenv.parseLine("1234=5678") should equal(None)
    }

    "not accept lines with no assignment" in {
      SbtDotenv.parseLine("F") should equal(None)
    }

    "accept unquoted strings containing whitespace" in {
      SbtDotenv.parseLine("SOMETHING=I love kittens") should equal(Some("SOMETHING", "I love kittens"))
    }

    "accept lines with trailing comments" in {
      SbtDotenv.parseLine("WITHOUT_COMMENT=ThisIsValue # here is a comment") should equal(Some("WITHOUT_COMMENT", "ThisIsValue"))
    }

    "accept lines with URLs containing # characters" in {
      SbtDotenv.parseLine("WITH_HASH_URL=http://example.com#awesome-id") should equal(Some("WITH_HASH_URL", "http://example.com#awesome-id"))
    }

    "accept lines with quoted variables" in {
      SbtDotenv.parseLine("FOO='a=b==ccddd'") should equal(Some("FOO", "'a=b==ccddd'"))
    }

    "accept lines with whitespace around assignment operator" in {
      SbtDotenv.parseLine("FOO   =   boo") should equal(Some("FOO", "boo"))
    }

    "validate correct lines in a .env file" in {
      SbtDotenv.parseLine("FOO=bar") should equal(Some("FOO", "bar"))

      SbtDotenv.parseLine("FOO=1234") should equal(Some("FOO", "1234"))

      SbtDotenv.parseLine("F.OO=bar") should equal(None)

      SbtDotenv.parseLine("COVERALLS_REPO_TOKEN=NTHnTHSNthnTHSntNt09aoesNTH6") should equal(Some("COVERALLS_REPO_TOKEN", "NTHnTHSNthnTHSntNt09aoesNTH6"))
    }
  }
}
