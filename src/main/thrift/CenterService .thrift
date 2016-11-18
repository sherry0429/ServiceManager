namespace java mobi.bihu.crawler.sc.thrift

//this Service,center is server,other want to get Service was client.
service CenterManageService {
    string getSuitableNode(1: string request);
}
