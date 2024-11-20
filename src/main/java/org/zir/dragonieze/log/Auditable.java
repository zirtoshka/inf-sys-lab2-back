package org.zir.dragonieze.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME) //доступна во время выполнения
public @interface Auditable {
    String action();

    String entity();
}
