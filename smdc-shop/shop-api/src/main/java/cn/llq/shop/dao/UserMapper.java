package cn.llq.shop.dao;

import cn.llq.shop.model.pojo.UserInfoPo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

public interface UserMapper extends Mapper<UserInfoPo>, InsertUseGeneratedKeysMapper<UserInfoPo> {
    @Select("<script>" +
            "SELECT u.id FROM user_info u\n" +
            "LEFT JOIN user_role ur ON ur.user_id=u.id\n" +
            "LEFT JOIN role_auth ra ON ra.role_id=ur.role_id\n" +
            "LEFT JOIN auth a ON a.id = ra.auth_id\n" +
            "WHERE a.auth_name=#{auth} AND u.id=#{userId}\n" +
            "group by u.id"+
            "</script>")
    Long checkAuth(@Param("auth")String auth, @Param("userId") Long userId);
}
