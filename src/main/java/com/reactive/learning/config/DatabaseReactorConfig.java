package com.reactive.learning.config;


import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * Configuration class for setting up the reactive MongoDB configuration.
 * Extends AbstractReactiveMongoConfiguration to provide custom configurations
 * for reactive MongoDB. It defines the reactive MongoDB client, database name,
 * and template required for interacting with MongoDB in a reactive manner.
 * <p>
 * Annotations used:
 * - @Configuration: Specifies that this class provides Spring configuration.
 * - @EnableReactiveMongoRepositories: Enables reactive support for MongoDB repositories.
 */
@EnableReactiveMongoRepositories
public class DatabaseReactorConfig extends AbstractReactiveMongoConfiguration {

    @Bean
    public MongoClient reactiveMongoClient() {
        return MongoClients.create();
    }

    @Override
    protected String getDatabaseName() {
        return "learning";
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(MongoClient reactiveMongoClient) {
        return new ReactiveMongoTemplate(reactiveMongoClient, getDatabaseName());
    }
}
