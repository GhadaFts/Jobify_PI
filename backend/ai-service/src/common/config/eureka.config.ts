export const eurekaConfig = {
  instance: {
    app: 'ai-service',
    hostName: 'localhost',
    ipAddr: '127.0.0.1',
    port: {
      '$': 3001,
      '@enabled': true,
    },
    vipAddress: 'ai-service',
    statusPageUrl: 'http://localhost:3001/info',
    healthCheckUrl: 'http://localhost:3001/health',
    homePageUrl: 'http://localhost:3001/',
    metadata: {
      'management.port': '3001',
    },
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
    leaseInfo: {
      renewalIntervalInSecs: 30,
      durationInSecs: 90,
    },
  },
  eureka: {
    host: 'localhost',
    port: 8761,
    servicePath: '/eureka/apps/',
    maxRetries: 10,
    requestRetryDelay: 2000,
  },
};