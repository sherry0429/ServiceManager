namespace java mobi.bihu.crawler.sc.thrift

//this Service,center is server,other want to get Service was client.
service sc_server {
    string getSuitable(1: string serviceName);
}
