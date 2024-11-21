package org.example;

import java.util.List;

/**
 * Сервис, регулирующий обогащения
 */
public class EnrichmentService {

    private final List<BaseEnrichments> enrichmentsList;

    public EnrichmentService(List<BaseEnrichments> enrichmentsList) {
        this.enrichmentsList = enrichmentsList;
    }

    /**
     *
     * @param message
     * @param enrichmentType
     *
     * Ищем в доступных классах - обогатителях
     * нужный нам (по EnrichmentType) и вызываем в нем enrich
     *
     * При отсутствии класс - обогатителя для данного типа обогащения
     * кидаем ClassCastException
     */
    public Message enrich(Message message, EnrichmentType enrichmentType) {
        for (BaseEnrichments enrichment : enrichmentsList) {
            if (enrichment.type().equals(enrichmentType)) {
                return enrichment.enrich(message);
            }
        }

        throw new ClassCastException("Такой тип обогащения пока не реализован :( ");
    }
}
