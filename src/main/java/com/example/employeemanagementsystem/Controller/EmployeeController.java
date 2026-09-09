package com.example.employeemanagementsystem.Controller;

import com.example.employeemanagementsystem.API.APIResponse;
import com.example.employeemanagementsystem.Model.Employee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {
    ArrayList<Employee> employees = new ArrayList<>();

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.status(200).body(employees);
    }

    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestBody @Valid Employee employee, Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());

        if (employee.isOnLeave())
            return ResponseEntity.status(400).body(new APIResponse("onLeave must be false when created"));

        employees.add(employee);
        return ResponseEntity.status(200).body(new APIResponse("added successfully"));

    }

    @PutMapping("/put/{index}")
    public ResponseEntity<?> put(@PathVariable int index, @RequestBody @Valid Employee employee, Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.status(400).body(errors.getFieldError().getDefaultMessage());

        if (index < 0 || index >= employees.size()) {
            return ResponseEntity.status(400).body(new APIResponse("invalid index"));
        }
        employees.set(index, employee);
        return ResponseEntity.status(200).body(new APIResponse("updated successfully"));

    }

    @DeleteMapping("/delete/{index}")
    public ResponseEntity<?> delete(@PathVariable int index) {
        if (index < 0 || index >= employees.size()) {
            return ResponseEntity.status(400).body(new APIResponse("invalid index"));
        }
        employees.remove(index);
        return ResponseEntity.status(200).body(new APIResponse("deleted successfully"));

    }

    @GetMapping("/search-position-employees/{position}")
    public ResponseEntity<?> searchEmployees(@PathVariable String position){
        ArrayList<Employee> selectedEmployees = new ArrayList<>();

        for (Employee employee: employees){
            if (employee.getPosition().equals(position))
                selectedEmployees.add(employee);
        }

        return ResponseEntity.status(200).body(selectedEmployees);
    }

    @GetMapping("/get-employees-by-range/{minAge}/{maxAge}")
    public ResponseEntity<?> getEmployeesByRange(@PathVariable int minAge, @PathVariable int maxAge){
        if (minAge <26)
            return ResponseEntity.status(400).body(new APIResponse("invalid minimum age range"));
        ArrayList<Employee> selectedEmployees = new ArrayList<>();

        for (Employee employee: employees){
            if (employee.getAge() >= minAge && employee.getAge() < maxAge)
                selectedEmployees.add(employee);
        }

        return ResponseEntity.status(200).body(selectedEmployees);
    }

    @PutMapping("/apply-leave/{ID}")
    public ResponseEntity<?> annualLeaveApply(@PathVariable String ID){
       boolean exist = false;
       Employee employee = new Employee();
       int index = 0;
        for (Employee employee1: employees){
            if (employee1.getID().equals(ID))
            {
                employee = employee1;
                exist = true;
            }
            index++;
        }
        if (!exist)
            return ResponseEntity.status(400).body(new APIResponse("no such ID exist"));

        if (employee.isOnLeave())
            return ResponseEntity.status(400).body(new APIResponse("onLeave must be false"));
        if (employee.getAnnualLeave() < 1)
            return ResponseEntity.status(400).body(new APIResponse("no annual leave remaining"));

        index--;
        employee.setAnnualLeave(employee.getAnnualLeave() - 1);
        employees.set(index, employee);
        return ResponseEntity.status(200).body(new APIResponse("Leave approved"));

    }

    @GetMapping("get/no-leave-employees")
    public ResponseEntity<?> getEmpWithNoLeave(){
        ArrayList<Employee> selectedEmployees = new ArrayList<>();

        for (Employee employee: employees) {
            if (employee.getAnnualLeave() == 0)
                selectedEmployees.add(employee);
        }
        return ResponseEntity.status(200).body(selectedEmployees);
    }

    @PutMapping("/promote/{supervisorID}/{employeeID}")
    public ResponseEntity<?> promoteEmployee(@PathVariable String supervisorID,@PathVariable String employeeID){
        boolean employeeExist = false;
        boolean isSupervisor = false;
        boolean acceptedAge = false;
        boolean isOnLeave = false;
        int counter = 0;
        int employeeIndex = -1;
        Employee employee2 = new Employee();
        for (Employee employee: employees){
            if (employee.getID().equals(employeeID)){
                employee2 = employee;
                employeeIndex = counter;
                employeeExist = true;
                if (employee.getAge() >= 30)
                    acceptedAge = true;
                if(employee.isOnLeave())
                    isOnLeave = true;
            }
            if (employee.getID().equals(supervisorID)){
                if (employee.getPosition().equals("supervisor"))
                    isSupervisor = true;
            }
            counter++;
        }

        if (!employeeExist)
            return ResponseEntity.status(400).body(new APIResponse("Invalid employee ID"));

        if (!isSupervisor)
            return ResponseEntity.status(400).body(new APIResponse("Invalid supervisor position"));
        if (!acceptedAge)
            return ResponseEntity.status(400).body(new APIResponse("Invalid employee age, must be 30 or more"));

        if (isOnLeave)
            return ResponseEntity.status(400).body(new APIResponse("can not promote on leave employee"));

        employee2.setPosition("supervisor");
        employees.set(employeeIndex, employee2);

        return ResponseEntity.status(200).body(new APIResponse("promoted successfully"));


    }

}
