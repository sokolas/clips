package org.sokolas.clips;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import org.sokolas.clips.data.Clip;
import org.sokolas.clips.data.ClipsService;
import org.sokolas.clips.data.User;
import org.sokolas.clips.data.UserService;
import org.sokolas.clips.ns.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Slf4j
@Configuration
@PropertySource("classpath:/application.properties")
@Import(JobConfig.class)
@EnableScheduling
public class AppConfig {
    public static final String CLIPS_API = "https://api.twitch.tv/helix/clips";
    public static final String ID_API = "https://id.twitch.tv/oauth2/token";

    public static final String TG_API = "https://api.telegram.org/";

    @Value("${dbpath}")
    private String dbPath;

    @Value("${db_user}")
    private String dbUser;

    @Value("${db_password}")
    private String dbPassword;

    @Value("${client_id}")
    private String clientId;

    @Value("${tg_token}")
    private String token;

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(1);
    }

    @Bean
    public Nitrite db() {
        return Nitrite.builder()
                .compressed()
                .filePath(dbPath)
                .openOrCreate(dbUser, dbPassword);
    }

    @Bean
    public WebClient twitchClient() {
        return WebClient.builder()
                .baseUrl(CLIPS_API)
                .defaultHeader("Client-ID", clientId)
                .build();
    }

    @Bean
    public WebClient twitchIdClient() {
        return WebClient.builder()
                .baseUrl(ID_API)
                .build();
    }

    @Bean
    public WebClient tgClient() {
        return WebClient.builder()
                .baseUrl(TG_API + "/bot" + token)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public ObjectRepository<Clip> clipRepository(@Autowired Nitrite db) {
        return db.getRepository(Clip.class);
    }

    @Bean
    public ClipsService clipsService() {
        return new ClipsService();
    }

    @Bean
    public UserService userService(@Autowired Nitrite db) {
        return new UserService(db.getRepository(User.class));
    }

    @Bean
    public NotificationService notificationService() {
        return new NotificationService();
    }
}
