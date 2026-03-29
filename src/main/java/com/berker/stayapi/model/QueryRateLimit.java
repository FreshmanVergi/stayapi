package com.berker.stayapi.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "query_rate_limits")
public class QueryRateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identifier;

    @Column(nullable = false)
    private LocalDate queryDate;

    @Column(nullable = false)
    private Integer callCount = 0;

    public QueryRateLimit() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String i) { this.identifier = i; }
    public LocalDate getQueryDate() { return queryDate; }
    public void setQueryDate(LocalDate d) { this.queryDate = d; }
    public Integer getCallCount() { return callCount; }
    public void setCallCount(Integer c) { this.callCount = c; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final QueryRateLimit q = new QueryRateLimit();
        public Builder identifier(String i) { q.identifier = i; return this; }
        public Builder queryDate(LocalDate d) { q.queryDate = d; return this; }
        public Builder callCount(Integer c) { q.callCount = c; return this; }
        public QueryRateLimit build() { return q; }
    }
}
