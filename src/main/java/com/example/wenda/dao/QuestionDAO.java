package com.example.wenda.dao;

import com.example.wenda.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by lenovo on 2017/5/25.
 */
public interface QuestionDAO {
    String TABLE_NAME="question";
    String INSERT_FIELDS="title,content,created_date,user_id,comment_count";
    String SELECT_FIELDS="id"+INSERT_FIELDS;


    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values (#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);


    List<Question> selectLatestQuestions(@Param("userId") int userId,@Param("offset") int offset,@Param("limit") int limit);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Question getById(int id);

    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);
}
