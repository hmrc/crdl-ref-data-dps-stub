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

import com.google.inject.ImplementedBy

import java.io.FileNotFoundException
import scala.io.Source

@ImplementedBy(classOf[DefaultFileReader])
trait FileReader {
  @throws[FileNotFoundException]
  def read(path: String): String
}

class DefaultFileReader extends FileReader {
  override def read(path: String): String = {
    val source = Source.fromResource(path)
    try source.mkString
    finally source.close()
  }
}
