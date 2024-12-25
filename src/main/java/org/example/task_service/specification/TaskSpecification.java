package org.example.task_service.specification;

import org.example.task_service.dto.task.TaskRequest;
import org.example.task_service.entity.Task;
import org.springframework.data.jpa.domain.Specification;

public interface TaskSpecification
{
    static Specification<Task> withRequest(TaskRequest request) {
        return Specification
                .where(byAuthor(request.getAuthorId()))
                .and(byResponsible(request.getResponsibleId()));
    }

    static Specification<Task> byAuthor(Long authorId) {
        return (root, query, criteriaBuilder) -> {
            if (authorId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("author").get("id"), authorId);
        };
    }

    static Specification<Task> byResponsible(Long responsibleId) {
        return (root, query, criteriaBuilder) -> {
            if (responsibleId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("responsible").get("id"), responsibleId);
        };
    }
}
