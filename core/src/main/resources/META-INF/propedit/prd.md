# 背景

为了最大化系统的可扩展性，我们需要设计一个dsl, 外界只需要提供符合格式的dsl，框架将可以据此快速生成相应的引导配置页面，配置项典型如数据库配置等需要重启才能生效的。

# 需求说明

我们希望:
1. 使用json格式来描述这段dsl, 
2. 基于模板引擎thymeleaf + smart_wizard JS框架的方式来实现配置界面的生成, 页面的基础模板参见项目下的validation.html.
3. 外界可以传递自定义的validation.html, 进而可以完全控制页面样式.

# 额外说明

1. 生成的系列文件存放到 META-INF/propedit文件夹下, 如果不存在你需要自己创建这个文件夹.

#########################################################
# 使用说明

## 1. JSON配置文件格式
范例： propedit/xxxx.json
```json
{
    "steps": [
        {
            "title": "数据库配置",
            "fields": [
                {
                    "name": "数据库地址",
                    "type": "text",
                    "id": "db_host",
                    "default": "localhost"
                },
                {
                    "name": "数据库端口",
                    "type": "text",
                    "id": "db_port",
                    "default": "3306"
                }
            ]
        }
    ]
}
```

### 支持的字段类型
- `text`: 文本输入框
- `checkbox`: 复选框
- `select`: 下拉选择框（需要提供options数组）

## 2. 配置方法

### 自动配置（推荐）
在`application.yml`或`application.properties`中配置：

```yaml
ssj:
  propedit:
    enabled: true                                        # 是否启用配置页面生成功能，默认true
    json-config-path: META-INF/propedit/props.json      # JSON配置文件路径，相对于classpath
    custom-template-path: META-INF/propedit/template.html  # 可选，自定义模板路径
    page-url-path: /propedit/config                     # 生成的页面访问路径
    output-path: META-INF/propedit2/config.html         # 生成的文件存放路径
```

系统启动后，可以通过配置的`page-url-path`直接访问配置页面，例如：`http://localhost:8080/propedit/config/configLQ.html`

### 手动调用
```java
ConfigToHtmlGenerator generator = new ConfigToHtmlGenerator();

// 使用默认模板
generator.generate(
    "path/to/config.json",
    "path/to/output.html"
);

// 使用自定义模板
generator.generate(
    "path/to/config.json",
    "path/to/output.html",
    "path/to/custom-template.html"
);
```

### REST API接口
1. 生成配置页面：
```http
GET /api/config/generate?jsonConfigPath=path/to/config.json&outputPath=path/to/output.html&customTemplatePath=path/to/template.html
```

2. 保存配置：
```http
POST /api/config/save
Content-Type: application/json

{
    "db_host": "localhost",
    "db_port": "3306"
}
```

3. 加载配置：
```http
GET /api/config/load
```

## 3. 注意事项

1. 配置文件相关：
   - JSON配置文件必须符合上述格式
   - 字段ID必须唯一
   - 对于select类型，必须提供options选项数组
   - default值为可选项

2. 模板相关：
   - 自定义模板必须包含必要的Thymeleaf标签
   - 需要保留基本的表单结构和验证逻辑
   - 可以自定义CSS样式和布局

3. 路径说明：
   - 所有配置文件默认放在`META-INF/propedit`目录下
   - 使用自定义模板时，建议放在`META-INF/propedit/templates`目录下

4. 依赖要求：
   - 需要引入Thymeleaf相关依赖
   - 需要引入Spring Boot Web依赖
   - 页面需要引入Bootstrap和SmartWizard相关的CSS和JS文件

5. 安全性：
   - 配置文件中不要包含敏感信息
   - 建议对API接口添加适当的访问控制
   - 生成的配置文件建议放在安全的位置

6. 自动配置说明：
   - 系统启动时会自动根据配置生成页面
   - 可以通过`ssj.propedit.enabled=false`禁用自动配置
   - 所有配置项都有默认值，可以零配置使用
   - 自定义模板路径为可选项，不配置则使用默认模板
