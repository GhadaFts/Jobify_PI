export const eurekaConfig = {
  instance: {
    app: 'auth-service',
    hostName: 'localhost',
    ipAddr: '127.0.0.1',
    port: {
      '$': 3000,
      '@enabled': true,
    },
    vipAddress: 'auth-service',
    statusPageUrl: 'http://localhost:3000/info',
    healthCheckUrl: 'http://localhost:3000/health',
    homePageUrl: 'http://localhost:3000/',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
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