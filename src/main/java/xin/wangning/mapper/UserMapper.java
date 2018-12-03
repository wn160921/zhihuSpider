package xin.wangning.mapper;

import xin.wangning.domain.User;

import java.util.List;

public interface UserMapper {
    public List<User> selectAll();
    public void insert(User user);
}
