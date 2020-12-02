package ch.puzzle.rest.exception;

import io.opentracing.Tracer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class TraceableExceptionMapper {

    @Inject
    Tracer tracer;

    public void logTrace(Throwable e) {
        tracer.scopeManager().active().span().log(e.getClass() + ": " + e.getMessage());
    }
}
