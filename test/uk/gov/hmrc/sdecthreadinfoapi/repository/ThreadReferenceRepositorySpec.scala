/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.sdecthreadinfoapi.repository

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.sdecthreadinfoapi.model.ThreadReference
import uk.gov.hmrc.sdecthreadinfoapi.stubs.ThreadReferenceRepository

class ThreadReferenceRepositorySpec extends AnyWordSpec with Matchers {

  val repo = new ThreadReferenceRepository

  "ThreadReferenceRepository" should {
    "return a seeded thread reference" in {
      repo.getThreadReference("1") shouldBe Some(ThreadReference("1", "THREAD-001"))
    }

    "return None for an unknown thread reference" in {
      repo.getThreadReference("999") shouldBe None
    }

    "allow inserting a new thread reference" in {
      val ref = ThreadReference("10", "THREAD-010")
      repo.insertThreadReference(ref)
      repo.getThreadReference("10") shouldBe Some(ref)
    }
  }
}
