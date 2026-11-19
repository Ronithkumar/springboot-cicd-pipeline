package com.example.employee.service;

import com.example.employee.dto.EmployeeRequest;
import com.example.employee.dto.EmployeeResponse;
import com.example.employee.entity.Employee;
import com.example.employee.exception.DuplicateEmailException;
import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void createsEmployeeAndNormalizesEmail() {
        EmployeeRequest request = request("  JANE@EXAMPLE.COM  ");
        when(employeeRepository.existsByEmailIgnoreCase("jane@example.com")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponse response = employeeService.create(request);

        assertEquals("jane@example.com", response.email());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void rejectsDuplicateEmailWithoutSaving() {
        when(employeeRepository.existsByEmailIgnoreCase("jane@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> employeeService.create(request("jane@example.com")));

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updatesEmployeeWhenEmailBelongsToSameEmployee() {
        Employee employee = new Employee(
                "Jane", "Doe", "jane@example.com", "Engineering", new BigDecimal("90000")
        );
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailIgnoreCaseAndIdNot("jane@example.com", 1L)).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponse response = employeeService.update(1L, request("JANE@example.com"));

        assertEquals("jane@example.com", response.email());
        assertEquals("Engineering", response.department());
    }

    @Test
    void throwsNotFoundWhenEmployeeDoesNotExist() {
        when(employeeRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.findById(42L));
    }

    private EmployeeRequest request(String email) {
        return new EmployeeRequest("Jane", "Doe", email, "Engineering", new BigDecimal("90000"));
    }
}
