package org.sokolas.clips;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.sokolas.clips.data.Clip;
import org.sokolas.clips.data.ClipsService;
import org.sokolas.clips.ns.NotificationService;
import org.sokolas.clips.twitch.ClipModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Configuration
public class JobConfig {
    @Autowired
    private ClipsService clipsService;

    @Autowired
    private NotificationService ns;

    @Scheduled(initialDelay = 30000, fixedDelay = 60000)
    public void get() {
        List<ClipModel> fromTwitch = clipsService.getClipsFromTwitch(
                ZonedDateTime.now(ZoneId.of("UTC")).minus(1, ChronoUnit.HOURS)
        );

        fromTwitch.forEach(clipModel -> {
            var id = clipModel.getId();
            log.info(id + " - " + clipModel.getTitle());
            var saved = clipsService.find(id);
            if (saved == null) {
                log.info("New clip! " + id);
                var clip = clipsService.insert(id, false,
                        clipModel.getCreatorName(), clipModel.getTitle(), clipModel.getUrl(), clipModel.getCreatedAt());
            }
        });
    }

    @Scheduled(initialDelay = 30000, fixedDelay = 60000)
    public void send() {
        log.info("list unsent");
        List<Clip> unsent = clipsService.findUnsent(ZonedDateTime.now().minus(5, ChronoUnit.MINUTES));
        unsent.forEach(clip -> {
            log.info("Sending " + clip.toString());
            var result = ns.sendClip(clip);
            log.info(result.toString());
            if (BooleanUtils.isTrue(result.getOk())) {
                clipsService.setBroadcasted(clip.getId());
            }
        });

    }
}
