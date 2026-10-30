> 🎟️ **在线体验：[访问 Damai 演出订票平台](https://8.160.171.211/)**
>
> 浏览演出、查看票档、管理订单，并通过顶部导航使用 AI 助手。
>
> 🔒 当前使用自签名 HTTPS 证书，浏览器可能提示证书不受信任；AI 助手需要访问密码。

# Damai

一个基于微服务架构的在线演出订票系统，覆盖节目浏览、票档查询、用户注册与登录、下单、支付和订单管理等流程。项目关注订票业务中的库存控制、并发请求处理和服务间协作。

## 技术栈

- **后端**：Java 17、Spring Boot 3、Spring Cloud、Spring Cloud Alibaba、Spring Cloud Gateway、Nacos
- **数据存储与访问**：MySQL、MyBatis-Plus、ShardingSphere、Redis、Redisson
- **消息与检索**：Kafka、Redis Stream、Elasticsearch
- **前端**：Vue 3、Vite、Element Plus、Pinia、Vue Router
- **其他**：AJ-Captcha、Knife4j、支付宝沙箱 SDK、Spring Boot Admin

## 项目结构

```text
damai
├── damai-server/                    # 网关及各业务服务
│   ├── damai-gateway-service/       # API 网关
│   ├── damai-user-service/          # 用户与观演人
│   ├── damai-program-service/       # 节目、票档、座位与下单入口
│   ├── damai-order-service/         # 订单管理
│   ├── damai-pay-service/           # 支付处理
│   ├── damai-base-data-service/     # 基础数据和渠道配置
│   ├── damai-customize-service/     # 自定义规则
│   ├── damai-admin-service/         # 服务监控
│   └── damai-mybatis-plus-service/  # 代码生成工具
├── damai-server-client/             # 服务间接口与数据模型
├── damai-common/                    # 通用模型、工具与异常处理
├── damai-*-framework/               # 缓存、锁、验证码、线程池等组件
├── sql/                             # 数据库初始化脚本
├── vue3/                            # Web 前端
└── benchmark-results/               # 测试记录
```

## 技术亮点

- **业务服务拆分**：按用户、节目、订单、支付等领域拆分服务，通过网关和服务发现组织调用。
- **多种下单策略**：节目服务提供四种下单策略，结合锁、缓存及异步处理等机制，便于比较不同实现。
- **分库分表与数据加密**：使用 ShardingSphere 配置用户、节目、订单和支付数据分片，并在用户数据中配置加密规则。
- **缓存与并发控制**：结合 Redis、Redisson、Lua 脚本和服务锁处理缓存、库存与并发访问。
- **异步消息处理**：使用 Kafka 和 Redis Stream 处理部分异步业务；订单服务包含基于消息的创建流程。
- **可复用基础组件**：将验证码、分布式锁、延迟队列、线程池等能力封装为独立模块。

## 关联项目

[Damai AI](https://github.com/IamDazing/damai-ai) 为订票系统提供自然语言交互、规则问答及运维查询能力。

## 🌟 社区关注

关联项目 [Damai AI](https://github.com/IamDazing/damai-ai) 的[登录校验与异常提示提交](https://github.com/IamDazing/damai-ai/commit/9b6a7ff05481960a9a71bb2c91c60014ccc10f9c)获 [XERJ](https://github.com/xerj-org/xerj) 关注。根据对方邮件，该提交已被选入 XERJ Corpus Hub 的参考代码收录计划。

## 许可证

[Apache License 2.0](LICENSE)
