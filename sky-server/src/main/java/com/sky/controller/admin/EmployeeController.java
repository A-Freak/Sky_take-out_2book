package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        // 此处将 员工id 信息进行了混入令牌
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());

        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    // 当其值只有一个时并且为value，可以不用写参数
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     * @author: zjy
     * @return: Result
     **/
    @ApiOperation("新增员工")
    @PostMapping()
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        // 测试线程
        System.out.println("当前线程的id：" + Thread.currentThread().getId());

        log.info("新增员工：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     * @author: zjy
     * @return: Result<PageResult>
     **/
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询，参数为：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用员工账号
     *
     * @param status
     * @param id
     * @author: zjy
     * @return: Result
     **/
    // 此处并非查询语句，故不需要返回值泛型[没遵守REST风格]
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用员工账号")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用禁用员工账号：{},{}", status, id);
        employeeService.startOrStop(status, id);//后绪步骤定义
        return Result.success();
    }

    /**
     * 根据id查询员工信息
     *
     * @param id
     * @author: zjy
     * @return: Result<Employee>
     **/
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工信息")
    public Result<EmployeeDTO> startOrStop(@PathVariable Long id) {
        EmployeeDTO employeeDTO = employeeService.getById(id);
        return Result.success(employeeDTO);
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO
     * @author: zjy
     * @return: Result
     **/
    @PutMapping()
    @ApiOperation("编辑员工信息")
    public Result startOrStop(@RequestBody EmployeeDTO employeeDTO) {
        log.info("编辑的员工账号：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }


    /**
     * 修改员工密码
     * @author: zjy
     * @param passwordEditDTO
     * @return: Result
     **/
    @PutMapping("editPassword")
    @ApiOperation("修改员工密码")
    public Result startOrStop(@RequestBody PasswordEditDTO passwordEditDTO) {
        log.info("修改员工密码：{}", passwordEditDTO);
        employeeService.changepassword(passwordEditDTO);
        return Result.success();
    }


}
