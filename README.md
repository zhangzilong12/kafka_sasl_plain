# Authentication using SASL/PLAIN 认证权限配置说明

## 本测试是在windows平台下,kafka版本0.10.0.0

#### server.properties修改
```
#对外通信协议及端口,客户端连接时使用,这里可以设置多个; PLAINTEXT表示使用普通的平面文件;SASL_PLAINTEXT采用SASL认证方式,
#客户端与服务器创建连接时需要账号和密码,其传输过程中使用明文;SASL_SSL传输过程中使用SSL加密的方式
listeners=PLAINTEXT://192.168.98.38:9092,SASL_PLAINTEXT://192.168.98.38:10092
advertised.listeners=PLAINTEXT://192.168.98.38:9092,SASL_PLAINTEXT://192.168.98.38:10092

#Security protocol used to communicate between brokers. Valid values are: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL.
#如果有SASL服务需要独立安装kerberos服务(https://access.redhat.com/documentation/en-US/Red_Hat_Enterprise_Linux/6/html/Managing_Smart_Cards/installing-kerberos.html)
#当前的测试环境没有安装使用kerberos服务,因此在broker内部使用的是PLAINTEXT,而使用PLAINTEXT导致的一个问题就是,客户端可以
#通过SASL_PLAINTEXT协议认证连接到服务器,但是对Topic读写,IP等授权(ACLs授权控制)的控制不起作用(因为配置了内部使用PLAINTEXT)
security.inter.broker.protocol=PLAINTEXT

sasl.enabled.mechanisms=PLAIN
sasl.mechanism.inter.broker.protocol=PLAIN
allow.everyone.if.no.acl.found=false

# super.users=User:admin

auto.create.topics.enable=false
```

### 添加kafka_server_jaas.conf
```
KafkaServer {
    org.apache.kafka.common.security.plain.PlainLoginModule required
    username="admin"
    password="admin-secret"
    user_alice="alice-secret";
};
```
> username,password是broker内部使用的账号和密码  
> `user_alice="alice-secret"`客户端连接时的账号 alice 密码 alice-secret  

### 添加kafka_client_jaas.conf
```
KafkaClient {
  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="alice"
  password="alice-secret";
};
```

### 启动zookeeper
```
zookeeper-server-start ../../config/zookeeper.properties
```

### 启动kafka
启动kafka前需要需要设置添加jvm参数值,可以在cmd里单独添加环境变量  
```
set KAFKA_OPTS=-Djava.security.auth.login.config=E:/thirdPackage/src/kafka/kafka_2.11-0.10.0.0/config/kafka_server_jaas.conf
kafka-server-start ../../config/server.properties
```

### 创建topic
```
kafka-topics --zookeeper localhost:2181 --create --topic Test-topic --partitions 1 --replication-factor 1 --config max.message.bytes=64000 --config flush.messages=1
```

### 添加授权用户,实测这里授权设置是不管用的
```
kafka-acls --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal User:alice  --operation Read --operation Write --topic Test-topic
```

### 代码测试生产和消费
像启动kafka前配置用户信息一样,启动测试程序前也需要设置在jvm中添加SASL的权限信息  
```
-Djava.security.auth.login.config=F:/src/test/java/kafka/kafka_client_jaas.conf
```
也可以在代码里设置  
```
System.setProperty("java.security.auth.login.config", "F:/src/test/java/kafka/kafka_client_jaas.conf");
```

