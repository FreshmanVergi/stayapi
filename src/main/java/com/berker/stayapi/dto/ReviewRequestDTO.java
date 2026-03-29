package com.berker.stayapi.dto;

import jakarta.validation.constraints.*;

public class ReviewRequestDTO {
    @NotNull private Long stayId;
    @NotNull @Min(1) @Max(5) private Integer rating;
    private String comment;

    public Long getStayId() { return stayId; }
    public void setStayId(Long s) { this.stayId = s; }
    public Integer getRating() { return rating; }
    public void setRating(Integer r) { this.rating = r; }
    public String getComment() { return comment; }
    public void setComment(String c) { this.comment = c; }
}
