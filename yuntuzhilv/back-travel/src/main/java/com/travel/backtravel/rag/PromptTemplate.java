package com.travel.backtravel.rag;

import com.travel.backtravel.dto.AiPlanDTO;
import org.springframework.stereotype.Component;

@Component
public class PromptTemplate {

    public String generateItineraryPrompt(AiPlanDTO dto) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的旅游规划师，请根据以下用户需求生成一份详细的多日旅游行程：\n\n");
        prompt.append("【基本信息】\n");
        prompt.append("- 出发地：").append(dto.getDepartureCity() != null ? dto.getDepartureCity() : "未指定").append("\n");
        prompt.append("- 目的地：").append(dto.getDestinationCity()).append("\n");
        prompt.append("- 行程天数：").append(dto.getDays()).append("天\n");
        prompt.append("- 出发日期：").append(dto.getStartDate()).append("\n");
        prompt.append("- 结束日期：").append(dto.getEndDate()).append("\n");
        
        if (dto.getBudget() != null) {
            prompt.append("- 预算：").append(dto.getBudget()).append("元\n");
        }
        
        if (dto.getInterests() != null && !dto.getInterests().isEmpty()) {
            prompt.append("- 兴趣偏好：").append(dto.getInterests()).append("\n");
        }
        
        if (dto.getRequirements() != null && !dto.getRequirements().isEmpty()) {
            prompt.append("- 特殊要求：").append(dto.getRequirements()).append("\n");
        }
        
        prompt.append("\n【行程规划要求】\n");
        prompt.append("1. 每天安排2-3个主要景点，合理分配游玩时间\n");
        prompt.append("2. 推荐1-2家当地特色酒店\n");
        prompt.append("3. 规划往返交通方式\n");
        prompt.append("4. 考虑景点之间的距离，避免过度奔波\n");
        prompt.append("5. 预算控制在用户指定范围内\n");
        prompt.append("6. 结合用户兴趣偏好推荐合适的景点\n");
        
        prompt.append("\n【输出格式】\n");
        prompt.append("请以JSON格式输出，包含以下字段：\n");
        prompt.append("{\n");
        prompt.append("  \"title\": \"行程标题\",\n");
        prompt.append("  \"days\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"dayNumber\": 1,\n");
        prompt.append("      \"items\": [\n");
        prompt.append("        {\n");
        prompt.append("          \"itemType\": \"ATTRACTIONS/TRANSPORT/HOTEL\",\n");
        prompt.append("          \"itemName\": \"名称\",\n");
        prompt.append("          \"itemDesc\": \"描述\",\n");
        prompt.append("          \"itemPrice\": 价格,\n");
        prompt.append("          \"startTime\": \"开始时间\",\n");
        prompt.append("          \"endTime\": \"结束时间\"\n");
        prompt.append("        }\n");
        prompt.append("      ]\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"totalCost\": 总费用\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }

    public String modifyItineraryPrompt(Long itineraryId, String currentItinerary, String modifications) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的旅游规划师，请根据用户的修改意见调整以下行程：\n\n");
        prompt.append("【当前行程】\n");
        prompt.append(currentItinerary).append("\n\n");
        prompt.append("【修改要求】\n");
        prompt.append(modifications).append("\n\n");
        prompt.append("【调整原则】\n");
        prompt.append("1. 尽量保留原有行程结构\n");
        prompt.append("2. 根据修改意见合理调整\n");
        prompt.append("3. 保持时间安排的合理性\n");
        prompt.append("4. 重新计算总费用\n");
        
        prompt.append("\n【输出格式】\n");
        prompt.append("请以JSON格式输出，格式与生成行程相同：\n");
        prompt.append("{\n");
        prompt.append("  \"title\": \"行程标题\",\n");
        prompt.append("  \"days\": [...],\n");
        prompt.append("  \"totalCost\": 总费用\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }
}
