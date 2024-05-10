package usw.suwiki.auth.core.annotation;

import org.springframework.core.annotation.AliasFor;
import usw.suwiki.domain.user.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {
  @AliasFor("role")
  Role value() default Role.USER;

  @AliasFor("value")
  Role role() default Role.USER;
}
