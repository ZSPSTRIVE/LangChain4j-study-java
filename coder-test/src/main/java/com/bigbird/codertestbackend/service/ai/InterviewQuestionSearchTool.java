package com.bigbird.codertestbackend.service.ai;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 面试题搜索工具
 * 从面试鸭网站搜索相关面试题
 */
@Component
@Slf4j
public class InterviewQuestionSearchTool {

    private static final String SEARCH_URL_TEMPLATE = "https://www.mianshiya.com/search/all?searchText=%s";
    
    /**
     * 搜索面试题
     * 
     * @param query 搜索关键词，应该是技术相关的关键词，比如"Java"、"Spring Boot"、"Vue"等
     * @return 搜索到的面试题列表，格式为：题目标题 - 题目链接
     */
    @Tool("根据技术关键词搜索相关的面试题，帮助用户获得针对性的面试题推荐")
    public String searchInterviewQuestions(String query) {
        try {
            log.info("开始搜索面试题，关键词：{}", query);
            
            // URL编码搜索关键词
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String searchUrl = String.format(SEARCH_URL_TEMPLATE, encodedQuery);
            
            // 使用Jsoup获取网页内容
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            // 查找题目列表容器
            Element questionTableView = doc.selectFirst(".question-table-view");
            if (questionTableView == null) {
                log.warn("未找到题目列表容器，关键词：{}", query);
                return "未找到相关面试题，建议尝试其他关键词。";
            }
            
            // 提取题目信息
            Elements questionItems = questionTableView.select(".ant-table-row");
            List<String> questions = new ArrayList<>();
            
            int count = 0;
            for (Element row : questionItems) {
                if (count >= 3) { // 限制返回数量
                    break;
                }
                
                try {
                    // 获取所有列
                    Elements cells = row.select(".ant-table-cell");
                    
                    // 确保列数足够（至少4列：空列、标题列、难度列、标签列）
                    if (cells.size() < 4) {
                        continue;
                    }
                    
                    // 第二列：题目标题和链接（索引为1）
                    Element titleCell = cells.get(1);
                    String title = titleCell.text().trim();
                    if (title.isEmpty()) {
                        continue;
                    }
                    
                    // 提取题目链接
                    Element linkElement = titleCell.selectFirst("a");
                    String link = "";
                    if (linkElement != null) {
                        String href = linkElement.attr("href");
                        if (!href.isEmpty()) {
                            link = href.startsWith("http") ? href : "https://www.mianshiya.com" + href;
                        }
                    }
                    
                    // 第三列：题目难度（索引为2）
                    String difficulty = cells.get(2).text().trim();
                    
                    // 第四列：题目标签（索引为3）
                    String tags = cells.get(3).text().trim();
                    
                    // 构建题目信息
                    StringBuilder questionInfo = new StringBuilder();
                    questionInfo.append(title);
                    
                    // 添加难度信息
                    if (!difficulty.isEmpty()) {
                        questionInfo.append(" [难度: ").append(difficulty).append("]");
                    }
                    
                    // 添加标签信息
                    if (!tags.isEmpty()) {
                        questionInfo.append(" [标签: ").append(tags).append("]");
                    }
                    
                    // 添加链接
                    if (!link.isEmpty()) {
                        questionInfo.append(" - ").append(link);
                    }
                    
                    questions.add(questionInfo.toString());
                    count++;
                    
                } catch (Exception e) {
                    log.warn("解析题目信息时出错：{}", e.getMessage());
                    continue;
                }
            }
            
            if (questions.isEmpty()) {
                log.warn("未解析到有效的面试题，关键词：{}", query);
                return "未找到相关面试题，建议尝试其他关键词。";
            }
            
            log.info("成功搜索到{}道面试题，关键词：{}", questions.size(), query);
            
            // 构建返回结果
            StringBuilder result = new StringBuilder();
            result.append("找到以下相关面试题：\n\n");
            for (int i = 0; i < questions.size(); i++) {
                result.append(i + 1).append(". ").append(questions.get(i)).append("\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            log.error("搜索面试题时发生错误，关键词：{}，错误：{}", query, e.getMessage());
            return "搜索面试题时发生错误，请稍后再试。";
        }
    }
}
