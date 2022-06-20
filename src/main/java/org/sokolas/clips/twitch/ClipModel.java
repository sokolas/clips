package org.sokolas.clips.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class ClipModel implements Serializable {
    private String id;
    private String url;
    private String title;
    @JsonProperty("creator_name")
    private String creatorName;
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
    @JsonProperty("created_at")
    private ZonedDateTime createdAt;
}
