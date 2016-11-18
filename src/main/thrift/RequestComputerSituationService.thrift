namespace java mobi.bihu.crawler.sc.thrift

//all Message use JSON.
//this Service center is client,appapi is server.
service RequestComputerService {
    string requestComputerSituation(1: string requestJSON);
}
