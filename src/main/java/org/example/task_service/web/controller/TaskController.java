package org.example.task_service.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.task_service.dto.ErrorResponse;
import org.example.task_service.dto.task.*;
import org.example.task_service.entity.Status;
import org.example.task_service.service.CommentService;
import org.example.task_service.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Service")
@SecurityRequirement(name = "bearerAuth")
public class TaskController
{
    private final TaskService taskService;
    private final CommentService commentService;

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Получение списка задач",
            description = """
                    Метод возвращает список задач с возможностью фильтрации и постраничной навигации
                    - **ROLE_ADMIN**: Может видеть все созданные задачи
                    - **ROLE_USER**: Может видеть только задачи, где он является исполнителем
                    """
    )
    public List<TaskResponse> taskList(
            @Parameter(name = "authorId", description = "Id автора задачи", example = "1", in = ParameterIn.QUERY)
            @RequestParam(required = false) Long authorId,
            @Parameter(name = "responsibleId", description = "Id ответственного за задачи", example = "1", in = ParameterIn.QUERY)
            @RequestParam(required = false) Long responsibleId,
            @Parameter(name = "pageSize", description = "Количество записей в ответе", example = "5", in = ParameterIn.QUERY)
            @RequestParam(required = false, defaultValue = "5") Integer pageSize,
            @Parameter(name = "pageNumber", description = "Номер страницы, отсчет начинается с нуля", example = "0", in = ParameterIn.QUERY)
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber
    ) {
        return taskService.tasksList(new TaskRequest(authorId, responsibleId, pageSize, pageNumber));
    }

    @PostMapping("/add-comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Попытка добавить комментарий к несуществующей задаче",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Добавление комментария к задаче",
            description = """
                    Метод добавляет новый комментарий к задаче
                    - **ROLE_ADMIN**: Может добавлять комментарии ко всем задачам
                    - **ROLE_USER**: Может добавлять комментарии только к задачам, где он является исполнителем
                    """)
    public CommentResponse addComment(@RequestBody @Valid UpsertCommentRequest request) {
        return commentService.addComment(request);
    }

    @GetMapping("/{taskId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Получение задачи по id",
            description = """
                    Метод возвращает задач по id
                    - **ROLE_ADMIN**: Может видеть все созданные задачи
                    - **ROLE_USER**: Может видеть только задачи, где он является исполнителем
                    """)
    public TaskResponse findTask(@PathVariable Long taskId) {
        return taskService.findTask(taskId);
    }

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @Operation(summary = "Создание новой задачи",
            description = "Метод добавляет новую задачу (требуется роль **ROLE_ADMIN**)")
    public TaskResponse createTask(
            @RequestBody @Valid UpsertTaskRequest request,
            HttpServletResponse response
    ) {
        TaskResponse task = taskService.createTask(request);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return task;
    }

    @PatchMapping("/{taskId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Попытка изменения несуществующей задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Изменение задачи",
            description = "Метод изменяет задачу (требуется роль **ROLE_ADMIN**)")
    public TaskResponse editTask(@PathVariable Long taskId, @RequestBody UpsertTaskRequest request) {
        return taskService.editTask(taskId, request);
    }

    @DeleteMapping("/{taskId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Попытка удаления несуществующей задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Удаление задачи по id",
            description = "Метод удаляет задачу по id (требуется роль **ROLE_ADMIN**)")
    public void deleteTask(@PathVariable Long taskId, HttpServletResponse response) {
        taskService.deleteTask(taskId);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @PatchMapping("/{taskId}/change-status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Попытка изменения несуществующей задачи",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Смена статуса задачи",
            description = """
                    Метод изменяет статус задачи по taskId
                    - **ROLE_ADMIN**: Может изменять статус любой задачи
                    - **ROLE_USER**: Может изменять статус в задачах, где он является исполнителем
                    """)
    public TaskResponse changeStatus(@PathVariable Long taskId, @RequestParam Status status) {
        return taskService.changeStatus(taskId, status);
    }
}
