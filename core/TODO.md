<s> 1. 静态页面配置化. 用户可以提供自己的loading页面.   IStaticLoadingPageFactory.java </s>
<s> 2. 提供界面化的启动配置项. 即应用初始启动后马上呈现配置项界面, 待用户配置完毕之后系统再继续启动. </s> 
<s> 3. 借鉴druid, 提供对外扩展性注册, 将 url与对应的处理函数以扩展的形式暴露给外界, 供外界满足自定义需求.  IRequestDealer.java  </s>
<s> 4. 提供唯一的扩展性接口. 不要分散在各个子接口里. ISsjExtendProvider.java  </s>
<s> 5. sample样例  </s>
<s> 6. 支持不同的springboot版本 —— 2.4+ AND 2.4-  </s>
7. 推送到官方maven仓库
<s> 8. github流水线</s>
9. github workspace
10. github release
11. 提供一个配置设置界面。其中用户可以通过编辑元信息文件(形如项目中的prop.json文件)来设置有哪些需要配置的配置项. 然后后端代码通过解析这个配置文件来生成配置界面. 相关类型 ConfigToHtmlGenerator.java