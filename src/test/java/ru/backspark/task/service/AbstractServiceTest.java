package ru.backspark.task.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.backspark.task.repository.SockRepository;

@SpringBootTest
@AutoConfigureMockMvc
abstract class AbstractServiceTest {

    @MockBean
    protected SockRepository sockRepository;

    @Autowired
    protected SockService sockService;
}
