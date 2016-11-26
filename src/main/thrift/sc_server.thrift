namespace java mobi.bihu.crawler.sc.thrift

service SCService {
    string getSuitable(1: string serviceName);
    string registerService(1: string serviceName,2: string watchNode,3: string selectType); 
}
