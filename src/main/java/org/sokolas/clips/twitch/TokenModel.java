package org.sokolas.clips.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class TokenModel implements Serializable {
    @JsonProperty("access_token")
    private String token;
    @JsonProperty("token_type")
    private String tokenType;

    public String getAuthorization() {
        return "Bearer " + token;
    }
}
