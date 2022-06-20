package org.sokolas.clips.twitch;

import lombok.Data;

import java.io.Serializable;

@Data
public class Pagination implements Serializable {
    private String cursor;
}
