# Kubernetes from Zero to L3 Production Support

## Complete learning notes --- all 20 steps

> **Purpose:** A consolidated reference for the Kubernetes learning path
> discussed in this chat, using Java/Spring Boot, Docker, CI/CD, and L3
> production-support examples.
>
> **Scope note:** This document brings together the 20-step path and the
> details preserved in the conversation summary. It includes
> explanations, diagrams, commands, YAML examples, troubleshooting
> cases, and quiz recap. Some topics naturally overlap (for example,
> Services and Ingress); those overlaps are retained because they serve
> different learning goals.

------------------------------------------------------------------------

## Table of contents

1.  [Why Kubernetes was needed](#1-why-kubernetes-was-needed)
2.  [Docker versus Kubernetes](#2-docker-versus-kubernetes)
3.  [Cluster, nodes, and control
    plane](#3-cluster-nodes-and-control-plane)
4.  [Pods](#4-pods)
5.  [Deployments and ReplicaSets](#5-deployments-and-replicasets)
6.  [Services](#6-services)
7.  [Service types and Ingress
    overview](#7-service-types-and-ingress-overview)
8.  [ConfigMaps and Secrets](#8-configmaps-and-secrets)
9.  [Startup, readiness, and liveness
    probes](#9-startup-readiness-and-liveness-probes)
10. [CPU and memory requests and
    limits](#10-cpu-and-memory-requests-and-limits)
11. [Namespaces](#11-namespaces)
12. [Rolling updates and rollbacks](#12-rolling-updates-and-rollbacks)
13. [Logs and troubleshooting](#13-logs-and-troubleshooting)
14. [Networking and Service
    discovery](#14-networking-and-service-discovery)
15. [RBAC](#15-rbac-role-based-access-control)
16. [Storage: Volumes, PVs,
    PVCs](#16-storage-volumes-persistentvolumes-and-persistentvolumeclaims)
17. [Ingress and external access in
    detail](#17-ingress-and-external-access-in-detail)
18. [Configuration and deployment
    troubleshooting](#18-configuration-and-deployment-troubleshooting)
19. [Monitoring and observability](#19-monitoring-and-observability)
20. [End-to-end L3 production incident
    practice](#20-end-to-end-l3-production-incident-practice)
21. [Complete deployment flow](#21-complete-deployment-flow)
22. [Command cheat sheet](#22-command-cheat-sheet)
23. [Important distinctions to
    remember](#23-important-distinctions-to-remember)
24. [Quiz recap and self-test](#24-quiz-recap-and-self-test)
25. [Further topics after the first 20
    steps](#25-further-topics-after-the-first-20-steps)

------------------------------------------------------------------------

# 1. Why Kubernetes was needed

Imagine you develop a Java Spring Boot application. You compile the
code, build a JAR, and deploy it to a server.

``` mermaid
flowchart TD
    A[Java source code] --> B[Maven build]
    B --> C[Spring Boot JAR]
    C --> D[Deploy to server]
    D --> E[Application runs]
```

## 1.1 Physical servers

Initially, applications were deployed directly on physical servers.

Problems included:

-   Applications could compete for CPU and memory.
-   Hardware resources could be underused.
-   Different applications could require conflicting runtime versions or
    dependencies.
-   Scaling often required more hardware and manual setup.
-   Hardware failure could make an application unavailable.
-   Moving an application between environments could be difficult.

## 1.2 Virtual machines

Virtual machines allowed multiple isolated operating systems to run on
one physical machine.

``` text
Physical server
├── Hypervisor
│   ├── VM 1: Guest OS + Application A
│   ├── VM 2: Guest OS + Application B
│   └── VM 3: Guest OS + Application C
```

VMs improved isolation and utilization, but each VM typically required
its own guest operating system, which consumed resources.

## 1.3 Containers

Containers package an application and its dependencies into an isolated
runtime environment while sharing the host operating system's kernel.

Containers are generally lighter than full VMs because they do not each
need a separate guest OS.

## 1.4 Why orchestration became necessary

Running one container is manageable. Running hundreds of containers
across multiple machines creates new questions:

-   What happens when a container crashes?
-   How do we maintain three or ten application instances?
-   How do we distribute workloads across machines?
-   How do services discover one another?
-   How do we deploy a new version safely?
-   What happens when a machine fails?
-   How do we configure applications without rebuilding images?
-   How do we monitor and recover applications consistently?

Kubernetes addresses these container-orchestration problems.

``` mermaid
flowchart TD
    A[Physical servers] --> B[Virtual machines]
    B --> C[Containers]
    C --> D[Docker makes images and containers easier to build and run]
    D --> E[Many containers across many machines]
    E --> F[Orchestration challenges]
    F --> G[Kubernetes]
```

**Key idea:** Kubernetes was needed because managing containerized
applications at scale is more complex than starting an individual
container.

------------------------------------------------------------------------

# 2. Docker versus Kubernetes

## 2.1 What Docker helps with

Docker commonly helps developers:

-   Define an application image with a `Dockerfile`.
-   Build an image.
-   Run a container from that image.
-   Tag and distribute images through a registry.
-   Package an application consistently across environments.

Important terms:

-   **Dockerfile:** Instructions for building an image.
-   **Image:** A packaged template containing application artifacts and
    required runtime components.
-   **Container:** A running instance of an image.
-   **Registry:** A place to store and distribute images, such as Docker
    Hub or Amazon ECR.

Example flow:

``` mermaid
flowchart TD
    A[Java code] --> B[Maven builds JAR]
    B --> C[Dockerfile]
    C --> D[Docker image]
    D --> E[Container registry]
    E --> F[Container runtime runs image]
```

## 2.2 What Kubernetes helps with

Kubernetes manages containerized workloads across a cluster of machines.
It helps with:

-   Maintaining the desired number of application instances.
-   Replacing failed Pods.
-   Scheduling workloads on suitable nodes.
-   Service discovery and networking.
-   Rolling out new application versions.
-   Scaling applications.
-   Managing configuration and storage.
-   Health checking and workload recovery.

## 2.3 Comparison

  -----------------------------------------------------------------------
  Docker / container tooling          Kubernetes
  ----------------------------------- -----------------------------------
  Builds and runs containers          Orchestrates containerized
  (depending on the tool/runtime)     workloads across a cluster

  Packages the application into an    Declares and maintains desired
  image                               workload state

  A container is an execution         A Pod is Kubernetes' smallest
  instance                            deployable unit

  Does not by itself provide full     Provides scheduling, controllers,
  cluster orchestration               Service discovery, rollout
                                      mechanisms, and related
                                      orchestration
  -----------------------------------------------------------------------

Modern Kubernetes normally uses container runtimes such as `containerd`
or CRI-O through the Container Runtime Interface (CRI). Kubernetes does
**not** require Docker Engine specifically.

**Remember:** Docker helps package and run containers; Kubernetes
orchestrates containerized workloads.

------------------------------------------------------------------------

# 3. Cluster, nodes, and control plane

## 3.1 Cluster

A Kubernetes cluster is a Kubernetes environment made up of a control
plane and worker nodes.

## 3.2 Node

A node is a machine in the cluster. It may be:

-   A virtual machine, such as an EC2 instance.
-   A physical machine.

A node is not the same thing as a Pod. Multiple Pods can run on one
worker node.

## 3.3 Control plane

The control plane manages the cluster and coordinates desired state.

Key components:

-   **API server:** Entry point for Kubernetes API requests.
-   **Scheduler:** Chooses a suitable node for a newly scheduled Pod.
-   **Controller manager:** Runs controllers that compare actual state
    with desired state and reconcile differences.
-   **etcd:** Stores Kubernetes cluster state.

## 3.4 Worker node

A worker node runs workloads. Important components include:

-   **Kubelet:** Communicates with the control plane and ensures
    assigned Pods are running as specified.
-   **Container runtime:** Runs containers.
-   **Networking components:** Support Pod and Service networking.

``` mermaid
flowchart TB
    subgraph CP[Control Plane]
        API[API server]
        SCH[Scheduler]
        CTRL[Controller manager]
        ETCD[(etcd)]
        API <--> ETCD
        API --> SCH
        API --> CTRL
    end
    subgraph W1[Worker Node 1]
        K1[Kubelet]
        P1[Pod A]
        P2[Pod B]
        K1 --> P1
        K1 --> P2
    end
    subgraph W2[Worker Node 2]
        K2[Kubelet]
        P3[Pod C]
        K2 --> P3
    end
    API <--> K1
    API <--> K2
```

**Important distinction:** The control plane coordinates the cluster;
worker nodes provide compute capacity for workloads.

------------------------------------------------------------------------

# 4. Pods

## 4.1 What is a Pod?

A Pod is the **smallest deployable unit in Kubernetes**. It contains one
or more containers.

A typical Spring Boot microservice often runs one application container
per Pod.

``` text
Kubernetes cluster
    |
    +-- Worker node
          |
          +-- Pod
                |
                +-- Container
                      |
                      +-- Spring Boot application
```

## 4.2 Can a Pod contain multiple containers?

Yes. A Pod can contain an application container and supporting
containers, such as a sidecar.

Containers in the same Pod:

-   Share the Pod's network namespace.
-   Can communicate through `localhost`.
-   Can share configured volumes.
-   Are scheduled together as a unit.

## 4.3 Pod IP and lifecycle

-   A Pod has its own IP address.
-   Containers in the same Pod share that network identity.
-   A Pod may be replaced when it fails, is deleted, or is recreated by
    a controller.
-   A replacement Pod may receive a different IP address.
-   Production workloads are usually managed through Deployments or
    other higher-level controllers rather than created manually.

## 4.4 First troubleshooting commands

``` bash
kubectl get pods
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

Use `kubectl describe pod` to inspect status, events, scheduling,
container state, and probe failures. Use logs to inspect application
output.

------------------------------------------------------------------------

# 5. Deployments and ReplicaSets

Suppose the application should run three instances. Creating and
maintaining every Pod manually would be inconvenient.

## 5.1 Deployment

A Deployment declares desired application state, including details such
as:

-   The desired replica count.
-   The Pod template.
-   The container image and version.
-   The rollout strategy.

## 5.2 ReplicaSet

A ReplicaSet ensures that the required number of matching Pods exists.

``` mermaid
flowchart TD
    D[Deployment] --> RS[ReplicaSet]
    RS --> P1[Pod 1]
    RS --> P2[Pod 2]
    RS --> P3[Pod 3]
    P1 --> C1[Container]
    P2 --> C2[Container]
    P3 --> C3[Container]
```

## 5.3 Example Deployment

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
        - name: order-service
          image: your-registry/order-service:1.0
          ports:
            - containerPort: 8080
```

What the fields mean:

-   `replicas: 3`: Desired number of Pods.
-   `selector.matchLabels`: Labels the Deployment uses to identify its
    Pods.
-   `template`: Template used to create Pods.
-   `containers`: Container definitions.
-   `image`: Image and tag to run.
-   `containerPort`: Documents the container's intended listening port;
    it does not itself publish the port outside the Pod.

## 5.4 Desired state and reconciliation

If one Pod disappears, the ReplicaSet controller attempts to restore the
desired replica count. Kubernetes continuously reconciles actual state
toward declared desired state.

Kubernetes may restart a failed container inside an existing Pod, or a
controller may create a replacement Pod if the Pod is missing or
deleted. It does not necessarily move the same container to another
machine.

## 5.5 Useful commands

``` bash
kubectl get deployments
kubectl get replicasets
kubectl get pods
kubectl describe deployment order-service
```

------------------------------------------------------------------------

# 6. Services

## 6.1 Why a Service is needed

Suppose three Pods run `order-service`:

``` text
Pod 1 -> 10.1.0.5
Pod 2 -> 10.1.0.8
Pod 3 -> 10.1.0.12
```

Pod IPs can change when Pods are replaced. A client that hardcodes a Pod
IP can break when that Pod disappears.

A Service provides a stable network endpoint for a set of eligible Pods.

``` mermaid
flowchart TD
    CLIENT[Client / another microservice] --> SVC[order-service Service]
    SVC --> P1[Pod 1]
    SVC --> P2[Pod 2]
    SVC --> P3[Pod 3]
```

## 6.2 Example ClusterIP Service

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: order-service
spec:
  selector:
    app: order-service
  ports:
    - port: 8080
      targetPort: 8080
  type: ClusterIP
```

-   `selector`: Identifies matching Pods through labels.
-   `port`: Port exposed by the Service.
-   `targetPort`: Port on the selected Pods.
-   `ClusterIP`: Provides an internal cluster endpoint.

Another microservice in the same namespace can typically call:

``` text
http://order-service:8080/orders
```

## 6.3 Deployment versus Service

  -----------------------------------------------------------------------
  Deployment                          Service
  ----------------------------------- -----------------------------------
  Declares desired Pod state and      Provides a stable endpoint for
  manages rollout                     eligible Pods

  Manages replicas through            Selects endpoints, commonly using
  ReplicaSets                         labels

  Defines image and Pod template      Defines network-facing ports and
                                      Service type
  -----------------------------------------------------------------------

A Service does not replace a Deployment; they solve different problems.

------------------------------------------------------------------------

# 7. Service types and Ingress overview

## 7.1 ClusterIP

-   Default Service type.
-   Provides an internal cluster endpoint.
-   Commonly used for microservice-to-microservice communication.

## 7.2 NodePort

-   Exposes a Service on a port across the cluster's nodes.
-   The default NodePort range is usually `30000–32767`, but it is
    configurable.
-   External access also requires suitable networking and firewall
    rules.

## 7.3 LoadBalancer

-   Requests integration with an external load balancer.
-   Common in cloud environments, depending on the cloud provider and
    cluster configuration.

## 7.4 Ingress

-   Defines HTTP/HTTPS routing rules.
-   Can route different hostnames or URL paths to different Services.
-   Requires an Ingress Controller to implement the rules.
-   Creating an Ingress object alone does not guarantee public access.

Example conceptual route:

``` text
Client
  |
  v
Ingress Controller
  |----------------------|
  v                      v
/orders                /payments
  |                      |
  v                      v
order-service        payment-service
```

Ingress is discussed in greater detail in Step 17.

------------------------------------------------------------------------

# 8. ConfigMaps and Secrets

Applications need configuration such as database hostnames, environment
names, log levels, usernames, passwords, and API tokens. Configuration
should generally not be hardcoded into source code or baked into an
image when it should vary by environment.

## 8.1 ConfigMap

A ConfigMap stores **non-sensitive configuration**.

Example:

``` yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: order-config
data:
  APP_ENV: production
  DB_HOST: mysql-prod.internal
  DB_NAME: orders_prod
```

A Pod can load the values as environment variables:

``` yaml
envFrom:
  - configMapRef:
      name: order-config
```

Spring Boot can read an environment variable:

``` java
String dbHost = System.getenv("DB_HOST");
```

Or use it in `application.properties`:

``` properties
spring.datasource.url=jdbc:mysql://${DB_HOST}:3306/${DB_NAME}
```

## 8.2 Secret

A Secret stores sensitive values such as passwords, tokens, and
certificates.

Example using `stringData`:

``` yaml
apiVersion: v1
kind: Secret
metadata:
  name: order-db-secret
type: Opaque
stringData:
  DB_USERNAME: app_user
  DB_PASSWORD: replace-with-real-password
```

Reference an individual key:

``` yaml
env:
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: order-db-secret
        key: DB_PASSWORD
```

Do not commit real credentials to public Git repositories.

## 8.3 ConfigMap versus Secret

  ConfigMap                            Secret
  ------------------------------------ ---------------------------------
  Non-sensitive configuration          Sensitive values
  Database host, app mode, log level   Passwords, tokens, certificates
  Not intended for credentials         Requires careful access control

**Security nuance:** Base64 encoding is not encryption. Kubernetes
Secrets are not automatically secure just because they are stored as
Secret objects. Use appropriate RBAC, encryption at rest, restricted
access, and a managed secret store where appropriate.

## 8.4 Configuration update behavior

If a ConfigMap value is supplied as an environment variable, updating
the ConfigMap does **not** change that environment variable inside an
already-running process. A restart or rollout is normally required.

Mounted ConfigMap files behave differently: their contents can update
eventually, but the application must support reloading if it needs to
apply the change.

## 8.5 SDLC connection

``` mermaid
flowchart TD
    A[Java source code] --> B[Git repository]
    B --> C[Jenkins CI and tests]
    C --> D[Build Docker image]
    D --> E[Push image to registry]
    E --> F[Kubernetes Deployment]
    F --> G[Pods and containers]
    H[ConfigMap and Secret] --> G
    G --> I[Spring Boot starts with environment-specific configuration]
```

------------------------------------------------------------------------

# 9. Startup, readiness, and liveness probes

Kubernetes needs to know whether an application has started, can receive
traffic, or is stuck and needs restarting.

Spring Boot Actuator can expose health endpoints when configured, for
example:

``` text
/actuator/health/liveness
/actuator/health/readiness
```

The endpoints must be enabled and reachable by the probes.

## 9.1 Liveness probe

Checks whether the application is still functioning.

If the liveness probe fails repeatedly according to configured
thresholds, Kubernetes restarts the container.

## 9.2 Readiness probe

Checks whether the application is ready to receive traffic.

If readiness fails, the Pod is removed from normal Service traffic while
it is not ready. The container is not automatically restarted just
because readiness failed.

## 9.3 Startup probe

Gives a slow-starting application more time to initialize. Liveness and
readiness checks are held back until the startup probe succeeds.

  -----------------------------------------------------------------------
  Probe                   Question it answers     Typical consequence of
                                                  repeated failure
  ----------------------- ----------------------- -----------------------
  Startup                 Has initialization      Container restart after
                          completed?              configured failure
                                                  threshold

  Readiness               Can the application     Pod stops receiving
                          receive traffic?        normal Service traffic

  Liveness                Is the application      Container restart
                          still functioning?      
  -----------------------------------------------------------------------

Conceptual flow:

``` mermaid
flowchart TD
    A[Container starts] --> B{Startup probe configured?}
    B -- Yes --> C[Wait for startup probe to succeed]
    C --> D[Run readiness and liveness checks]
    B -- No --> D
    D --> E{Readiness succeeds?}
    E -- No --> F[Do not send normal Service traffic]
    E -- Yes --> G[Eligible for normal Service traffic]
    D --> H{Liveness repeatedly fails?}
    H -- Yes --> I[Restart container]
    H -- No --> D
```

### Important production-support case

If liveness checks start too early while a Spring Boot application is
still initializing, Kubernetes can repeatedly restart it before startup
completes. A suitable startup probe can prevent this.

Avoid making liveness depend on a transient database outage unless
restarting the application is genuinely the right recovery action.

------------------------------------------------------------------------

# 10. CPU and memory requests and limits

Example:

``` yaml
resources:
  requests:
    cpu: "250m"
    memory: "512Mi"
  limits:
    cpu: "1"
    memory: "1Gi"
```

## 10.1 Requests

A request tells Kubernetes how much resource a container is expected to
need for scheduling.

-   CPU request influences scheduling and CPU allocation
    priority/shares.
-   Memory request influences scheduling.
-   A request is not a hard usage cap.
-   `250m` CPU means one quarter of a CPU core.

## 10.2 Limits

-   CPU above the configured limit can result in throttling.
-   Memory above the configured limit can lead to the container being
    terminated for exceeding the memory limit, often showing
    `OOMKilled`.

For Java applications, memory usage includes more than the heap. It may
include native memory, thread stacks, direct buffers, and other JVM
overhead.

## 10.3 Troubleshooting commands

``` bash
kubectl describe pod <pod-name>
kubectl top pod <pod-name>
kubectl get events --sort-by=.metadata.creationTimestamp
```

`kubectl top` requires working metrics support, such as Metrics Server.

### Example: `OOMKilled`

Investigate:

-   Actual memory usage and trends.
-   The container memory limit.
-   JVM heap configuration.
-   Native memory and thread stacks.
-   Traffic and workload changes.
-   Memory leaks or unusually large requests.

Do not simply increase the memory limit without checking the evidence.

------------------------------------------------------------------------

# 11. Namespaces

A Namespace logically separates resources within a cluster.

``` text
Kubernetes cluster
├── dev
│   ├── order-service
│   └── payment-service
├── test
│   ├── order-service
│   └── payment-service
└── prod
    ├── order-service
    └── payment-service
```

Different namespaces can contain resources with the same name.

Namespaces help organize resources, apply access permissions, and manage
quotas. They are not separate physical clusters, and creating namespaces
does not automatically provide network isolation.

## 11.1 Commands

``` bash
kubectl get namespaces
kubectl get pods -n prod
kubectl get services -n prod
kubectl get pods --all-namespaces
```

Set the current context's default namespace:

``` bash
kubectl config set-context --current --namespace=prod
```

This changes the default namespace used by commands that omit `-n`; it
does not change access permissions.

## 11.2 Production-support example

If a Pod exists in `prod` but a command runs in another namespace, the
Pod may not appear in the result. Verify the namespace before assuming
the Pod is missing.

------------------------------------------------------------------------

# 12. Rolling updates and rollbacks

Suppose the current application image is version `1.0` and you want to
deploy `1.1`.

A Deployment can update Pods gradually using a rolling update strategy.

``` mermaid
flowchart TD
    A[Version 1.0 Pods running] --> B[Update Deployment image to 1.1]
    B --> C[Create new Pods]
    C --> D[Check new Pods' readiness]
    D --> E[Gradually replace old Pods]
    E --> F[Version 1.1 rollout completes]
```

## 12.1 Update an image

``` bash
kubectl set image deployment/order-service order-service=your-registry/order-service:1.1
```

## 12.2 Check rollout status

``` bash
kubectl rollout status deployment/order-service
```

## 12.3 View rollout history

``` bash
kubectl rollout history deployment/order-service
```

## 12.4 Roll back

``` bash
kubectl rollout undo deployment/order-service
```

A Deployment rollback restores a previous Deployment revision. It does
not automatically reverse database schema changes or external side
effects.

## 12.5 Production considerations

-   Readiness probes help keep unready Pods out of normal Service
    traffic.
-   The rollout strategy controls how many Pods can be unavailable or
    created above the desired replica count.
-   Insufficient cluster capacity, bad configuration, image-pull errors,
    and failed health checks can block a rollout.
-   A rolling update does not guarantee zero downtime in every
    situation.
-   Verify the application and business functionality after deployment
    or rollback.

------------------------------------------------------------------------

# 13. Logs and troubleshooting

In L3 support, investigate evidence rather than immediately restarting a
failing application.

## 13.1 Core commands

``` bash
kubectl get pods
kubectl logs <pod-name>
kubectl logs <pod-name> --previous
kubectl describe pod <pod-name>
kubectl get events --sort-by=.metadata.creationTimestamp
```

For a multi-container Pod:

``` bash
kubectl logs <pod-name> -c <container-name>
```

To specify a namespace:

``` bash
kubectl logs <pod-name> -n prod
kubectl describe pod <pod-name> -n prod
```

## 13.2 Common symptoms

  -----------------------------------------------------------------------
  Status or symptom                   Investigate
  ----------------------------------- -----------------------------------
  `CrashLoopBackOff`                  Startup errors, logs,
                                      configuration, probes, exit reason

  `OOMKilled`                         Memory usage, limit, JVM heap and
                                      native memory

  `ImagePullBackOff`                  Image name/tag, registry access,
                                      pull credentials

  `FailedScheduling`                  CPU/memory availability, selectors,
                                      affinity, taints and tolerations

  Probe failures                      Health endpoint, startup time,
                                      port, path, timeout and thresholds
  -----------------------------------------------------------------------

## 13.3 Example: hostname resolution error

If Spring Boot logs show:

``` text
UnknownHostException: mysql-prod.internal
```

This indicates hostname-resolution failure. Possible causes include:

-   Incorrect hostname.
-   DNS issue.
-   Wrong namespace or Service name.
-   Environment-specific configuration error.

It does not by itself prove that the database server is down.

**L3 approach:** Connect logs, events, configuration, networking, and
database health rather than jumping to a conclusion.

------------------------------------------------------------------------

# 14. Networking and Service discovery

Pod IPs can change when Pods are replaced. Microservices should
generally use stable Service names instead of hardcoding Pod IPs.

``` mermaid
flowchart LR
    O[order-service Pod] -->|http://payment-service:8080| S[payment-service Service]
    S --> P1[payment Pod 1]
    S --> P2[payment Pod 2]
```

## 14.1 Service DNS

Within the same namespace, a client can typically call:

``` text
http://payment-service:8080/payments
```

For a Service in another namespace, a fully qualified name is typically:

``` text
payment-service.prod.svc.cluster.local
```

The cluster DNS domain may differ from `cluster.local`.

## 14.2 What happens behind the scenes?

1.  The client resolves the Service name through cluster DNS.
2.  The Service name resolves to a stable Service address.
3.  Kubernetes networking directs traffic to eligible endpoints for the
    Service.
4.  The request reaches an eligible Pod.

EndpointSlices hold information about endpoints associated with a
Service.

## 14.3 If a Service has no working endpoints

``` bash
kubectl get services -n prod
kubectl get pods -n prod --show-labels
kubectl describe service payment-service -n prod
kubectl get endpointslices -n prod
```

Check:

-   Do Service selectors match Pod labels?
-   Are Pods ready?
-   Is the Service in the expected namespace?
-   Does `targetPort` match the application's listening port?
-   Is DNS resolution working?
-   Are NetworkPolicies or other network controls blocking traffic?

------------------------------------------------------------------------

# 15. RBAC (Role-Based Access Control)

RBAC determines **who can perform which actions on which Kubernetes
resources**.

Example: an L3 support engineer may need to inspect Pods and read logs
in `prod`, but should not have permission to delete production workloads
without authorization.

## 15.1 Concepts

-   **Subject:** User, group, or ServiceAccount receiving permissions.
-   **Role:** Defines allowed actions on resources within a namespace.
-   **RoleBinding:** Grants a Role's permissions to subjects in that
    namespace.
-   **ClusterRole:** Defines permissions that can be used at cluster
    scope or reused through a RoleBinding.
-   **ClusterRoleBinding:** Grants a ClusterRole at cluster scope.

A ClusterRole can also be bound through a RoleBinding to grant its
permissions within a namespace.

## 15.2 Example read-only Role

``` yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: support-readonly
  namespace: prod
rules:
  - apiGroups: [""]
    resources: ["pods"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["pods/log"]
    verbs: ["get"]
```

This Role defines permissions but does not grant them to anyone until a
suitable RoleBinding is created.

## 15.3 Check permissions

``` bash
kubectl auth can-i list pods -n prod
kubectl auth can-i get pods/log -n prod
kubectl auth can-i delete pods -n prod
```

A `Forbidden` error may indicate that the current identity lacks the
required RBAC permission. Confirm the identity, namespace, resource, and
action. Use the approved access-request process instead of trying to
bypass permissions.

------------------------------------------------------------------------

# 16. Storage: Volumes, PersistentVolumes, and PersistentVolumeClaims

Containers and Pods are replaceable. Data stored only in a container's
writable filesystem may not survive container replacement or Pod
recreation.

## 16.1 Volume

A Volume provides storage to containers in a Pod. Its lifecycle and
persistence depend on the volume type.

For example, an `emptyDir` volume survives a container restart while the
Pod remains, but normally disappears when the Pod is removed.

## 16.2 PersistentVolume (PV)

A PersistentVolume represents storage made available to the cluster. It
can be backed by cloud storage, network storage, or another supported
storage system.

## 16.3 PersistentVolumeClaim (PVC)

A PersistentVolumeClaim is a request for storage by an application.

``` mermaid
flowchart TD
    POD[Application Pod] --> PVC[PersistentVolumeClaim]
    PVC --> PV[PersistentVolume]
    PV --> STORAGE[Underlying storage]
```

## 16.4 Example PVC

``` yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: order-data
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
```

This requests 5 GiB. Whether it can be provisioned depends on the
cluster's storage configuration and StorageClasses.

## 16.5 Mount the claim in a Pod

``` yaml
volumes:
  - name: order-data
    persistentVolumeClaim:
      claimName: order-data
```

Mount it inside a container:

``` yaml
volumeMounts:
  - name: order-data
    mountPath: /app/data
```

These fragments must be placed in the correct `spec` sections of a
complete Pod or Deployment manifest.

## 16.6 Troubleshooting storage

``` bash
kubectl get pvc
kubectl describe pvc order-data
kubectl get pv
kubectl describe pod <pod-name>
```

If a Pod remains `Pending`, investigate:

-   An unbound PVC.
-   Unavailable storage.
-   Incompatible access modes.
-   Storage capacity.
-   StorageClass and provisioning configuration.

Persistent storage does not automatically make a database highly
available. Replication, backups, recovery, and data integrity still need
to be designed.

------------------------------------------------------------------------

# 17. Ingress and external access in detail

Imagine a public request:

``` text
https://api.example.com/orders
```

A typical flow might be:

``` mermaid
flowchart TD
    A[Client / browser] --> B[DNS resolves domain]
    B --> C[External load balancer or entry point]
    C --> D[Ingress Controller]
    D --> E[Ingress routing rule]
    E --> F[order-service Service]
    F --> G[Ready order-service Pod]
    G --> H[Spring Boot application]
```

The exact path depends on the cluster, cloud provider, controller, and
networking configuration. Some deployments use a cloud load balancer
directly, while others use an Ingress Controller behind one.

## 17.1 Example Ingress rule

This example is illustrative. An Ingress Controller must be installed
and configured to implement the rule.

``` yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: app-ingress
spec:
  rules:
    - host: api.example.com
      http:
        paths:
          - path: /orders
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 8080
```

## 17.2 External-access troubleshooting

If the application works inside the cluster but not from a browser,
check each layer:

1.  Does DNS resolve to the expected address?
2.  Is the external load balancer or entry point available?
3.  Is the Ingress Controller running?
4.  Is the Ingress rule accepted and pointing to the correct Service?
5.  Does the Service have eligible endpoints?
6.  Are Pod readiness checks passing?
7.  Are TLS certificates, ports, firewalls, and network policies
    correct?

Commands:

``` bash
kubectl get ingress -A
kubectl describe ingress app-ingress
kubectl get services -A
kubectl get pods -A
```

An Ingress object existing in the cluster does not prove that external
traffic can reach the application.

------------------------------------------------------------------------

# 18. Configuration and deployment troubleshooting

This step combines ConfigMap/Secret knowledge with Deployment and
troubleshooting skills.

Suppose a new Spring Boot version deploys successfully as an image, but
the application cannot connect to MySQL. The image can be valid while
the configuration is wrong.

## 18.1 Common scenarios

  -----------------------------------------------------------------------
  Symptom                             What to check
  ----------------------------------- -----------------------------------
  Cannot connect to database          Hostname, port, credentials,
                                      network access, database
                                      availability

  `UnknownHostException`              DNS, Service name, hostname and
                                      environment configuration

  Authentication failure              Username/password source, Secret
                                      key names, credential rotation

  Pod starts with old configuration   Environment variables, ConfigMap
                                      changes, rollout/restart status

  `ImagePullBackOff`                  Image name/tag, registry
                                      permissions, pull Secret

  Deployment is stuck                 Pod events, readiness, capacity,
                                      image pull and startup logs

  Errors after deployment             Logs, probes, configuration
                                      differences, downstream
                                      dependencies
  -----------------------------------------------------------------------

## 18.2 Disciplined troubleshooting workflow

``` mermaid
flowchart TD
    A[Check Deployment and Pod status] --> B[Inspect events and Pod description]
    B --> C[Read current and previous container logs]
    C --> D[Verify image and configuration]
    D --> E[Check Service, DNS and dependencies]
    E --> F[Identify root cause]
    F --> G[Apply controlled fix]
    G --> H[Verify health and business functionality]
```

Commands:

``` bash
kubectl get deployments
kubectl rollout status deployment/order-service
kubectl get pods
kubectl describe pod <pod-name>
kubectl logs <pod-name>
kubectl logs <pod-name> --previous
kubectl get configmap
kubectl get secrets
```

For Secrets, follow access controls and avoid exposing credentials in
terminal output or support tickets.

## 18.3 L3 mindset

Do not immediately restart a Pod just because the application is
failing. Collect evidence, understand the symptom, identify the likely
cause, and choose a controlled corrective action.

A restart can temporarily hide a problem without fixing it.

------------------------------------------------------------------------

# 19. Monitoring and observability

In production support, you need to know not only whether a Pod is
running but also whether the application is healthy, slow, or failing.

Three important observability signals:

-   **Metrics:** Numerical measurements over time, such as CPU, memory,
    request rate, latency, and error rate.
-   **Logs:** Events and messages from the application and
    infrastructure.
-   **Traces:** The path of a request through multiple services.

## 19.1 Example: API latency rises

Suppose a Spring Boot API normally responds in under two seconds but now
takes ten seconds.

``` mermaid
flowchart TD
    A[API latency increases] --> B[Check request rate, errors and latency metrics]
    B --> C[Check Pod CPU and memory]
    C --> D[Review application logs]
    D --> E[Inspect database and downstream calls]
    E --> F[Use traces to identify slow operation]
    F --> G[Confirm root cause and verify recovery]
```

## 19.2 Tools you may encounter

  Tool                  Typical use
  --------------------- ----------------------------------------
  Prometheus            Collects and stores metrics
  Grafana               Visualizes metrics and dashboards
  ELK / Elastic Stack   Collects, searches and analyzes logs
  Kibana                Explores and visualizes Elastic data
  Splunk                Log search, investigation and analysis
  Datadog               Monitoring and observability
  New Relic             Application performance monitoring
  AppDynamics           Application performance monitoring

Exact tools depend on the organization.

## 19.3 Kubernetes commands

``` bash
kubectl get pods
kubectl top pods
kubectl top nodes
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

`kubectl top` requires metrics support. These commands do not replace
application dashboards, distributed tracing, or database monitoring.

## 19.4 Signals to investigate

-   Increased request latency.
-   Rising error rates.
-   CPU throttling.
-   Memory growth or `OOMKilled`.
-   Frequent container restarts.
-   Failed health probes.
-   Database connection pool exhaustion.
-   Slow SQL queries.
-   Downstream service timeouts.

A Pod can be `Running` while the application is unhealthy or performing
poorly. Pod status is only one part of the investigation.

------------------------------------------------------------------------

# 20. End-to-end L3 production incident practice

The objective is to investigate incidents systematically rather than
memorizing isolated commands.

## Scenario 1: Spring Boot Pod is in `CrashLoopBackOff`

Start with:

``` bash
kubectl get pods -n prod
kubectl describe pod <pod-name> -n prod
kubectl logs <pod-name> -n prod
kubectl logs <pod-name> --previous -n prod
```

Investigate:

-   Did the application fail during startup?
-   Is a required environment variable missing?
-   Is the database hostname or credential wrong?
-   Is the application failing a health probe?
-   Was the container terminated for exceeding its memory limit?

**Goal:** Find the cause in logs, events, or configuration before
choosing a fix.

## Scenario 2: A Service cannot reach a microservice

Example: `order-service` cannot call `payment-service`.

``` bash
kubectl get services -n prod
kubectl get pods -n prod --show-labels
kubectl describe service payment-service -n prod
kubectl get endpointslices -n prod
```

Investigate:

-   Is the Service name correct?
-   Are the Pods in the expected namespace?
-   Does the Service selector match Pod labels?
-   Are the target Pods ready?
-   Is the correct port configured?
-   Is DNS resolution working?
-   Are network policies blocking communication?

**Goal:** Determine whether the failure is in discovery, Service
selection, Pod readiness, ports, or network connectivity.

## Scenario 3: The application is slow

Suppose the API takes ten seconds instead of two.

1.  Confirm latency and error rate in monitoring.
2.  Check CPU, memory, restarts, and throttling.
3.  Review logs around affected requests.
4.  Examine database query performance and connection-pool health.
5.  Check latency and errors in downstream services.
6.  Use distributed traces, if available, to locate the slow operation.
7.  Confirm the root cause and verify the fix.

Do not assume Kubernetes is responsible just because the application
runs on Kubernetes. The bottleneck may be in Java code, SQL, networking,
a downstream service, or infrastructure.

## Scenario 4: A new deployment fails

``` bash
kubectl rollout status deployment/order-service
kubectl rollout history deployment/order-service
kubectl get pods
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

Investigate image availability, configuration, readiness probes,
resource requests, application startup, and dependency access.

If rollback is the safest approved recovery action:

``` bash
kubectl rollout undo deployment/order-service
```

Verify that the application is healthy and user impact has stopped. A
Deployment rollback does not reverse database migrations.

## Scenario 5: An engineer gets `Forbidden`

``` bash
kubectl auth can-i list pods -n prod
kubectl auth can-i get pods/log -n prod
```

Confirm the current identity, namespace, and required permission.
Request appropriate access through the approved process rather than
bypassing RBAC.

## 20.1 L3 incident checklist

-   [ ] Confirm incident scope, affected environment, and user impact.
-   [ ] Check Deployment, Pod, Service, and namespace status.
-   [ ] Inspect events and current/previous application logs.
-   [ ] Check configuration, image version, health probes, and resource
    usage.
-   [ ] Trace network, DNS, database, and downstream dependencies.
-   [ ] Identify the likely root cause using evidence.
-   [ ] Apply an approved, controlled remediation.
-   [ ] Verify application health, API behavior, and monitoring.
-   [ ] Document root cause, corrective action, and prevention plan.

------------------------------------------------------------------------

# 21. Complete deployment flow

This diagram ties the major topics together.

``` mermaid
flowchart TD
    A[Java / Spring Boot source] --> B[Maven build and tests]
    B --> C[Git repository]
    C --> D[Jenkins CI/CD pipeline]
    D --> E[Build Docker image]
    E --> F[Push image to registry]
    F --> G[Kubernetes Deployment]
    G --> H[ReplicaSet]
    H --> I[Pods]
    I --> J[Containers running Spring Boot]
    K[ConfigMap and Secret] --> J
    L[PVC / storage] --> I
    M[Startup / readiness / liveness probes] --> I
    I --> N[Service]
    N --> O[Ingress / external entry point]
    I --> P[Metrics, logs and traces]
    P --> Q[Prometheus / Grafana / logging tools]
    Q --> R[L3 investigation and recovery]
```

## End-to-end explanation

1.  A developer writes Java/Spring Boot code.
2.  Maven builds and tests the application.
3.  Code is stored in Git.
4.  Jenkins or another CI/CD system runs build and test stages.
5.  A container image is built and pushed to a registry.
6.  A Kubernetes Deployment declares the desired image and replicas.
7.  A ReplicaSet manages the desired number of Pods.
8.  Pods run containers on worker nodes.
9.  ConfigMaps and Secrets provide environment-specific configuration.
10. PVCs provide persistent storage where required.
11. Probes check startup, readiness, and liveness.
12. A Service provides a stable endpoint for eligible Pods.
13. An Ingress or other external entry point may route client traffic.
14. Metrics, logs, and traces help operators detect and diagnose issues.
15. L3 support identifies the root cause, applies a controlled fix, and
    verifies recovery.

------------------------------------------------------------------------

# 22. Command cheat sheet

## Cluster and resources

``` bash
kubectl get nodes
kubectl get namespaces
kubectl get pods
kubectl get pods -A
kubectl get deployments
kubectl get replicasets
kubectl get services
kubectl get ingress -A
```

## Namespace-specific inspection

``` bash
kubectl get pods -n prod
kubectl get services -n prod
kubectl describe pod <pod-name> -n prod
kubectl logs <pod-name> -n prod
```

## Logs and events

``` bash
kubectl logs <pod-name>
kubectl logs <pod-name> --previous
kubectl logs <pod-name> -c <container-name>
kubectl get events --sort-by=.metadata.creationTimestamp
```

## Rollouts

``` bash
kubectl rollout status deployment/order-service
kubectl rollout history deployment/order-service
kubectl rollout undo deployment/order-service
kubectl set image deployment/order-service order-service=your-registry/order-service:1.1
```

## Resource usage

``` bash
kubectl top pod <pod-name>
kubectl top pods
kubectl top nodes
kubectl describe pod <pod-name>
```

## Service discovery and storage

``` bash
kubectl get endpointslices -n prod
kubectl get pvc
kubectl describe pvc <pvc-name>
kubectl get pv
```

## RBAC

``` bash
kubectl auth can-i list pods -n prod
kubectl auth can-i get pods/log -n prod
kubectl auth can-i delete pods -n prod
```

## Context and namespace

``` bash
kubectl config get-contexts
kubectl config current-context
kubectl config set-context --current --namespace=prod
```

**Operational caution:** Run read-only inspection commands first. Before
applying changes or rolling back a production deployment, verify the
target cluster/context and follow the organization's change-control
process.

------------------------------------------------------------------------

# 23. Important distinctions to remember

  -----------------------------------------------------------------------
  Concepts                            Difference
  ----------------------------------- -----------------------------------
  Image vs container                  Image is the packaged template;
                                      container is a running instance

  Container vs Pod                    Container runs the process; Pod is
                                      Kubernetes' smallest deployable
                                      unit and may contain multiple
                                      containers

  Pod vs node                         A Pod is a workload unit; a node is
                                      a machine that can host multiple
                                      Pods

  Control plane vs worker node        Control plane manages cluster
                                      state; worker nodes run workloads

  Deployment vs ReplicaSet            Deployment manages rollout and
                                      desired application state;
                                      ReplicaSet maintains the required
                                      Pod count

  Deployment vs Service               Deployment manages Pods; Service
                                      provides a stable network endpoint

  ClusterIP vs NodePort               ClusterIP is internal; NodePort
                                      exposes a port on nodes

  LoadBalancer vs Ingress             LoadBalancer exposes a Service
                                      through an external load balancer;
                                      Ingress defines HTTP/HTTPS routing
                                      rules implemented by a controller

  ConfigMap vs Secret                 ConfigMap is for non-sensitive
                                      settings; Secret is for sensitive
                                      values and needs proper security
                                      controls

  Startup vs readiness vs liveness    Startup checks initialization;
                                      readiness controls traffic
                                      eligibility; liveness can trigger a
                                      container restart

  Request vs limit                    Request influences scheduling and
                                      resource allocation; limit
                                      constrains usage

  Namespace vs cluster                Namespace logically groups
                                      resources inside a cluster

  PV vs PVC                           PV represents storage; PVC requests
                                      storage

  Metrics vs logs vs traces           Metrics quantify behavior; logs
                                      record events; traces show request
                                      paths

  Running vs healthy                  A Pod can be running while its
                                      application is unhealthy or slow

  Restart vs root-cause fix           A restart may restore service
                                      temporarily but does not
                                      necessarily fix the cause
  -----------------------------------------------------------------------

------------------------------------------------------------------------

# 24. Quiz recap and self-test

## 24.1 Step 7: Service exposure

1.  Which Service type is commonly used for internal microservice
    communication?
    -   A. ClusterIP
    -   B. NodePort
    -   C. Ingress
2.  Which Service type exposes a port across cluster nodes?
    -   A. ClusterIP
    -   B. NodePort
    -   C. ConfigMap
3.  What defines HTTP/HTTPS routing rules?
    -   A. Ingress
    -   B. ReplicaSet
    -   C. Secret
4.  Does creating an Ingress resource alone guarantee public access?
    -   A. Yes
    -   B. No

**Answers:** 1-A, 2-B, 3-A, 4-B.

## 24.2 Step 8: Configuration

1.  Which resource is intended for non-sensitive configuration?
    -   A. ConfigMap
    -   B. Secret
2.  Does changing a ConfigMap automatically change an environment
    variable inside an already-running process?
    -   A. Yes
    -   B. No
3.  Is Base64 encoding equivalent to encryption?
    -   A. Yes
    -   B. No

**Answers:** 1-A, 2-B, 3-B.

## 24.3 Step 9: Health probes

1.  Which probe can trigger a container restart after repeated failure?
    -   A. Readiness
    -   B. Liveness
    -   C. Neither
2.  What is the primary purpose of readiness?
    -   A. Decide whether the Pod should receive normal Service traffic
    -   B. Increase replica count
3.  What is the purpose of startup probe?
    -   A. Give a slow-starting application time to initialize
    -   B. Store credentials

**Answers:** 1-B, 2-A, 3-A.

## 24.4 Step 10: Resources

1.  What can happen when a container reaches its CPU limit?
    -   A. CPU throttling
    -   B. Automatic permanent deletion of the Deployment
2.  What can happen when a container exceeds its memory limit?
    -   A. It is guaranteed to keep running
    -   B. It can be terminated, often with `OOMKilled`
3.  Is a memory request a hard memory cap?
    -   A. Yes
    -   B. No

**Answers:** 1-A, 2-B, 3-B.

## 24.5 Step 11: Namespaces

1.  Can two namespaces contain resources with the same name?
    -   A. Yes
    -   B. No
2.  Does a namespace represent a separate physical cluster?
    -   A. Yes
    -   B. No
3.  Does creating a namespace automatically isolate all network traffic?
    -   A. Yes
    -   B. No

**Answers:** 1-A, 2-B, 3-B.

## 24.6 Step 12: Rollouts

1.  Which command checks rollout progress?
    -   A. `kubectl rollout status deployment/order-service`
    -   B. `kubectl get namespaces`
2.  Which command rolls back a Deployment?
    -   A. `kubectl rollout undo deployment/order-service`
    -   B. `kubectl get nodes`
3.  Does a Deployment rollback automatically reverse a database
    migration?
    -   A. Yes
    -   B. No

**Answers:** 1-A, 2-A, 3-B.

## 24.7 Step 13: Logs

1.  Which command shows logs from the previous container instance?
    -   A. `kubectl logs <pod-name> --previous`
    -   B. `kubectl get nodes`
2.  Which command helps inspect Pod events and state?
    -   A. `kubectl describe pod <pod-name>`
    -   B. `kubectl rollout undo`
3.  Does `UnknownHostException` alone prove that the database server is
    down?
    -   A. Yes
    -   B. No

**Answers:** 1-A, 2-A, 3-B.

## 24.8 Step 14: Service discovery

1.  Why use a Service name instead of a Pod IP?
    -   A. Pod IPs can change
    -   B. Pod IPs never change
2.  What should you check if a Service has no endpoints?
    -   A. Pod readiness and selector/label matching
    -   B. Only the Dockerfile
3.  Can a cross-namespace Service be addressed with a DNS name including
    its namespace?
    -   A. Yes
    -   B. No

**Answers:** 1-A, 2-A, 3-A.

## 24.9 Step 15: RBAC

1.  What does a Role define?
    -   A. Allowed actions on resources within a namespace
    -   B. Number of replicas
    -   C. Pod IP address
2.  What does a RoleBinding do?
    -   A. Connects a Role to subjects and grants permissions
    -   B. Connects a Pod to a node
    -   C. Restarts a container
3.  An L3 engineer receives `Forbidden` when listing Pods. What should
    they check?
    -   A. Image history
    -   B. Whether their identity has the required RBAC permission
    -   C. Public IP address
4.  Does creating a Role automatically grant permissions to every user?
    -   A. Yes
    -   B. No, a suitable binding is required

**Answers:** 1-A, 2-A, 3-B, 4-B.

## 24.10 Additional self-test

Try answering these without looking at the earlier sections:

1.  What is the difference between a Pod and a worker node?
2.  Why is a Deployment usually preferred over manually creating Pods?
3.  Why is a Service useful when Pod IPs change?
4.  What is the difference between a readiness failure and a liveness
    failure?
5.  What is the difference between CPU throttling and `OOMKilled`?
6.  Why can a Pod be `Running` but the application still be unhealthy?
7.  What does `ImagePullBackOff` suggest?
8.  What would you check if a PVC is not bound?
9.  What does an `UnknownHostException` suggest?
10. How would you investigate an API that becomes much slower after a
    deployment?
11. Why does a ConfigMap environment-variable change usually require a
    restart or rollout?
12. Why is creating a Role not sufficient to grant permissions?
13. Why does a successful image deployment not guarantee that the
    application can connect to its database?
14. Why should a rollback be followed by verification?
15. Why should you confirm the Kubernetes context before running
    production commands?

------------------------------------------------------------------------

# 25. Further topics after the first 20 steps

The first 20 steps form the initial learning path. After becoming
comfortable with them, continue with:

-   **Helm:** Packaging and templating Kubernetes resources.
-   **Horizontal Pod Autoscaler (HPA):** Adjusting replica counts based
    on metrics.
-   **Node failures and scheduling:** Understanding placement
    constraints, affinity, taints, and tolerations.
-   **NetworkPolicies:** Controlling which network traffic Pods may
    receive or send, when supported by the cluster networking
    implementation.
-   **Kubernetes on AWS / EKS:** Understanding managed control planes,
    worker capacity, cloud load balancers, storage integration, and AWS
    identity/access integration.
-   **More advanced production support:** Resource pressure, DNS
    incidents, rollout failures, application performance analysis, and
    incident postmortems.

These are follow-on topics, not extra steps silently inserted into the
original 20-step sequence.

------------------------------------------------------------------------

## Final mental model

``` text
Source code
   ↓
CI/CD builds and tests
   ↓
Container image
   ↓
Kubernetes Deployment
   ↓
ReplicaSet
   ↓
Pods on worker nodes
   ↓
Containers running the application
   ├── Configuration: ConfigMaps and Secrets
   ├── Storage: Volumes / PV / PVC
   ├── Health: startup / readiness / liveness
   └── Resources: CPU and memory requests/limits
   ↓
Service discovery and routing
   ↓
Client traffic
   ↓
Metrics, logs, traces
   ↓
L3 diagnosis, remediation, verification and prevention
```

The central production-support habit is: **observe first, gather
evidence, isolate the failing layer, identify the root cause, make a
controlled change, and verify recovery.**
