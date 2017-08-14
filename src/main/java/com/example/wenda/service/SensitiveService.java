package com.example.wenda.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2017/5/26.
 */
public class SensitiveService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    private static final String DEFAULT_REPLACEMWNT="***";

    private static  TrieNode rootNode=new TrieNode();

    private static class TrieNode{
        private boolean end=false;

        private Map<Character,TrieNode> subNodes=new HashMap<>();

        void addSubNode(Character key,TrieNode node){
            subNodes.put(key,node);
        }

        TrieNode getSubNode(Character key){return subNodes.get(key);}

        boolean isKeywordEnd(){return  end;}

        void setKeywordEnd(boolean end){
            this.end=end;
        }
    }

    private boolean isSymbol(char c){
        int ic=(int)c;

        return !CharUtils.isAsciiAlphanumeric(c)&&(ic<0x2E80||ic>0x9FFF);
    }

    public String filter(String text){

        if(StringUtils.isBlank(text))
            return text;

        String replacement=DEFAULT_REPLACEMWNT;
        StringBuilder sb=new StringBuilder();

        TrieNode temNode=rootNode;

        int begin=0;
        int position=0;
        while (position<text.length()){
            char c=text.charAt(position);
            if(isSymbol(c)){
                if(temNode==rootNode){
                    sb.append(c);
                    ++begin;
                }
                position++;
                continue;
            }
            temNode=temNode.getSubNode(c);
            if(temNode==null){
                sb.append(text.charAt(begin));
                position=begin+1;
                begin=position;
                temNode=rootNode;
            }else if(temNode.isKeywordEnd()){

                sb.append(replacement);
                position=position+1;
                begin=position;
                temNode=rootNode;

            }else {
                ++position;
            }

        }
        sb.append(text.substring(begin));
        return sb.toString();
    }

    private void addWord(String lineTxt){

        TrieNode tempNode=rootNode;

        for (int i=0;i<lineTxt.length();++i){
            Character c=lineTxt.charAt(i);
            if(isSymbol(c)){
                continue;
            }

            TrieNode node=tempNode.getSubNode(c);
            if(node==null){
                node=new TrieNode();
                tempNode.addSubNode(c,node);
            }
            tempNode=node;
            if(i==lineTxt.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }

    }


    @Override
    public void afterPropertiesSet() throws Exception {

        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }

    }
}
