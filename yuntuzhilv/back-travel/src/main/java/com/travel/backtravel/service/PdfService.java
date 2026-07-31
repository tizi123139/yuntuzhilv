package com.travel.backtravel.service;

import com.itextpdf.text.pdf.BaseFont;
import com.travel.backtravel.vo.ItineraryVO;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PDF 生成服务（Freemarker + Flying Saucer）
 */
@Slf4j
@Service
public class PdfService {

    private final Configuration freemarkerConfig;

    public PdfService() {
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates"
        );
        freemarkerConfig.setDefaultEncoding("UTF-8");
    }

    /**
     * 生成行程 PDF
     *
     * @param itinerary 行程数据
     * @return PDF 字节数组
     */
    public byte[] generateItineraryPdf(ItineraryVO itinerary) {
        try {
            // 准备模板数据
            Map<String, Object> dataModel = new HashMap<>();
            
            // 构建展示用的行程数据（处理日期格式化）
            Map<String, Object> displayItinerary = new HashMap<>();
            displayItinerary.put("title", itinerary.getTitle());
            displayItinerary.put("destination", itinerary.getDestination());
            displayItinerary.put("startCity", itinerary.getStartCity());
            displayItinerary.put("days", itinerary.getDays());
            displayItinerary.put("startDate", itinerary.getStartDate() != null ? itinerary.getStartDate().toString() : "");
            displayItinerary.put("endDate", itinerary.getEndDate() != null ? itinerary.getEndDate().toString() : "");
            displayItinerary.put("totalBudget", itinerary.getTotalBudget() != null ? itinerary.getTotalBudget() : 0);
            displayItinerary.put("totalCost", itinerary.getTotalCost() != null ? itinerary.getTotalCost() : 0);
            displayItinerary.put("people", itinerary.getPeople() != null ? itinerary.getPeople() : 1);
            displayItinerary.put("interests", itinerary.getInterests());
            
            // 转换 dayPlans
            if (itinerary.getDayPlans() != null) {
                LocalDate startDate = itinerary.getStartDate();
                List<Map<String, Object>> dayPlans = new java.util.ArrayList<>();
                for (var dayPlan : itinerary.getDayPlans()) {
                    Map<String, Object> dayMap = new HashMap<>();
                    dayMap.put("dayNumber", dayPlan.getDayNumber());

                    // 计算当天日期 = 出发日期 + (第N天 - 1)
                    if (startDate != null) {
                        LocalDate dayDate = startDate.plusDays(dayPlan.getDayNumber() - 1);
                        dayMap.put("dayDate", dayDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    } else {
                        dayMap.put("dayDate", "");
                    }
                    
                    List<Map<String, Object>> items = new java.util.ArrayList<>();
                    if (dayPlan.getItems() != null) {
                        for (var item : dayPlan.getItems()) {
                            Map<String, Object> itemMap = new HashMap<>();
                            itemMap.put("startTime", item.getStartTime());
                            itemMap.put("endTime", item.getEndTime());
                            itemMap.put("itemName", item.getItemName());
                            itemMap.put("itemDesc", item.getItemDesc());
                            itemMap.put("itemPrice", item.getItemPrice() != null ? item.getItemPrice() : 0);
                            items.add(itemMap);
                        }
                    }
                    dayMap.put("items", items);
                    dayPlans.add(dayMap);
                }
                displayItinerary.put("dayPlans", dayPlans);
            }
            
            dataModel.put("itinerary", displayItinerary);
            dataModel.put("currentTime", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // 渲染模板
            Template template = freemarkerConfig.getTemplate("itinerary.ftl");
            StringWriter stringWriter = new StringWriter();
            template.process(dataModel, stringWriter);
            String html = stringWriter.toString();

            // 生成 PDF
            return htmlToPdf(html);

        } catch (Exception e) {
            log.error("PDF 生成失败", e);
            throw new RuntimeException("PDF 生成失败：" + e.getMessage(), e);
        }
    }

    /**
     * HTML 转 PDF
     */
    private byte[] htmlToPdf(String html) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();

        // 配置中文字体（支持中文显示）
        ITextFontResolver fontResolver = renderer.getFontResolver();

        // 优先使用 TTF 文件（Flying Saucer 对 TTC 支持差），字体族名必须与 CSS font-family 匹配
        // simhei.ttf → family name "SimHei"
        // STSONG.TTF  → family name "STSong"
        String[][] fontCandidates = {
                {"C:/Windows/Fonts/simhei.ttf", "SimHei"},
                {"C:/Windows/Fonts/STSONG.TTF", "STSong"},
                {"C:/Windows/Fonts/Deng.ttf", "DengXian"},
                {"C:/Windows/Fonts/simsun.ttc", "SimSun"},
                {"C:/Windows/Fonts/msyh.ttc", "Microsoft YaHei"},
        };

        String loadedFamilyName = null;
        for (String[] candidate : fontCandidates) {
            String fontPath = candidate[0];
            String familyName = candidate[1];
            File fontFile = new File(fontPath);
            if (fontFile.exists()) {
                try {
                    fontResolver.addFont(fontPath, "Identity-H", BaseFont.EMBEDDED);
                    loadedFamilyName = familyName;
                    log.info("加载中文字体成功：{} (family={})", fontPath, familyName);
                    break;
                } catch (Exception e) {
                    log.warn("加载字体 {} 失败：{}", fontPath, e.getMessage());
                }
            }
        }

        if (loadedFamilyName == null) {
            log.warn("未找到中文字体，可能导致中文显示异常");
        } else {
            // 将 HTML 中的 font-family 替换为已加载的字体族名，确保精确匹配
            html = html.replaceFirst(
                    "font-family:\\s*\"[^\"]+\"",
                    "font-family: \"" + loadedFamilyName + "\""
            );
            log.info("PDF 使用字体族名：{}", loadedFamilyName);
        }

        // 设置 PDF 渲染参数
        renderer.setDocumentFromString(html);

        // 渲染并生成 PDF
        renderer.layout();
        renderer.createPDF(baos);

        return baos.toByteArray();
    }

    /**
     * 生成行程 PDF 并写入输出流
     *
     * @param itinerary 行程数据
     * @param response  HttpServletResponse
     */
    public void generateItineraryPdf(ItineraryVO itinerary, jakarta.servlet.http.HttpServletResponse response) throws IOException {
        byte[] pdfBytes = generateItineraryPdf(itinerary);

        // 设置响应头
        String fileName = java.net.URLEncoder.encode(
                itinerary.getTitle() + "_行程单.pdf",
                StandardCharsets.UTF_8
        ).replaceAll("\\+", "%20");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        response.setContentLength(pdfBytes.length);

        // 写入响应
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
}
