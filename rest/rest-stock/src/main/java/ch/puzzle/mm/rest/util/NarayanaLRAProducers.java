package ch.puzzle.mm.rest.util;

import io.narayana.lra.client.internal.proxy.nonjaxrs.LRAParticipantRegistry;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class NarayanaLRAProducers {

    @Produces
    public LRAParticipantRegistry lraParticipantRegistry() {
        return null;
    }
}