package com.example.student.repository;

import com.example.student.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void saveAndFindAll_ShouldWork() {
        Student s1 = new Student();
        s1.setName("Petr");
        s1.setSurname("Petrov");

        studentRepository.save(s1);

        List<Student> students = studentRepository.findAll();

        assertEquals(1, students.size());
        assertEquals("Petr", students.get(0).getName());
    }
}