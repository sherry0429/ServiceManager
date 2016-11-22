namespace java mobi.bihu.crawler.sc.thrift

//all Message use JSON.
//this Service center is client,appapi is server.
service SCClientService {
    string requestServiceSituation(1: string requestType);
}
