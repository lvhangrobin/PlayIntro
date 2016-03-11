

object TestData {
  val homePageResponse = "Welcome to the Time Advisor Research and Development Institute of Science"
  def currencyDateResponse(date: String) = s"TARDIS currency advisor for date $date"
  def invalidDateResponse(date: String) = s"$date is in wrong format"
  def outOfRangeDateResponse(date: String) = s"requested $date is not in range"
}
