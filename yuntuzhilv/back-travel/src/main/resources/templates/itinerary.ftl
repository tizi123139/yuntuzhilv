<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8" />
    <title>${itinerary.title}_行程单</title>
    <style>
        @page {
            size: A4;
            margin: 2cm;
        }
        body {
            font-family: "SimHei", "STSong", "Microsoft YaHei", Arial, sans-serif;
            font-size: 12px;
            color: #333;
        }
        .header {
            text-align: center;
            border-bottom: 3px solid #667eea;
            padding-bottom: 15px;
            margin-bottom: 20px;
        }
        .header h1 {
            color: #333;
            margin: 0 0 10px 0;
            font-size: 28px;
        }
        .header .subtitle {
            color: #667eea;
            font-size: 14px;
        }
        .info-section {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 25px;
        }
        .info-section h2 {
            color: #667eea;
            margin: 0 0 15px 0;
            font-size: 18px;
        }
        .info-grid {
            display: table;
            width: 100%;
        }
        .info-row {
            display: table-row;
        }
        .info-label {
            display: table-cell;
            width: 100px;
            color: #666;
            font-weight: bold;
            padding: 5px 10px;
        }
        .info-value {
            display: table-cell;
            color: #333;
            padding: 5px 10px;
        }
        .day-section {
            margin-bottom: 20px;
        }
        .day-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: #333;
            padding: 10px 15px;
            border-radius: 5px;
            margin-bottom: 10px;
        }
        .day-header h3 {
            margin: 0;
            font-size: 16px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 10px;
        }
        th {
            background: #667eea;
            color: white;
            padding: 10px;
            text-align: left;
            font-size: 12px;
        }
        td {
            border: 1px solid #ddd;
            padding: 10px;
            font-size: 11px;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        .footer {
            text-align: center;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #ddd;
            color: #999;
            font-size: 10px;
        }
        .empty-tip {
            text-align: center;
            color: #999;
            padding: 20px;
            font-style: italic;
        }
    </style>
</head>
<body>
    <!-- 标题 -->
    <div class="header">
        <h1>${itinerary.title}</h1>
        <div class="subtitle">✈️ 旅行规划行程单</div>
    </div>

    <!-- 基本信息 -->
    <div class="info-section">
        <h2>📋 行程信息</h2>
        <div class="info-grid">
            <div class="info-row">
                <div class="info-label">目的地：</div>
                <div class="info-value">${itinerary.destination!''}</div>
            </div>
            <div class="info-row">
                <div class="info-label">出发地：</div>
                <div class="info-value">${itinerary.startCity!''}</div>
            </div>
            <div class="info-row">
                <div class="info-label">行程天数：</div>
                <div class="info-value">${itinerary.days!0} 天</div>
            </div>
            <div class="info-row">
                <div class="info-label">开始日期：</div>
                <div class="info-value">${itinerary.startDate!''}</div>
            </div>
            <div class="info-row">
                <div class="info-label">结束日期：</div>
                <div class="info-value">${itinerary.endDate!''}</div>
            </div>
            <div class="info-row">
                <div class="info-label">总预算：</div>
                <div class="info-value">¥ ${itinerary.totalBudget!0}</div>
            </div>
            <div class="info-row">
                <div class="info-label">总费用：</div>
                <div class="info-value">¥ ${itinerary.totalCost!0}</div>
            </div>
            <div class="info-row">
                <div class="info-label">出行人数：</div>
                <div class="info-value">${itinerary.people!1} 人</div>
            </div>
            <#if itinerary.interests?? && itinerary.interests?has_content>
            <div class="info-row">
                <div class="info-label">兴趣偏好：</div>
                <div class="info-value"><#list itinerary.interests as i>${i}<#if i_has_next>, </#if></#list></div>
            </div>
            </#if>
        </div>
    </div>

    <!-- 行程明细 -->
    <#if itinerary.dayPlans?? && itinerary.dayPlans?size gt 0>
    <div class="info-section">
        <h2>🗺️ 行程明细</h2>
        
        <#list itinerary.dayPlans as dayPlan>
        <div class="day-section">
            <div class="day-header">
                <h3>📅 第 ${dayPlan.dayNumber} 天<#if dayPlan.dayDate?? && dayPlan.dayDate?has_content>（${dayPlan.dayDate}）</#if></h3>
            </div>
            
            <#if dayPlan.items?? && dayPlan.items?size gt 0>
            <table>
                <thead>
                    <tr>
                        <th style="width: 25%;">时间</th>
                        <th style="width: 55%;">描述</th>
                        <th style="width: 20%;">费用</th>
                    </tr>
                </thead>
                <tbody>
                    <#list dayPlan.items as item>
                    <tr>
                        <td>
                            <#if item.startTime?? && item.startTime?has_content>
                                ${item.startTime}
                                <#if item.endTime?? && item.endTime?has_content> - ${item.endTime}</#if>
                            <#else>
                                -
                            </#if>
                        </td>
                        <td>${item.itemDesc!''}</td>
                        <td>¥ ${item.itemPrice!0}</td>
                    </tr>
                    </#list>
                </tbody>
            </table>
            <#else>
            <div class="empty-tip">暂无行程安排</div>
            </#if>
        </div>
        </#list>
    </div>
    </#if>

    <!-- 底部 -->
    <div class="footer">
        <p>📌 祝您旅途愉快！</p>
        <p>生成时间：${currentTime}</p>
    </div>
</body>
</html>
