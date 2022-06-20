package org.sokolas.clips.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.Document;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.sokolas.clips.twitch.ClipModel;
import org.sokolas.clips.twitch.ClipsResponse;
import org.sokolas.clips.twitch.TokenModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.dizitart.no2.objects.filters.ObjectFilters.eq;

@Slf4j
public class ClipsService {

    @Autowired
    private ObjectRepository<Clip> repository;

    @Autowired
    private WebClient twitchClient;

    @Autowired
    private WebClient twitchIdClient;

    @Value("${broadcaster_id}")
    private String broadcasterId;

    @Value("${client_id}")
    private String clientId;

    @Value("${secret}")
    private String secret;

    @Autowired
    private ObjectMapper objectMapper;

    private volatile String token = "";

    @PostConstruct
    public void setup() {
        getTokenInternal();
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroyed");
    }

    public List<String> list() {
        Cursor<Clip> clips = repository.find();
        ArrayList<String> result = new ArrayList<>();
        clips.forEach(clip -> result.add(clip.getId()));
        return result;
    }

    public String listAsString() {
        Cursor<Clip> cursor = repository.find();
        StringBuilder sb = new StringBuilder();
        cursor.forEach(clip -> sb.append(clip.toString()).append("\n"));
        return sb.toString();
    }

    public Clip find(String id) {
        Cursor<Clip> cursor = repository.find(eq("id", id));
        return cursor.firstOrDefault();
    }

    public Clip insert(String clipId, boolean broadcasted, String author, String title, String url, ZonedDateTime time) {
        Clip clip = new Clip(clipId, broadcasted, author, title, url, time);
        repository.update(eq("id", clipId), clip, true);
        return clip;
    }

    public void setBroadcasted(String clipId) {
        repository.update(eq("id", clipId), Document.createDocument("broadcasted", true));
    }

    public List<ClipModel> getClipsFromTwitch(ZonedDateTime from) {
        String date = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        var clips = new ArrayList<ClipModel>();
        var resp = new ClipsResponse();
        String cursor = null;
        do {
            resp = getClipsFromTwitchInternal(date, cursor);
            if (resp != null) {
                clips.addAll(resp.getData());
                if (resp.getPagination() != null && resp.getPagination().getCursor() != null) {
                    cursor = resp.getPagination().getCursor();
                }
            }
        } while (resp != null && cursor != null);
        return clips;
    }

    public static class NotAuthorizedException extends Exception {
    }

    private ClipsResponse getClipsFromTwitchInternal(String from, String cursor) {
        return twitchClient
                .get().uri(builder -> builder
                        .path("/")
                        .queryParam("broadcaster_id", broadcasterId)
                        .queryParam("first", "100")
                        .queryParam("started_at", from)
                        .queryParam("after", cursor)
                        .build())
                .header("Authorization", token)
                .retrieve()
                .onStatus(status -> status.value() == 401, response -> Mono.just(new NotAuthorizedException()))
                .bodyToMono(ClipsResponse.class)
                .doOnError(NotAuthorizedException.class, e -> getTokenInternal())
                .block();
    }

    private void getTokenInternal() {
        log.info("Getting new token");
        var result = twitchIdClient
                .post().uri(builder -> builder
                    .path("")
                    .queryParam("client_id", clientId)
                    .queryParam("grant_type", "client_credentials")
                    .queryParam("client_secret", secret)
                    .build())
                .retrieve()
                .bodyToMono(TokenModel.class)
                .block();
        log.info("New token: " + result.getAuthorization());
        this.token = result.getAuthorization();
    }

    public List<Clip> findUnsent(ZonedDateTime from) {
        log.info("Searching from " + from);
        Cursor<Clip> clips = repository.find(eq("broadcasted", false));
        ArrayList<Clip> result = new ArrayList<>();
        clips.forEach(clip -> {
            if (clip.getTime().isBefore(from)) {
                result.add(clip);
            } else {
                log.info("Too early " + clip.getTime().toString());
            }
        });
        return result;
    }
}
