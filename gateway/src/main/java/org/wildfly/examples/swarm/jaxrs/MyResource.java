package org.wildfly.examples.swarm.jaxrs;

import java.net.URI;
import java.net.URISyntaxException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

/**
 * @author Bob McWhirter
 */
@Path("/gateway")
@ApplicationScoped
public class MyResource {
	Logger LOGGER = LoggerFactory.getLogger(MyResource.class);

	@Inject
	@ConfigurationValue("project.service.url")
	String projectURL;

	@Inject
	@ConfigurationValue("freelancer.service.url")
	String freelancerURL;

	@GET
	@Path("/projects")
	@Produces("application/json")
	public JsonArray getAllProjects() throws URISyntaxException {

		URI apiUri = new URI(projectURL);
		ProjectService projectService = RestClientBuilder.newBuilder().baseUri(apiUri).build(ProjectService.class);

		JsonArray projects = projectService.getAllProjects();

		LOGGER.info("projects {}", projects);

		return projects;
	}

	@GET
	@Path("/projects/{projectId}")
	@Produces("application/json")
	public JsonObject getProjectById(@PathParam("projectId") String projectId) throws URISyntaxException {

		URI apiUri = new URI(projectURL);
		ProjectService projectService = RestClientBuilder.newBuilder().baseUri(apiUri).build(ProjectService.class);

		JsonObject project = projectService.getProjectById(projectId);

		LOGGER.info("project {}", project);

		return project;
	}

	@GET
	@Path("/projects/status/{theStatus}")
	@Produces("application/json")
	public JsonArray getProjectsByStatus(@PathParam("theStatus") String status) throws URISyntaxException {

		URI apiUri = new URI(projectURL);
		ProjectService projectService = RestClientBuilder.newBuilder().baseUri(apiUri).build(ProjectService.class);

		JsonArray project = projectService.getProjectsByStatus(status);

		LOGGER.info("project {}", project);

		return project;
	}

	@GET
	@Path("/freelancers")
	@Produces("application/json")
	public JsonObject getAllFreelancers() throws URISyntaxException {

		URI apiUri = new URI(freelancerURL);
		FreelancerService freelanceService = RestClientBuilder.newBuilder().baseUri(apiUri)
				.build(FreelancerService.class);

		JsonObject freelancers = freelanceService.getAllFreelancers();

		LOGGER.info("freelancers {}", freelancers);

		return freelancers;
	}

	@GET
	@Path("/freelancers/{id}")
	@Produces("application/json")
	public JsonObject getFreelancerById(@PathParam("id") String freelancerId) throws URISyntaxException {

		URI apiUri = new URI(freelancerURL);
		FreelancerService freelanceService = RestClientBuilder.newBuilder().baseUri(apiUri)
				.build(FreelancerService.class);

		JsonObject freelancer = freelanceService.getFreelancerById(freelancerId);

		LOGGER.info("freelancer {}", freelancer);

		return freelancer;
	}

}
