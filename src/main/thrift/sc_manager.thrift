namespace java servicemanager.thrift

service SCManager {
    string registerService(1: string serviceName,2: string watchNode);
    string removeService(1: string serviceName);
    string getServiceNodes(1: string serviceName);
    string removeNodes(1: string nodePath);
}
