package ch.puzzle.mm.rest.util;

import io.opentracing.Tracer;
import io.restassured.path.json.JsonPath;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class ErrorResponseTracingFilter implements ContainerResponseFilter {

    @Inject
    Tracer tracer;

    private static final String ERROR_TAG = "error";

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Response.Status.Family responseFamily = responseContext.getStatusInfo().getFamily();
        if (Response.Status.Family.SUCCESSFUL != responseFamily &&
                Response.Status.Family.INFORMATIONAL != responseFamily &&
                Response.Status.Family.REDIRECTION != responseFamily) {

            String message = null;
            try {
                Object entity = responseContext.getEntity();
                if(entity != null) {
                    message = JsonPath.from(entity.toString()).get("message");
                }
            } catch (ClassCastException e) {
                message = "ResponseStatus: " + responseContext.getStatusInfo().getReasonPhrase();
            }

            tracer.scopeManager().active().span().setTag(ERROR_TAG, true).log(message);
        }
    }
}