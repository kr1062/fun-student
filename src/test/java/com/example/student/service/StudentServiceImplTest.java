package com.example.student.service;

import com.example.student.dto.StudentRequest;
import com.example.student.dto.StudentResponse;
import com.example.student.exception.EntityNotFoundException;
import com.example.student.mapper.StudentMapper;
import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import com.example.student.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void save_ShouldReturnStudentResponse() {
        StudentRequest request = new StudentRequest();
        request.setName("Ivan");
        request.setSurname("Ivanov");

        Student student = new Student();
        student.setName("Ivan");

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName("Ivan");

        StudentResponse expectedResponse = new StudentResponse(1L, "Ivan", "Ivanov");

        when(studentMapper.requestToStudent(request)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(savedStudent);
        when(studentMapper.studentToResponse(savedStudent)).thenReturn(expectedResponse);

        StudentResponse actualResponse = studentService.save(request);

        assertNotNull(actualResponse);
        assertEquals(1L, actualResponse.getId());
        assertEquals("Ivan", actualResponse.getName());
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void findById_WhenNotFound_ShouldThrowException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            studentService.findById(99L);
        });

        assertTrue(exception.getMessage().contains("Student with ID 99 not found"));
    }

    @Test
    void deleteById_ShouldCallRepository() {
        studentService.deleteById(1L);
        verify(studentRepository, times(1)).deleteById(1L);
    }
}