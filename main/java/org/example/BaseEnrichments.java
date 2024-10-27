package org.example;

/**
 *  "Родитель" - интерфейс для всех
 *  типов обогащения, цель - сделать код в соответствии с
 *  правилами SOLID
 */
public interface BaseEnrichments {

    EnrichmentType type();

    Message enrich(Message message);
}
