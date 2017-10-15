#!/usr/bin/env bash


if [[ -z "${ISTIO_HOME}" ]]; then
  echo "Set istio home environment variable ISTIO_HOME"
  exit 0;
fi

# Must start minikube with these args for automatic sidecar injection
(minikube delete || true) &>/dev/null && \
minikube start --kubernetes-version=v1.7.5 --memory 8192 && \
eval $(minikube docker-env)

#set ISTIO home env
kubectl apply -f ${ISTIO_HOME}/install/kubernetes/istio.yaml

#kubectl apply -f ${ISTIO_HOME}/install/kubernetes/istio-auth.yaml

#install if you want automatic sidecar injection
kubectl apply -f ${ISTIO_HOME}/install/kubernetes/istio-initializer.yaml

# adding zipkin
kubectl apply -f ${ISTIO_HOME}/install/kubernetes/addons/zipkin.yaml

# prometheus
kubectl apply -f ${ISTIO_HOME}/install/kubernetes/addons/prometheus.yaml

# grafana
kubectl apply -f ${ISTIO_HOME}/install/kubernetes/addons/grafana.yaml

# Start Cassandra
kubectl create -f deploy/kubernetes/resources/cassandra

# Start Zookeeper
kubectl create -f deploy/kubernetes/resources/zookeeper

# Start Kafka
kubectl create -f deploy/kubernetes/resources/kafka

# Start Users
kubectl create -f deploy/kubernetes/resources/ride-share-app