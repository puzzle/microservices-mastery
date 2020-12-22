package ch.puzzle.mm.rest.util;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Rewrite the RequestURI base which does not correspond to the container or service name in a docker network
 * if application.rewrite.base.enabled=true the filter will rewrite the baseHost to the value given by
 * application.rewrite.base.host
 */
@Provider
@PreMatching
@Priority(500)
public class DockerRewriteContainerRequestFilter implements ContainerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(DockerRewriteContainerRequestFilter.class.getName());

    @ConfigProperty(name = "application.rewrite.base.enabled", defaultValue = "false")
    public Boolean rewriteEnabled;

    @ConfigProperty(name = "application.rewrite.base.host")
    public String baseHost;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(!rewriteEnabled) {
            return;
        }

        UriInfo info = requestContext.getUriInfo();
        if(!info.getBaseUri().getHost().equalsIgnoreCase(baseHost)) {
            String pre = info.getAbsolutePath().toString();
            requestContext.setRequestUri(info.getBaseUriBuilder().host(baseHost).build(), info.getRequestUriBuilder().host(baseHost).build());
            logger.debug("Rewriting " + pre + " to " + requestContext.getUriInfo().getAbsolutePath().toString());
        }
    }
}
