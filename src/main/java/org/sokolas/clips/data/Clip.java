package org.sokolas.clips.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor @NoArgsConstructor
@Index(value = "broadcasted", type = IndexType.NonUnique)
public class Clip {
    @Id
    private String id;
    private Boolean broadcasted;

    private String author;
    private String title;
    private String url;
    private ZonedDateTime time;
}
