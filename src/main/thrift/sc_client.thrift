namespace java mobi.bihu.crawler.sc.thrift

//all Message use JSON.
//this Service center is client,appapi is server.
service LoadService {
    i64 getAvaliableMemory();

    string requestServiceSituation(1: string requestType);
}
