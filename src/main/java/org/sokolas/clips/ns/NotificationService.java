package org.sokolas.clips.ns;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sokolas.clips.data.Clip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    @Autowired
    private WebClient tgClient;

    @Value("${chat_id}")
    private String chatId;

    public TgResponse sendClip(Clip clip) {
        return tgClient.get().uri(builder -> builder
                .path("/sendMessage")
                .queryParam("chat_id", chatId)
                .queryParam("text", clip.getTitle() + " by " + clip.getAuthor() + "\n"
                        + clip.getUrl())
                .build())
        .retrieve().bodyToMono(TgResponse.class).block();
    }
}
