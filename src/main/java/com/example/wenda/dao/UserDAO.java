package com.example.wenda.dao;

import com.example.wenda.model.User;
import org.apache.ibatis.annotations.*;

/**
 * Created by lenovo on 2017/5/25.
 */

@Mapper
public interface UserDAO {
    String TABLE_NAME="user";
    String INSERT_FIELDS="name,password,salt,head_url";
    String SELECT_FIELDS="id,name,password,salt,head_url";


    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values (#{name},#{password},#{salt},#{headUrl}))"})
    int addUser(User user);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where id=#{id}"})
    User selectById(int id);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where name=#{name}"})
    User selectByName(String name);

    @Update({"update",TABLE_NAME,"set password=#{password} where where id=#{id}"})
    void updatePassword(User user);

    @Delete({"delete from",TABLE_NAME,"where id=#{id}"})
    void deleteById(int id);

}
