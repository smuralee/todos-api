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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smuralee.entity.Todo;
import com.smuralee.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
class TodoControllerTest {

    @Spy
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoRepository repository;

    @Spy
    private List<Todo> todoList;

    @BeforeEach
    void setUp() {

        todoList = Arrays.asList(
                new Todo(1L, "Get milk", true),
                new Todo(2L, "Deposit rent", false),
                new Todo(3L, "Buy vegetables", false),
                new Todo(4L, "Book a taxi", true)
        );

    }

    @Test
    void getAll() throws Exception {

        when(repository.findAll()).thenReturn(todoList);

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(
                        content()
                                .json(this.mapper.writeValueAsString(todoList))
                );

        // Verify the method is called just once
        verify(repository, times(1)).findAll();

    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4})
    void getById(final Long selectedId) throws Exception {

        Optional<Todo> todo = todoList.stream()
                .filter(item -> item.getId().equals(selectedId))
                .findFirst();

        when(repository.findById(Mockito.anyLong())).thenReturn(todo);

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/todos/".concat(String.valueOf(selectedId)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(
                        content()
                                .json(this.mapper.writeValueAsString(todo.orElse(null)))
                );

        // Verify the method is called just once
        verify(repository, times(1)).findById(Mockito.anyLong());

    }

    @Test
    void addTodo() throws Exception {

        // Payload for the REST endpoint
        Todo payload = new Todo();
        payload.setDescription("Buy a cycle");
        payload.setCompleted(false);

        // Response for the REST endpoint
        Todo response = new Todo();
        response.setId(1L);
        payload.setDescription("Buy a cycle");
        payload.setCompleted(false);

        when(repository.save(Mockito.any(Todo.class))).thenReturn(response);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload))
        )
                .andExpect(status().isOk())
                .andExpect(
                        content()
                                .json(this.mapper.writeValueAsString(response))
                );

        // Verify the method is called just once
        verify(repository, times(1)).save(Mockito.any(Todo.class));

    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4})
    void updateTodo(final Long selectedId) throws Exception {

        Optional<Todo> todo = todoList.stream()
                .filter(item -> item.getId().equals(selectedId))
                .findFirst();

        when(repository.findById(Mockito.anyLong())).thenReturn(todo);

        // Payload for the REST endpoint
        Todo payload = new Todo();
        payload.setDescription("Buy a cycle updated");
        payload.setCompleted(false);

        // Response for the REST endpoint
        Todo response = new Todo();
        response.setId(1L);
        payload.setDescription("Buy a cycle updated");
        payload.setCompleted(false);

        when(repository.save(Mockito.any(Todo.class))).thenReturn(response);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/todos/".concat(String.valueOf(selectedId)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload))
        )
                .andExpect(status().isOk())
                .andExpect(
                        content()
                                .json(this.mapper.writeValueAsString(response))
                );

        // Verify the method is called just once
        verify(repository, times(1)).save(Mockito.any(Todo.class));
        verify(repository, times(1)).findById(Mockito.anyLong());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4})
    void deleteById(final Long selectedId) throws Exception {

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/todos/".concat(String.valueOf(selectedId)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        // Verify the method is called just once
        verify(repository, times(1)).deleteById(Mockito.anyLong());

    }
}
