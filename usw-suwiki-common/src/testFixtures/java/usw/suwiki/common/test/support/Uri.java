package usw.suwiki.common.test.support;

import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.net.URI;

public final class Uri implements Serializable {
  final String urlTemplate;
  final URI resource;

  private Uri(String urlTemplate, URI resource) {
    this.urlTemplate = urlTemplate;
    this.resource = resource;
  }

  public static Uri of(String url, Object... vars) {
    String uriString = url.isEmpty() ? "/" : url;
    return new Uri(url, UriComponentsBuilder.fromUriString(uriString).buildAndExpand(vars).encode().toUri());
  }
}
