package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 后期需要进行md5加密，然后再进行比对
        // IDEA太酷了 调用 spring 中已有的md5加密方法
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // Java有对包装类的Integer包装类的-128到127有进行缓存，发生误报
        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void save(EmployeeDTO employeeDTO) {
        // 测试线程
        // System.out.println("当前线程的id：" + Thread.currentThread().getId());

        // 前端传入DTO 本地存储数据库的是表的对应的Entry对象
        Employee employee = new Employee();
        // spring自带的对象拷贝工具类
        BeanUtils.copyProperties(employeeDTO, employee);

        // 没有的6个属性
        // 对常量类直接进行了修改，故不需要再次进行MD5加密
        employee.setPassword(PasswordConstant.DEFAULT_PASSWORD);
        employee.setStatus(StatusConstant.ENABLE);
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());

        // 创建人以及修改人 逆向解析用于荷载,新技术是线程传值
        // employee.setCreateUser(BaseContext.getCurrentId());
        // employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     * @author: zjy
     * @return: PageResult
     **/
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 固定格式
        // 1.设置分页参数
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        // 2.执行分页查询【3.使用其固定类型】
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 启用禁用员工账号
     *
     * @param status
     * @param id
     * @author: zjy
     * @return: void
     **/
    @Override
    public void startOrStop(Integer status, Long id) {
        //使用了 lombok 的新注解进行链式构建
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();

        // 依旧使用Entry对象进行传递，扩大SQL语句的作用
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工信息
     *
     * @param id
     * @author: zjy
     * @return: Employee
     **/
    @Override
    public EmployeeDTO getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        // 本人对其前端传输改为DTO格式避免一些泄露【缺点：一些信息无法修改】
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employee, employeeDTO);
        return employeeDTO;
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO
     * @author: zjy
     * @return: void
     **/
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        // 调用之前的广泛的SQL
        employeeMapper.update(employee);
    }

    /**
     * 修改员工密码
     *
     * @param passwordEditDTO
     * @author: zjy
     * @return: void
     **/
    @Override
    @Transactional
    public void changepassword(PasswordEditDTO passwordEditDTO) {
        // 对传入旧密码进行验证[也许还有旧密码，不等于新密码?
        // 此处 id无法进行获取，因为只有当其为新增和修改时才大概率需要用到获取员工id
        Long empId = BaseContext.getCurrentId();
        Employee employee = employeeMapper.getById(empId);
        // 数据库中存在密码是已加密过后的,进行比对
        if (employee.getPassword().equals(DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes()))) {
            // 同样要存储加密后的密码
            employee.setPassword(DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes()));
            employeeMapper.update(employee);
        } else {
            // 抛出异常
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
    }

}
