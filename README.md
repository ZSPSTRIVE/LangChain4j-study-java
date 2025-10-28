# AI 功能集成说明

## 概述

本项目已成功集成 **LangChain4j** 框架，实现了基于阿里云通义千问大模型的 AI 功能，包括：

1. **AI 生成关卡**：根据用户薪资动态生成程序员技术练兵场关卡。
2. **AI 生成结果报告**：根据用户答题情况生成详细的结果分析报告。

## 已完成的功能

### 1. AI 配置类 (`AiConfig.java`)
- 配置阿里云通义千问大模型。
- 创建 AI 服务的 Bean 实例。
- 支持通过配置文件动态配置模型参数，确保灵活性与可扩展性。

### 2. AI 服务接口
- **`LevelGenerationAiService`**：关卡生成服务，利用薪资信息生成不同难度的技术关卡。
- **`ResultReportAiService`**：结果报告生成服务，根据用户答题结果生成详细分析报告。
- 使用 `@SystemMessage` 注解引入提示词文件，`@UserMessage` 注解接收业务层拼接的消息，确保消息的格式灵活。

### 3. 服务实现类集成
- **`LevelServiceImpl`**：集成关卡生成 AI 服务，包含降级处理，确保在 AI 服务不可用时提供默认关卡。
- **`UserLevelServiceImpl`**：集成结果报告生成 AI 服务，包含降级处理，确保在 AI 服务不可用时提供默认报告。

### 4. 数据传输对象 (DTO)
- **`LevelGenerationResponse`**：关卡生成响应对象。
- **`ResultReportResponse`**：结果报告响应对象，包含得分与分析内容。
- **`LevelOption`**：关卡选项，包含选项内容和正确答案。

## 配置说明

### 1. 环境变量配置
在 `application.yml` 中配置阿里云 API 参数：
```yaml
langchain4j:
  dashscope:
    api-key: ${DASHSCOPE_API_KEY:your-api-key-here}
    model-name: qwen-max
    temperature: 0.7
    timeout: 60s
2. 本地开发配置

在 application-local.yml 中直接配置 API Key：

langchain4j:
  dashscope:
    api-key: your-actual-api-key
    model-name: qwen-max
    temperature: 0.7
    timeout: 60s

API 接口
1. 生成关卡

POST 请求，传入薪资参数生成技术关卡：

POST /api/level/generate
Content-Type: application/json

{
  "salary": 10000
}

2. 提交答案

POST 请求，提交用户答案并生成结果报告：

POST /api/user-level/submit
Content-Type: application/json

{
  "levelId": "关卡ID",
  "userOptions": ["选项1", "选项2"]
}

提示词文件
1. 关卡生成提示词 (prompts/level-generation-system.txt)

定义了如何根据用户薪资生成合适难度的关卡，包括：

关卡名称和描述

技术选项（正确答案和干扰项）

难度等级

目标薪资范围

2. 结果报告提示词 (prompts/result-report-system.txt)

定义了如何生成详细的结果分析报告，包括：

评分和评价

薪资调整建议

公司投递建议

标准答案解析

错误处理和降级机制
1. 关卡生成降级

当 AI 服务不可用时，会生成一个默认的临时关卡，确保用户体验不中断。

2. 结果报告降级

当 AI 服务不可用时，会生成一个默认的结果报告，给出及格分数但不调整薪资，确保系统的稳定性。

测试
1. 单元测试

运行 AiServiceTest 来测试 AI 服务的基本功能：

mvn test -Dtest=AiServiceTest

2. 集成测试

启动应用后，可以通过 Swagger UI 测试完整的 API 流程：

访问：http://localhost:8123/api/doc.html

注意事项

API Key 安全：

生产环境请使用环境变量配置 API Key。

不要将 API Key 提交到代码仓库，避免泄露敏感信息。

模型选择：

当前使用 qwen-plus 模型，平衡效果与成本。

可根据需求调整为 qwen-max（提供更好效果）或 qwen-turbo（更低成本，适用于高并发场景）。

消息拼接：

@UserMessage 注解不支持多参数模板。

在业务层使用 Java 模板字符串 (String.format("""...""")) 拼接复杂消息，灵活控制消息格式。

错误处理：

所有 AI 调用都有降级机制，确保服务不可用时不影响用户体验。

错误信息将被详细记录，便于后续调试与修复。

性能优化：

AI 调用可能较慢，建议在前端添加加载提示或进度条。

可考虑添加缓存机制，减少重复调用，提高系统响应速度。

依赖版本

LangChain4j: 1.4.0-beta10

Spring Boot: 3.5.5

Java: 21

故障排除

编译错误：确保使用 Java 21，并检查 JDK 配置。

API 调用失败：检查 API Key 配置及网络连接是否正常。

JSON 解析错误：检查 AI 返回的 JSON 格式是否正确，确保按预期处理。
