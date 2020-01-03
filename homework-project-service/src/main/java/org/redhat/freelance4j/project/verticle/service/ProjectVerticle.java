package org.redhat.freelance4j.project.verticle.service;

import java.util.Optional;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;

public class ProjectVerticle extends AbstractVerticle {

	private ProjectService service;

	private MongoClient client;

	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		client = MongoClient.createShared(vertx, config());

		service = ProjectService.create(vertx, config(), client);

		ServiceBinder binder = new ServiceBinder(vertx);
		binder.setAddress(ProjectService.ADDRESS);
		binder.register(ProjectService.class, service);

		startPromise.complete();
	}

	@Override
	public void stop() throws Exception {
		Optional.ofNullable(client).ifPresent(c -> c.close());
	}

}
