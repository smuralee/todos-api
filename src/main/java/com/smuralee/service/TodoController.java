/**
 * Copyright 2020 Suraj Muraleedharan

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.smuralee.service;

import com.smuralee.entity.Todo;
import com.smuralee.errors.DataNotFoundException;
import com.smuralee.repository.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoRepository repository;

    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Todo> getAll() {
        log.info("Getting all the todos");
        return this.repository.findAll();
    }

    @GetMapping("/{id}")
    public Todo getById(final @PathVariable Long id) {
        log.info("Getting the todos by id");
        Optional<Todo> todo = this.repository.findById(id);
        return todo.orElseThrow(DataNotFoundException::new);
    }

    @PostMapping
    public Todo addTodo(final @RequestBody Todo todo) {
        log.info("Saving the new todo");
        return this.repository.save(todo);
    }

    @PutMapping("/{id}")
    public Todo updateTodo(final @RequestBody Todo todo, final @PathVariable Long id) {
        log.info("Fetching the todo by id");
        Optional<Todo> fetchedTodo = this.repository.findById(id);

        log.info("Updating the todo identified by the id");
        if (fetchedTodo.isPresent()) {
            todo.setId(id);
            return this.repository.save(todo);
        } else {
            throw new DataNotFoundException();
        }
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Long id) {
        log.info("Deleting the todo by id");
        this.repository.deleteById(id);
    }
}
