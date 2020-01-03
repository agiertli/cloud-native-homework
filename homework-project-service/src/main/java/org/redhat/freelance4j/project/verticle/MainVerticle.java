package org.redhat.freelance4j.project.verticle;

import org.redhat.freelance4j.project.verticle.service.ProjectService;
import org.redhat.freelance4j.project.verticle.service.ProjectVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

	Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		LOGGER.info("Creating main verticle");

		ConfigStoreOptions jsonConfigStore = new ConfigStoreOptions().setType("json");
		ConfigStoreOptions appStore = new ConfigStoreOptions().setType("configmap").setFormat("yaml")
				.setConfig(new JsonObject().put("name", System.getenv("APP_CONFIGMAP_NAME")).put("key",
						System.getenv("APP_CONFIGMAP_KEY")));

		ConfigRetrieverOptions options = new ConfigRetrieverOptions();
		if (System.getenv("KUBERNETES_NAMESPACE") != null) {
			// we're running in Kubernetes
			options.addStore(appStore);
		} else {
			// default to json based config
			jsonConfigStore.setConfig(config());
			options.addStore(jsonConfigStore);
		}

		LOGGER.info("Configuration processed");

		ConfigRetriever.create(vertx, options).getConfig(ar -> {
			if (ar.succeeded()) {
				LOGGER.info("Entering deployingVerticles method");
				deployVerticles(ar.result(), startPromise);
			} else {
				LOGGER.info("Failed to retrieve the configuration.");
				startPromise.fail(ar.cause());
			}
		});
	}

	private void deployVerticles(JsonObject config, Promise<Void> startPromise) {
		Promise<String> apiVerticlePromise = Promise.promise();
		Promise<String> projectVerticlePromise = Promise.promise();
		ProjectService projectService = ProjectService.createProxy(vertx);

		DeploymentOptions options = new DeploymentOptions();
		options.setConfig(config);

		vertx.deployVerticle(new ProjectVerticle(), options, projectVerticlePromise);
		vertx.deployVerticle(new ApiVerticle(projectService), options, apiVerticlePromise);

		CompositeFuture.all(apiVerticlePromise.future(), projectVerticlePromise.future()).setHandler(ar -> {
			if (ar.succeeded()) {
				LOGGER.info("Verticles deployed successfully.");
				startPromise.complete();
			} else {
				LOGGER.info("WARNINIG: Verticles NOT deployed successfully.");
				startPromise.fail(ar.cause());
			}
		});

	}

	@Override
	public void stop(Promise<Void> stopPromise) throws Exception {
		super.stop(stopPromise);
	}

//	private void deployVerticles(JsonObject config, Promise<Void> startFuture) {
//
//		Future<String> apiVerticleFuture = Future.future();
//		Future<String> catalogVerticleFuture = Future.future();
//
//		CatalogService catalogService = CatalogService.createProxy(vertx);
//		DeploymentOptions options = new DeploymentOptions();
//		options.setConfig(config);
//		vertx.deployVerticle(new CatalogVerticle(), options, catalogVerticleFuture.completer());
//		vertx.deployVerticle(new ApiVerticle(catalogService), options, apiVerticleFuture.completer());
//
//		CompositeFuture.all(apiVerticleFuture, catalogVerticleFuture).setHandler(ar -> {
//			if (ar.succeeded()) {
//				System.out.println("Verticles deployed successfully.");
//				startFuture.complete();
//			} else {
//				System.out.println("WARNINIG: Verticles NOT deployed successfully.");
//				startFuture.fail(ar.cause());
//			}
//		});
//
//	}

}
