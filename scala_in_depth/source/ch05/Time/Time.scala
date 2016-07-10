object Time {
    case class TimeRange(start: Long, end: Long)
    implicit def longWrapper(start: Long) = new {
        def to(end: Long) = TimeRange(start, end)
    }
}
