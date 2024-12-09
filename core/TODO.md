<s> 1. 静态页面配置化. 用户可以提供自己的loading页面.   IStaticLoadingPageFactory.java </s>
<s> 2. 提供界面化的启动配置项. 即应用初始启动后马上呈现配置项界面, 待用户配置完毕之后系统再继续启动. </s> 
<s> 3. 借鉴druid, 提供对外扩展性注册, 将 url与对应的处理函数以扩展的形式暴露给外界, 供外界满足自定义需求.  IRequestDealer.java  </s>
<s> 4. 提供唯一的扩展性接口. 不要分散在各个子接口里. ISsjExtendProvider.java  </s>
<s> 5. sample样例  </s>
<s> 6. 支持不同的springboot版本 —— 2.4+ AND 2.4-  </s>