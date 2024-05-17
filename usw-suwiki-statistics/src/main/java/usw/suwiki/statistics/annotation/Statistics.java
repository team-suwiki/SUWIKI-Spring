package usw.suwiki.statistics.annotation;

import usw.suwiki.statistics.log.MonitorTarget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Statistics {
  MonitorTarget target();
}
