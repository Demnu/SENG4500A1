public enum RangeUpdateStatus {
    ADD,           // The existing range was updated
    SPILT,             // The existing range was split into smaller ranges
    REPLACE,          // The existing range was subsumed by the new range
}
