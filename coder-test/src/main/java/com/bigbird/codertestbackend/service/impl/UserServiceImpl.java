package com.bigbird.codertestbackend.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bigbird.codertestbackend.mapper.UserMapper;
import com.bigbird.codertestbackend.model.dto.user.UserLoginRequest;
import com.bigbird.codertestbackend.model.dto.user.UserRegisterRequest;
import com.bigbird.codertestbackend.model.entity.User;
import com.bigbird.codertestbackend.model.enums.UserRoleEnum;
import com.bigbird.codertestbackend.model.vo.UserRankingVO;
import com.bigbird.codertestbackend.model.vo.UserVO;
import com.bigbird.codertestbackend.service.UserService;
import com.bigbird.codertestbackend.utils.AvatarUtils;

import java.util.List;
import java.util.stream.IntStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "bigbird";

    /**
     * 用户登录态键
     */
    public static final String USER_LOGIN_STATE = "user_login";

    @Override
    public String userRegister(UserRegisterRequest userRegisterRequest) {
        String username = userRegisterRequest.getUsername();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String nickname = userRegisterRequest.getNickname();

        // 校验
        if (StrUtil.hasBlank(username, password, checkPassword)) {
            throw new RuntimeException("参数为空");
        }
        if (username.length() < 4) {
            throw new RuntimeException("用户名过短");
        }
        if (password.length() < 8 || checkPassword.length() < 8) {
            throw new RuntimeException("用户密码过短");
        }
        if (!password.equals(checkPassword)) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查是否已存在该用户名
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 加密密码
        String encryptPassword = DigestUtil.md5Hex(SALT + password);

        // 插入数据
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptPassword);
        user.setNickname(StrUtil.isBlank(nickname) ? username : nickname);
        user.setUserRole(UserRoleEnum.USER.getValue()); // 默认角色为普通用户
        user.setSalary(10000); // 默认薪资10000

        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new RuntimeException("注册失败，数据库错误");
        }
        
        // 注册成功后设置默认头像（基于用户ID生成固定头像）
        user.setAvatar(AvatarUtils.getDefaultAvatarByUserId(user.getId()));
        this.updateById(user);
        
        return user.getId();
    }

    @Override
    public UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String username = userLoginRequest.getUsername();
        String password = userLoginRequest.getPassword();

        // 校验
        if (StrUtil.hasBlank(username, password)) {
            throw new RuntimeException("参数为空");
        }
        if (username.length() < 4) {
            throw new RuntimeException("用户名错误");
        }
        if (password.length() < 8) {
            throw new RuntimeException("密码错误");
        }

        // 加密
        String encryptPassword = DigestUtil.md5Hex(SALT + password);

        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);

        // 用户不存在
        if (user == null) {
            log.info("user login failed, username cannot match password");
            throw new RuntimeException("用户不存在或密码错误");
        }

        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new RuntimeException("未登录");
        }

        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        String userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new RuntimeException("未登录");
        }
        return currentUser;
    }

    @Override
    public UserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        // 如果用户没有头像，设置默认头像
        if (StrUtil.isBlank(userVO.getAvatar())) {
            userVO.setAvatar(AvatarUtils.getDefaultAvatarByUserId(user.getId()));
        }
        
        return userVO;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new RuntimeException("未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public boolean updateUserSalary(String userId, int salaryChange) {
        if (StrUtil.isBlank(userId)) {
            return false;
        }

        User user = this.getById(userId);
        if (user == null) {
            return false;
        }

        int newSalary = user.getSalary() + salaryChange;
        // 薪资不能为负数
        if (newSalary < 0) {
            newSalary = 0;
        }

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId);
        updateWrapper.set("salary", newSalary);

        return this.update(updateWrapper);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    @Override
    public void checkAdminAuth(HttpServletRequest request) {
        User user = getLoginUser(request);
        if (!isAdmin(user)) {
            throw new RuntimeException("无权限访问，仅限管理员");
        }
    }

    @Override
    public List<UserRankingVO> getSalaryRanking(int limit) {
        // 查询薪资排行榜，按薪资降序排列
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "nickname", "avatar", "salary")
                   .orderByDesc("salary", "createTime")
                   .last("LIMIT " + limit);
        
        List<User> users = this.list(queryWrapper);
        
        // 转换为排行榜VO并设置排名
        return IntStream.range(0, users.size())
                .mapToObj(i -> {
                    User user = users.get(i);
                    UserRankingVO rankingVO = new UserRankingVO();
                    BeanUtils.copyProperties(user, rankingVO);
                    rankingVO.setRank(i + 1);
                    
                    // 如果用户没有头像，设置默认头像
                    if (StrUtil.isBlank(rankingVO.getAvatar())) {
                        rankingVO.setAvatar(AvatarUtils.getDefaultAvatarByUserId(user.getId()));
                    }
                    
                    return rankingVO;
                })
                .toList();
    }
}

