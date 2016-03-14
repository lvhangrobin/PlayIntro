import play.api.test.FakeRequest
import play.api.test.Helpers._

object TestData {
  val homePageResponse = "Welcome to the Time Advisor Research and Development Institute of Science"
  val validApiKey = "abcdefghijklmnopqrstuvwxyz123456"
  val validApiKeyForCache = "zzcdefghijklmnopqrstuvwxyz123456"
  val invalidApiKey = "!bcdefghijklmnopqrstuvwxyz123456"

  def currencyDateResponse(date: String) = s"TARDIS currency advisor for date $date"
  def invalidDateResponse(date: String) = s"$date is in wrong format"
  def outOfRangeDateResponse(date: String) = s"requested $date is not in range"
  def fakeGetRequestWithAuth(url: String) = FakeRequest(GET, url).withHeaders("api-key" -> validApiKey)
  def fakeGetRequestWithAuthForCache(url: String) = FakeRequest(GET, url).withHeaders("api-key" -> validApiKeyForCache)
  def fakeGetRequestWithInvalidAuth(url: String) = FakeRequest(GET, url).withHeaders("api-key" -> invalidApiKey)
}
