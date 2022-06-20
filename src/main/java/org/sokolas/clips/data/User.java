package org.sokolas.clips.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dizitart.no2.objects.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
}
