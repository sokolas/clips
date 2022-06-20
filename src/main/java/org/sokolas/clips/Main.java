package org.sokolas.clips;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Main {

    private static AnnotationConfigApplicationContext ctx;

    public static void main(String[] args) throws Exception {
        ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    }

}
