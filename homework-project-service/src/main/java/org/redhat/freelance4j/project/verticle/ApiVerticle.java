package org.redhat.freelance4j.project.verticle;

import java.util.List;

import org.redhat.freelance4j.project.model.Project;
import org.redhat.freelance4j.project.verticle.service.ProjectService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ApiVerticle extends AbstractVerticle {

	private ProjectService projectService;

	public ApiVerticle(ProjectService projectService) {
		this.projectService = projectService;
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		Router router = Router.router(vertx);
		router.get("/projects").handler(this::getProjects);
		router.get("/projects/:projectId").handler(this::getProject);
		router.get("/projects/status/:status").handler(this::getProjectsByStatus);

		// Health Checks
		router.get("/health/readiness").handler(rc -> rc.response().end("OK"));
		HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx).register("health", f -> health(f));
		router.get("/health/liveness").handler(healthCheckHandler);

		vertx.createHttpServer().requestHandler(router).listen(config().getInteger("project.http.port", 8080),
				result -> {
					if (result.succeeded()) {
						startPromise.complete();
					} else {
						startPromise.fail(result.cause());
					}
				});
	}

	private void getProjectsByStatus(RoutingContext rc) {
		String status = rc.request().getParam("status");
		
		projectService.getProjectsByStatus(status,ar -> {
			if (ar.succeeded()) {
				List<Project> projects = ar.result();
				JsonArray json = new JsonArray();
				projects.stream().map(p -> p.toJson()).forEach(p -> json.add(p));
				rc.response().putHeader("Content-type", "application/json").end(json.encodePrettily());
			} else {
				rc.fail(ar.cause());
			}
		});


	}

	private void getProjects(RoutingContext rc) {
		projectService.getProjects(ar -> {
			if (ar.succeeded()) {
				List<Project> projects = ar.result();
				JsonArray json = new JsonArray();
				projects.stream().map(p -> p.toJson()).forEach(p -> json.add(p));
				rc.response().putHeader("Content-type", "application/json").end(json.encodePrettily());
			} else {
				rc.fail(ar.cause());
			}
		});
	}

	private void getProject(RoutingContext rc) {
		String projectId = rc.request().getParam("projectId");
		projectService.getProject(projectId, ar -> {
			if (ar.succeeded()) {
				Project project = ar.result();
				JsonObject json;
				if (project != null) {
					json = project.toJson();
					rc.response().putHeader("Content-type", "application/json").end(json.encodePrettily());
				} else {
					rc.fail(404);
				}
			} else {
				rc.fail(ar.cause());
			}
		});
	}

	private void health(Promise<Status> promise) {
		projectService.ping(ar -> {
			if (ar.succeeded()) {
				// HealthCheckHandler has a timeout of 1000s. If timeout is exceeded, the future
				// will be failed
				promise.tryComplete();
			} else {
				promise.tryFail("Something went wrong");
			}
		});
	}

}
