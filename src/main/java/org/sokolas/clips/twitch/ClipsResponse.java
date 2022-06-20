package org.sokolas.clips.twitch;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClipsResponse implements Serializable {
    private List<ClipModel> data;
    private Pagination pagination;
}
