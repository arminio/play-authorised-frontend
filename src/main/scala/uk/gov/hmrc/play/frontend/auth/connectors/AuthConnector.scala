/*
 * Copyright 2016 HM Revenue & Customs
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

package uk.gov.hmrc.play.frontend.auth.connectors

import java.net.URL

import play.api.libs.json.JsValue
import uk.gov.hmrc.play.frontend.auth.connectors.domain.Authority
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet}

import scala.concurrent.Future

trait AuthConnector {

  val serviceUrl: String

  lazy private val authUrl = new URL(serviceUrl)

  def http: HttpGet

  def currentAuthority(implicit hc: HeaderCarrier): Future[Option[Authority]] = {
    http.GET[Authority](new URL(authUrl, "/auth/authority").toString)
      .map(addContextToLinks)
      .map(Some.apply) // Option return is legacy of previous http library now baked into this class's api
  }

  private def addContextToLinks(authority: Authority) =
    authority.copy(
      enrolments = new URL(authUrl, authority.enrolments).toString,
      userDetailsLink = new URL(authUrl, authority.userDetailsLink).toString
    )

  def getJson(url: URL)(implicit hc: HeaderCarrier): Future[JsValue] = {
    http.GET[JsValue](url.toString)
  }

}
