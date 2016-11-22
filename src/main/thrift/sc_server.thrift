namespace java mobi.bihu.crawler.sc.thrift

//this Service,center is server,other want to get Service was client.
service SCService {
    string getSuitable(1: string serviceName);
    string registerService(1: string serviceName,2: string watchNode); 
}
