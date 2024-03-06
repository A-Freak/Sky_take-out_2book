package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     *
     * @param employeeDTO
     * @author: zjy
     * @return: void
     **/
    void save(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     * @author: zjy
     * @return: PageResult
     **/
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用禁用员工账号
     *
     * @param status
     * @param id
     * @author: zjy
     * @return: void
     **/
    void startOrStop(Integer status, Long id);

    /**
     * 根据id查询员工信息
     *
     * @param id
     * @author: zjy
     * @return: Employee
     **/
    EmployeeDTO getById(Long id);

    /**
     * 编辑员工信息
     *
     * @param employeeDTO
     * @author: zjy
     * @return: void
     **/
    void update(EmployeeDTO employeeDTO);

    /**
     * 修改员工密码
     * @author: zjy
     * @param passwordEditDTO
     * @return: void
     **/
    void changepassword(PasswordEditDTO passwordEditDTO);
}
