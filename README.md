# Deployment

## Build docker images for services locally
```
docker build -t orders-service ./orders-service
docker build -t delivery-service ./delivery-service
docker build -t orders-separation-service ./orders-separation-service
```

## Option 1: Deployment with Helm
```
helm install app _k8s_helm_chart/
```

## Option 2: Deployment with Kubernetes

```
kubectl apply -f _k8s/kafka
kubectl apply -f _k8s/postgres
kubectl apply -f _k8s/services
```

## Forward port
```
kubectl port-forward svc/orders-service-svc 8080:8080
```
