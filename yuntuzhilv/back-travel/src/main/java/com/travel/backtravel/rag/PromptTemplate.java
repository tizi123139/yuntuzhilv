package com.travel.backtravel.rag;

import com.travel.backtravel.dto.AiPlanDTO;
import org.springframework.stereotype.Component;

@Component
public class PromptTemplate {

    /**
     * 生成行程 Prompt（不拼接任何景点/酒店/交通资源，极大缩短上下文）
     */
    public String generateItineraryPrompt(AiPlanDTO dto) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("你是旅游规划师。根据用户需求生成行程，只输出纯JSON。\n\n");

        // ---- 用户需求（一行紧凑表达）----
        prompt.append("【需求】");
        prompt.append(dto.getDepartureCity() != null ? dto.getDepartureCity() : "出发地未定").append("→")
              .append(dto.getDestinationCity()).append("，")
              .append(dto.getDays()).append("天");
        if (dto.getPeople() != null) prompt.append("，").append(dto.getPeople()).append("人");
        if (dto.getBudget() != null) prompt.append("，预算").append(dto.getBudget()).append("元");
        if (dto.getInterests() != null && !dto.getInterests().isEmpty())
            prompt.append("，偏好").append(String.join("、", dto.getInterests()));
        if (dto.getAccommodationNeed() != null && !dto.getAccommodationNeed().isEmpty())
            prompt.append("，住宿").append(dto.getAccommodationNeed());
        if (dto.getTravelRhythm() != null && !dto.getTravelRhythm().isEmpty())
            prompt.append("，节奏").append(dto.getTravelRhythm());
        if (dto.getRequirements() != null && !dto.getRequirements().isEmpty())
            prompt.append("，要求").append(dto.getRequirements());
        prompt.append("\n\n");

        // ---- 输出规则 ----
        prompt.append("【输出规则】\n");
        prompt.append("1. 只输出纯净JSON，无注释、无markdown、无解释文字\n");
        prompt.append("2. totalCost必须是纯数字，禁止任何+、*、/或数学公式\n");
        prompt.append("3. days数组长度=").append(dto.getDays()).append("，dayNumber从1到").append(dto.getDays()).append("\n");
        prompt.append("4. 每天必须包含完整安排：交通+早餐+景点+午餐+景点/活动+晚餐+住宿\n");
        prompt.append("5. itemName必须使用以下关键词之一作为开头：交通、早餐、午餐、晚餐、景点、住宿\n");
        prompt.append("6. 每个item只包含: itemName,itemDesc,itemPrice,startTime,endTime\n");
        prompt.append("7. 禁止输出resourceId或itemType字段\n");
        prompt.append("8. itemDesc一句话简要说明即可，不超过20字\n");
        prompt.append("9. 住宿类item的endTime写次日08:00或次日12:00\n");
        prompt.append("10. 总费用不超过预算\n\n");

        // ---- JSON 结构示例 ----
        prompt.append("【示例】\n");
        prompt.append("{\"title\":\"杭州3日游\",\"days\":[");
        prompt.append("{\"dayNumber\":1,\"items\":[");
        prompt.append("{\"itemName\":\"交通:杭州东站接送\",\"itemDesc\":\"高铁到达\",\"itemPrice\":0,\"startTime\":\"07:30\",\"endTime\":\"09:00\"},");
        prompt.append("{\"itemName\":\"早餐:知味观\",\"itemDesc\":\"品尝杭州传统早点\",\"itemPrice\":30,\"startTime\":\"09:30\",\"endTime\":\"10:30\"},");
        prompt.append("{\"itemName\":\"景点:西湖\",\"itemDesc\":\"漫步苏堤白堤\",\"itemPrice\":0,\"startTime\":\"10:30\",\"endTime\":\"12:30\"},");
        prompt.append("{\"itemName\":\"午餐:楼外楼\",\"itemDesc\":\"品尝东坡肉\",\"itemPrice\":200,\"startTime\":\"12:30\",\"endTime\":\"14:00\"},");
        prompt.append("{\"itemName\":\"景点:灵隐寺\",\"itemDesc\":\"千年古刹\",\"itemPrice\":45,\"startTime\":\"14:30\",\"endTime\":\"17:00\"},");
        prompt.append("{\"itemName\":\"晚餐:外婆家\",\"itemDesc\":\"杭帮菜\",\"itemPrice\":150,\"startTime\":\"18:00\",\"endTime\":\"19:30\"},");
        prompt.append("{\"itemName\":\"住宿:西湖国宾馆\",\"itemDesc\":\"湖景房\",\"itemPrice\":800,\"startTime\":\"20:00\",\"endTime\":\"次日12:00\"}");
        prompt.append("]},");
        prompt.append("{\"dayNumber\":2,\"items\":[");
        prompt.append("{\"itemName\":\"早餐:酒店自助\",\"itemDesc\":\"宾馆早餐\",\"itemPrice\":0,\"startTime\":\"07:30\",\"endTime\":\"08:30\"},");
        prompt.append("{\"itemName\":\"景点:千岛湖\",\"itemDesc\":\"坐船游湖\",\"itemPrice\":150,\"startTime\":\"09:00\",\"endTime\":\"12:00\"},");
        prompt.append("{\"itemName\":\"午餐:鱼味馆\",\"itemDesc\":\"千岛湖鱼头\",\"itemPrice\":180,\"startTime\":\"12:30\",\"endTime\":\"14:00\"},");
        prompt.append("{\"itemName\":\"景点:河坊街\",\"itemDesc\":\"南宋御街\",\"itemPrice\":0,\"startTime\":\"14:30\",\"endTime\":\"17:00\"},");
        prompt.append("{\"itemName\":\"晚餐:小吃街\",\"itemDesc\":\"特色小吃\",\"itemPrice\":80,\"startTime\":\"18:00\",\"endTime\":\"19:30\"},");
        prompt.append("{\"itemName\":\"住宿:如家酒店\",\"itemDesc\":\"经济型酒店\",\"itemPrice\":300,\"startTime\":\"20:00\",\"endTime\":\"次日12:00\"}");
        prompt.append("]}],\"totalCost\":1935}");

        return prompt.toString();
    }

    public String modifyItineraryPrompt(String currentItinerary, String modifications) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是旅游规划师。根据用户修改意见调整行程，只输出纯JSON。\n\n");
        prompt.append("【当前行程】\n").append(currentItinerary).append("\n\n");
        prompt.append("【修改要求】\n").append(modifications).append("\n\n");
        prompt.append("【输出规则】\n");
        prompt.append("1. 只输出纯净JSON，无注释、无markdown、无解释文字\n");
        prompt.append("2. totalCost必须是纯数字，禁止任何+、*、/或数学公式\n");
        prompt.append("3. 每个item只包含: itemName,itemDesc,itemPrice,startTime,endTime\n");
        prompt.append("4. 禁止输出resourceId或itemType字段\n");
        prompt.append("5. JSON结构: {\"title\":\"\",\"days\":[{\"dayNumber\":1,\"items\":[{\"itemName\":\"\",\"itemDesc\":\"\",\"itemPrice\":0,\"startTime\":\"\",\"endTime\":\"\"}]}],\"totalCost\":0}\n");
        return prompt.toString();
    }
}
