package org.zir.dragonieze;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DragoniezeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DragoniezeApplication.class, args);
    }

}
