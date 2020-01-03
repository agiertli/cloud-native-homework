package org.redhat.freelance4j.freelancer.dao;

import org.redhat.freelance4j.freelancer.model.Freelancer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "freelancers", path = "freelancers")

public interface FreelancerRepository extends CrudRepository<Freelancer, Long> {

}
