# AWS Complete Guide — From Zero to Production

## Let's Start With an Analogy

Think of AWS as a **huge shopping mall** where you rent different shops/services instead of building your own building.

```
Your Spring Boot App = Your Business
AWS = The Mall (infrastructure provider)
You don't build the mall, you just rent what you need.
```

## The Big Picture — How Your App Gets to Users

```
Developer writes code
        |
        ▼
Push to Github/CodeCommit
        |
        ▼
Jenkins/CI-CD picks it up
        |
        ▼
Builds JAR/Docker image
        |
        ▼
Deploys to AWS (EC2/ECS/EKS)
        |
        ▼
Users access via Internet
```

## 1. REGION & AVAILABILITY ZONE (The Foundation)

### Analogy:
- **Region** = A city (Mumbai, Virginia, London)
- **Availability Zone (AZ)** = Different buildings in that city

```
AWS Region: ap-south-1 (Mumbai)
  ├── AZ: ap-south-1a  (Data Center Building 1)
  ├── AZ: ap-south-1b  (Data Center Building 2)
  └── AZ: ap-south-1c  (Data Center Building 3)
```

**Why multiple AZs?** If one building catches fire, your app still runs in another building.

## 2. VPC (Virtual Private Cloud) — Your Private Network

### Analogy:
VPC = Your own **gated apartment complex** inside the city. No one can enter without permission.

```
VPC (10.0.0.0/16)
Your private network in AWS
┌─────────────────────────────┐
│ Public Subnet                │
│ (10.0.1.0/24)                 │
│ - Load Balancer               │
│ - NAT Gateway                 │
├─────────────────────────────┤
│ Private Subnet               │
│ (10.0.2.0/24)                 │
│ - Your App                    │
│ - Database                    │
└─────────────────────────────┘
```

### What's inside VPC:

| Component | Analogy | Purpose |
|---|---|---|
| Public Subnet | Front gate of apartment | Things that face the internet |
| Private Subnet | Inside rooms | Things hidden from internet (your app, DB) |
| Internet Gateway | Main gate | Allows internet traffic in/out |
| NAT Gateway | Security guard who goes out to buy things | Lets private subnet access internet (for updates) but no one can come in |
| Route Table | Direction board | Tells traffic where to go |
| Security Group | Building security | Firewall rules (allow port 8080, block others) |
| NACL | Door lock | Network-level firewall |

### Security Group Example:

```
Inbound Rules:
Protocol | Port | Source
TCP      | 80   | 0.0.0.0/0      ← Allow HTTP from anywhere
TCP      | 443  | 0.0.0.0/0      ← Allow HTTPS from anywhere
TCP      | 22   | My IP only     ← SSH only from my computer
TCP      | 8080 | ALB only       ← App port only from Load Balancer
```

## 3. EC2 (Elastic Compute Cloud) — Your Server

### Analogy:
EC2 = A **rented computer** in the cloud. Just like your laptop, but in AWS data center.

```
┌─────────────────────────┐
│       EC2 Instance        │
│                            │
│ OS: Amazon Linux / Ubuntu │
│ CPU: 2 cores               │
│ RAM: 4 GB                  │
│ Storage: 20 GB (EBS)      │
│                            │
│ Running: java -jar myapp.jar │
└─────────────────────────┘
```

### Key Concepts:

| Concept | Meaning |
|---|---|
| Instance Type | Size of computer (t2.micro = small, m5.large = big) |
| AMI | Template/image to create EC2 (like Windows ISO) |
| Key Pair | SSH key to login to your server |
| EBS | Hard disk attached to EC2 |
| Elastic IP | Fixed IP address (otherwise IP changes on restart) |

### How your Spring Boot app runs on EC2:

```
# SSH into EC2
ssh -i mykey.pem ec2-user@<public-ip>

# Install Java
sudo yum install java-17

# Copy your JAR
scp myapp.jar ec2-user@<ip>:/home/ec2-user/

# Run it
java -jar myapp.jar --server.port=8080
```

## 4. LOAD BALANCER (ALB) — Traffic Distributor

### Analogy:
Like a **receptionist** at a hospital who sends patients to different doctors so no single doctor is overwhelmed.

```
1000 Users
    |
    ▼
┌───────────────┐
│ Load Balancer  │
└───────────────┘
   |      |      |
   ▼      ▼      ▼
 EC2-1  EC2-2  EC2-3
```

### Types:

| Type | Use |
|---|---|
| ALB (Application) | HTTP/HTTPS traffic (your REST APIs) |
| NLB (Network) | Ultra-fast TCP traffic (gaming, IoT) |
| CLB (Classic) | Old, don't use |

### Health Checks:
ALB pings `/health` endpoint every 30 seconds. If EC2 doesn't respond → removes it from rotation.

```java
// Your Spring Boot health endpoint
@GetMapping("/health")
public String health() {
    return "OK";
}
```

## 5. AUTO SCALING GROUP — Automatic Scale Up/Down

### Analogy:
Like a **restaurant** that opens more tables when it's crowded, removes tables when empty.

```
Low Traffic (2 AM):           High Traffic (10 AM):
┌───────┐                     ┌───────┐ ┌───────┐ ┌───────┐ ┌───────┐
│ EC2-1 │                     │ EC2-1 │ │ EC2-2 │ │ EC2-3 │ │ EC2-4 │
└───────┘                     └───────┘ └───────┘ └───────┘ └───────┘
Min: 1                        Max: 6
```

### Rules:

```
IF CPU > 70% for 5 min → Add 2 more EC2
IF CPU < 30% for 10 min → Remove 1 EC2
Min instances: 2 (always running)
Max instances: 6 (cost control)
```

## 6. ROUTE 53 — DNS (Domain Name System)

### Analogy:
Like a **phone book**. You say "api.myapp.com" → it gives the IP address of your Load Balancer.

```
User types: www.myapp.com
        |
        ▼
┌──────────┐
│ Route 53  │  → "Oh, myapp.com? Go to ALB at 54.23.xx.xx"
└──────────┘
        |
        ▼
   Load Balancer
```

## 7. S3 (Simple Storage Service) — File Storage

### Analogy:
S3 = **Google Drive** but for your application. Store files, images, videos, backups.

```
S3 Bucket: my-app-files
  ├── /images/profile-pic-1.jpg
  ├── /videos/intro.mp4
  ├── /backups/db-backup-2024.sql
  └── /logs/app-log-jan.txt
```

### Key Points:
- Unlimited storage
- Each file can be up to 5 TB
- 99.999999999% durability (11 nines — basically never loses your file)
- Can make files public (website hosting) or private

### From Spring Boot:

```java
@Autowired
private AmazonS3 s3Client;

public String uploadFile(MultipartFile file) {
    s3Client.putObject("my-bucket", file.getOriginalFilename(),
        file.getInputStream(), new ObjectMetadata());
    return "uploaded";
}
```

## 8. RDS (Relational Database Service) — Managed Database

### Analogy:
Instead of installing MySQL on your laptop and managing it, AWS does it for you. Like hiring a **DBA (Database Admin)** who handles backups, updates, scaling.

```
┌──────────────────────────────────────┐
│              RDS (Managed DB)         │
│                                        │
│ Engine: MySQL / PostgreSQL            │
│ Auto backups: Every day               │
│ Multi-AZ: Yes (failover ready)        │
│ Storage: Auto-scales                  │
│                                        │
│ Endpoint: mydb.abc123.rds.amazonaws.com │
└──────────────────────────────────────┘
```

### Your Spring Boot connects to it:

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://mydb.abc123.rds.amazonaws.com:3306/mydb
    username: admin
    password: ${DB_PASSWORD}  # from environment variable
```

### RDS vs DynamoDB:

| | RDS | DynamoDB |
|---|---|---|
| Type | SQL (tables, joins) | NoSQL (key-value) |
| Use | Complex queries, relationships | Simple fast lookups |
| Example | User orders with joins | Session storage, cache |

## 9. DynamoDB — NoSQL Database

### Analogy:
Like a **giant dictionary/HashMap**. You give a key → you get value. Super fast.

```
Table: Users
┌───────────────┬──────────────────────────┐
│ userId (Key)   │ data                      │
├───────────────┼──────────────────────────┤
│ user-001       │ {name: "Hari", age: 25}   │
│ user-002       │ {name: "John", age: 30}   │
└───────────────┴──────────────────────────┘
```

- Single-digit millisecond response
- Auto-scales
- No server to manage

## 10. IAM (Identity & Access Management) — Security

### Analogy:
IAM = ID cards and permissions in an office.
- Who are you? (Authentication)
- What can you do? (Authorization)

```
┌──────────────────────────────────────┐
│                  IAM                  │
│                                        │
│ Users    → Humans (developers, admins) │
│ Roles    → Permissions for services    │
│ Policies → Rules (allow/deny actions)  │
│ Groups   → Collection of users         │
└──────────────────────────────────────┘
```

### Example Policy:

```json
{
  "Effect": "Allow",
  "Action": "s3:GetObject",
  "Resource": "arn:aws:s3:::my-bucket/*"
}
```

This says "You CAN read files from my-bucket, but NOTHING else."

### IAM Role for EC2:
Your EC2 needs to access S3? Give it a **Role** (like giving an employee a special badge).

```
EC2 Instance --has role--> "S3ReadRole" --allows--> Read from S3
```

No hardcoded credentials needed!

## 11. CI/CD PIPELINE — Code to Production

### Analogy:
Like a **factory assembly line**. Code goes in → tested → packaged → deployed. Automatically.

```
┌───────┐    ┌───────┐    ┌───────┐    ┌────────┐
│ Code   │ → │ Build  │ → │ Test   │ → │ Deploy  │
│ (Git)  │    │(Maven) │    │(JUnit) │    │ (AWS)   │
└───────┘    └───────┘    └───────┘    └────────┘
```

### Using Jenkins:

```groovy
// Jenkinsfile
pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/you/your-app.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker build -t my-app .'
            }
        }
        stage('Push to ECR') {
            steps {
                sh 'docker push <aws-account>.ecr.region.amazonaws.com/my-app'
            }
        }
        stage('Deploy to ECS') {
            steps {
                sh 'aws ecs update-service --cluster my-cluster --service my-service'
            }
        }
    }
}
```

### AWS Native CI/CD (Alternative to Jenkins):

```
CodeCommit (Github alternative)
        |
        ▼
CodeBuild (builds & tests your code)
        |
        ▼
CodeDeploy (deploys to EC2/ECS)
        |
        ▼
CodePipeline (orchestrates all above)
```

## 12. ECR & ECS & EKS — Container Services

### Analogy:
- **Docker Image** = A packed suitcase with everything your app needs
- **ECR** = Wardrobe where you store packed suitcases
- **ECS** = Someone who opens suitcases and runs your app
- **EKS** = Same but using Kubernetes (more powerful, complex)

```
Dockerfile → Docker Image → Push to ECR → Run on ECS
```

### Dockerfile for Spring Boot:

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/myapp.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### ECS vs EKS:

| | ECS | EKS |
|---|---|---|
| Managed by | AWS | Kubernetes |
| Complexity | Simple | Complex |
| Use when | Small-medium apps | Large microservices |

## 13. API GATEWAY — Front Door for APIs

### Analogy:
Like a **security guard + receptionist** at a building entrance.
- Checks if you have permission (authentication)
- Rate limiting (only 100 requests/min)
- Routes you to correct floor (microservice)

```
Client
  |
  ▼
┌────────────────┐
│  API Gateway     │  ← authentication, rate limiting, routing
└────────────────┘
   |          |
   ▼          ▼
Service1    Service2
(users)     (orders)
```

### Features:
- Authentication (API keys, JWT, Cognito)
- Rate limiting (throttle abusers)
- Request/Response transformation
- Caching
- Logging

## 14. CloudWatch — Monitoring & Logs

### Analogy:
Like **CCTV cameras + alarm system** for your entire infrastructure.

```
┌───────────────────────────────────┐
│             CloudWatch              │
│                                      │
│ Metrics: CPU, Memory, Requests      │
│ Logs: Application logs              │
│ Alarms: Alert if CPU > 80%          │
│ Dashboards: Visual graphs           │
└───────────────────────────────────┘
```

### Your Spring Boot logs go here:

```yaml
# application.yml
logging:
  level:
    root: INFO
  file:
    path: /var/log/myapp
```

CloudWatch Agent on EC2 pushes these logs to CloudWatch.

### Alarms:

```
IF EC2 CPU > 80% for 5 minutes → Send email to you
IF API 5xx errors > 10/min → Trigger alert
IF DynamoDB read throttles → Scale up
```

## 15. SQS & SNS — Messaging

### Analogy:
- **SQS (Simple Queue Service)** = A letter box. Messages wait in line until someone reads them.
- **SNS (Simple Notification Service)** = A loudspeaker. Broadcasts message to many listeners.

```
SQS (Queue — one to one):
Producer → [msg1, msg2, msg3] → Consumer picks one by one

SNS (Topic — one to many):
Publisher → Topic → Subscriber1 (email)
                  → Subscriber2 (SQS)
                  → Subscriber3 (Lambda)
```

### Use Case:

```
User places order
        |
        ▼
Order Service → publishes to SQS
        |
        ▼
Payment Service (picks from queue, processes)
        |
        ▼
Email Service (sends confirmation)
```

### From Spring Boot:

```java
@Autowired
private AmazonSQS sqsClient;

// Send message
sqsClient.sendMessage("queue-url", "Order #123 placed");

// Receive message
List<Message> messages = sqsClient.receiveMessage("queue-url").getMessages();
```

## 16. ElastiCache — In-Memory Cache

### Analogy:
Like keeping frequently used phone numbers on a **sticky note** instead of opening the phone book every time.

```
Without Cache:
User → App → Database (slow, 50ms)

With Cache:
User → App → ElastiCache (fast, 1ms) → Cache miss? → Database
```

### Types:
- **Redis** — Rich data structures, pub/sub, persistence
- **Memcached** — Simple key-value, multi-threaded

### From Spring Boot:

```java
@Cacheable("users")
public User getUserById(String id) {
    return userRepository.findById(id); // only hits DB on cache miss
}
```

## 17. CloudFront — CDN (Content Delivery Network)

### Analogy:
Like local **warehouses** of Amazon. Instead of shipping from one central place, they store products in warehouses near you for faster delivery.

```
Without CDN:
User in India → fetches image from US server (slow, 200ms)

With CloudFront:
User in India → fetches from Mumbai edge location (fast, 10ms)
```

```
┌──────────────────────────────────────┐
│           CloudFront (CDN)            │
│                                        │
│ Edge Locations worldwide:             │
│ Mumbai, Singapore, London, Virginia... │
│                                        │
│ Caches: images, CSS, JS, API responses │
└──────────────────────────────────────┘
```

## 18. Secrets Manager / Parameter Store

### Analogy:
Like a **safe/locker** for your passwords and API keys.

### NEVER hardcode secrets in code:

```java
// ❌ BAD
String dbPassword = "mypassword123";

// ✅ GOOD - fetch from Secrets Manager
String dbPassword = secretsManager.getSecretValue("prod/db/password");
```

## 19. Lambda — Serverless Compute

### Analogy:
Like an **Uber driver**. You don't own the car (server). You just call when needed, use it, and pay per ride (execution).

```
No EC2 to manage
No scaling to configure
Just write code → AWS runs it when triggered

Trigger → Lambda Function → Response
(API call, S3 upload, SQS message)
```

```java
public class MyHandler implements RequestHandler<Map, String> {
    @Override
    public String handleRequest(Map input, Context context) {
        return "Hello from Lambda!";
    }
}
```

## COMPLETE ARCHITECTURE — Everything Together

Pay only when code runs (per millisecond).

```
Users
  |
  ▼
┌──────────┐
│ Route 53  │ (DNS)
└──────────┘
  |
  ▼
┌────────────┐
│ CloudFront  │ (CDN - static files)
└────────────┘
  |
  ▼
┌────────────┐
│ API Gateway │ (Auth, Rate limit)
└────────────┘
  |
  ▼
┌────────────┐
│    ALB      │ (Load Balancer)
└────────────┘
   |                    |
   ▼                    ▼
┌─────────┐        ┌─────────┐
│ EC2/ECS  │        │ EC2/ECS  │  (Your Spring Boot)
│ AZ-1a    │        │ AZ-1b    │
└─────────┘        └─────────┘
   |                    |
   ▼                    ▼
┌────────┐  ┌────────┐  ┌─────────────┐  ┌──────────┐
│  RDS    │  │   S3    │  │ ElastiCache │  │ DynamoDB │
│ (MySQL) │  │ (Files) │  │   (Redis)   │  │ (NoSQL)  │
└────────┘  └────────┘  └─────────────┘  └──────────┘
                  |
                  ▼
           ┌────────────┐
           │ CloudWatch  │ (Monitoring & Logs)
           └────────────┘
```

## Flow of a Single Request:

1. User hits: https://api.myapp.com/users/1
2. Route 53 → resolves to CloudFront/ALB IP
3. ALB → picks healthy EC2 in any AZ
4. EC2 (Spring Boot) →
   - Checks ElastiCache (Redis) first
   - Cache HIT? → return cached user
   - Cache MISS? → query RDS → store in cache → return
5. Response travels back to user
6. CloudWatch logs the entire request

## Summary Table

| Service | Category | One-line Purpose |
|---|---|---|
| VPC | Networking | Your private network |
| EC2 | Compute | Virtual server |
| ALB | Networking | Distributes traffic |
| Auto Scaling | Compute | Add/remove servers automatically |
| Route 53 | DNS | Domain to IP mapping |
| S3 | Storage | Store files |
| RDS | Database | Managed SQL database |
| DynamoDB | Database | Fast NoSQL database |
| ElastiCache | Cache | In-memory fast lookups |
| IAM | Security | Who can do what |
| CloudWatch | Monitoring | Logs, metrics, alarms |
| API Gateway | Networking | API front door |
| SQS/SNS | Messaging | Async communication |
| ECR/ECS | Containers | Run Docker containers |
| Lambda | Compute | Serverless functions |
| CloudFront | CDN | Fast content delivery |
| Secrets Manager | Security | Store passwords safely |
| CodePipeline | CI/CD | Automate deployments |

## 20. Terraform — Infrastructure as Code (IaC)

### Analogy:
Imagine building a house. You can either:
- **Manually** place each brick (clicking in AWS Console) — slow, error-prone, not repeatable
- Give a **blueprint** to a contractor who builds it exactly as specified — that's **Terraform**

```
Without Terraform:
Developer → logs into AWS Console → clicks 50 buttons → creates VPC, EC2, RDS...
Problem: "Hey, create same setup for staging" → click 50 buttons AGAIN

With Terraform:
Developer → writes .tf file → runs "terraform apply" → everything created
Need staging? → same file, different variables → done
```

### What Terraform Does:

```
You write:                          Terraform creates:
main.tf          --- apply --->     VPC, EC2, RDS, ALB
(blueprint)                          S3, IAM, SQS...
```

### Basic Example:

```hcl
# Create a VPC
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
}

# Create an EC2 instance
resource "aws_instance" "app_server" {
  ami           = "ami-0abcdef1234567890"
  instance_type = "t2.micro"

  tags = {
    Name = "MySpringBootApp"
  }
}

# Create RDS
resource "aws_db_instance" "mydb" {
  engine         = "mysql"
  instance_class = "db.t3.micro"
  username       = "admin"
  password       = var.db_password
  multi_az       = true
}
```

### Terraform Workflow:

```
terraform init     → downloads AWS provider plugins
        |
        ▼
terraform plan      → shows what WILL be created (dry run)
        |
        ▼
terraform apply     → actually creates resources in AWS
        |
        ▼
terraform destroy   → tears down everything (cleanup)
```

### Where It Fits in CI/CD:

```
Developer pushes code
  ├── App Code → Jenkins → Build JAR → Deploy to EC2/ECS
  └── Infra Code (.tf files) → Terraform → Creates/Updates AWS resources
```

### Terraform vs AWS CloudFormation:

| | Terraform | CloudFormation |
|---|---|---|
| By | HashiCorp (third-party) | AWS (native) |
| Language | HCL (.tf files) | YAML/JSON |
| Multi-cloud | ✅ AWS, Azure, GCP | ❌ AWS only |
| State | Stored in file/S3 | Managed by AWS |
| Industry use | More popular | AWS-specific teams |

### Key Concept — State File:

Terraform keeps a **state file** (terraform.tfstate) that tracks what it created. It compares this with your .tf files to know what to add/remove/change.

```
Your .tf file says: 3 EC2 instances
State file says: 2 EC2 instances exist
Terraform: "I need to create 1 more"
```

### Why Terraform Matters:
- **Repeatable** — same infra in dev, staging, prod
- **Version controlled** — infra changes tracked in Git
- **Reviewable** — team can review infra changes like code (PR)
- **Destroyable** — spin up for testing, destroy after (save cost)
