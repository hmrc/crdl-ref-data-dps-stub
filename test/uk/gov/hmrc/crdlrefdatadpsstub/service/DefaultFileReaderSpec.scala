/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.crdlrefdatadpsstub.service

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.nio.file.{Files, Paths}

class DefaultFileReaderSpec extends AnyWordSpec with Matchers {
  "DefaultFileReader" should {
    "read the contents of a file" in {
      val expectedJson = """{ "countrycode": "United Kingdom"}"""
      val tempFile     = java.io.File.createTempFile("test-file", ".json")
      tempFile.deleteOnExit()

      Files.write(Paths.get(tempFile.getAbsolutePath), expectedJson.getBytes)

      val reader = new DefaultFileReader
      val result = reader.read(tempFile.getAbsolutePath)
      result shouldBe expectedJson
    }
  }
}
