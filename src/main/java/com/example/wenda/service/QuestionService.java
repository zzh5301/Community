package com.example.wenda.service;

import com.example.wenda.dao.QuestionDAO;
import com.example.wenda.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by lenovo on 2017/5/25.
 */

@Service
public class QuestionService {

    @Autowired
    QuestionDAO questionDAO;


    public Question getById(int id){return questionDAO.getById(id);}


    public int addQuestion(Question question){

        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));

        return questionDAO.addQuestion(question)>0?question.getId():0;
    }

    public List<Question> getLatestQuestions(int userId,int offset,int limit){
        return questionDAO.selectLatestQuestions(userId,offset,limit);
    }

    public int updateCommentCount(int id,int count){return  questionDAO.updateCommentCount(id,count);}
}
