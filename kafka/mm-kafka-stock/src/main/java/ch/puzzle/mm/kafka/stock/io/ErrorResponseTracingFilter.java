package ch.puzzle.mm.kafka.stock.io;

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

            String message;
            try {
                message = JsonPath.from(responseContext.getEntity().toString()).get("message");
            } catch (ClassCastException e) {
                message = "ResponseStatus: " + responseContext.getStatusInfo().getReasonPhrase();
            }

            if(tracer != null && tracer.scopeManager() != null && tracer.scopeManager().active() != null) {
                tracer.scopeManager().active().span().setTag(ERROR_TAG, true).log(message);
            }
        }
    }
}
