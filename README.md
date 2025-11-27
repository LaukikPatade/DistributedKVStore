# DistributedKVStore

A lightweight, Dynamo-style **distributed key–value database** implemented in Java.
It supports **consistent hashing, replication, tunable consistency levels, persistent commit logs**, and a modular multi-node architecture.

---

## 🚀 Features

### **⚡ Distributed Architecture**

* Uses **consistent hashing** with virtual nodes for balanced key distribution.
* Scales horizontally — nodes can be added/removed with minimal key reshuffling.

### **🛡 Replication**

* Supports configurable **replication factor (RF)**.
* Routes read/write requests to all replicas of a key.
* Ensures durability even if nodes fail.

### **📏 Tunable Consistency Levels**

Implements Dynamo-style consistency semantics:

* **ONE** – Fastest, returns after first replica ack.
* **QUORUM** – Majority ack ensures stronger consistency.
* **ALL** – Strong consistency, waits for all replicas.

### **💾 Persistent Storage**

* Log-structured, append-only **commit logs**.
* Enables crash recovery and deterministic state reconstruction.

### **🔌 Networking**

* Custom JSON-based RPC layer using **TCP sockets**.
* Concurrent request handling with multi-threaded NodeServer.

### **🧩 Modular Components**

* `ClusterManager` – Manages nodes, bootstrapping, and ports.
* `NodeServer` – Handles incoming GET/PUT/DELETE RPC operations.
* `NodeClient` – Routes requests based on consistent hashing + consistency level.
* `KeyValueStore` – In-memory KV engine backed by persistent logs.
* `ConsistentHashRing` – Hash ring with virtual nodes for sharding.

---

## 📁 Project Structure

```
DistributedKVStore/
└── src/main/java/dkv/
    ├── Main.java
    ├── KeyValueStore.java
    ├── cluster/
    │   └── ClusterManager.java
    ├── network/
    │   ├── NodeServer.java
    │   ├── NodeClient.java
    │   ├── Request.java
    │   └── Response.java
    └── routing/
        ├── ConsistentHashRing.java
        └── HashUtil.java
```

---

## 🏗 How It Works

### 1. **Cluster Startup**

`Main.java` initializes the system:

* Bootstraps 3 nodes (NodeA, NodeB, NodeC)
* Assigns ports and commit logs
* Builds consistent hash ring
* Starts node servers on separate threads

### 2. **Routing a Request**

On `client.put(key, value)`:

1. Hash key → find coordinator node
2. Determine replica set (`RF=2`)
3. Send write RPCs to replicas
4. Apply consistency logic (ONE / QUORUM / ALL)
5. Return result to client

### 3. **Commit Log Recovery**

On node restart:

* Replay commit log line-by-line
* Rebuild in-memory map safely

---

## ▶️ Running the Cluster

### **Compile**

```bash
mvn compile
```

### **Start the Cluster**

```bash
mvn exec:java -Dexec.mainClass="dkv.Main"
```

Expected output:

```
Started NodeA on port 5050
Started NodeB on port 5051
Started NodeC on port 5052
```

---

## 🧪 Example Operations

Inside `Main.java`:

```java
System.out.println("=== PUT ===");
client.put("name", "Laukik");

System.out.println("=== GET ===");
System.out.println(client.get("name"));

System.out.println("=== DELETE ===");
client.delete("name");
```

---

## 📚 Future Enhancements

You can extend this system with:

### 🔥 System-Level Features

* **Hinted Handoff** (buffer writes for failed replicas)
* **Gossip Membership Protocol**
* **Failure Detection (Phi Accrual)**
* **Vector Clocks for versioning**
* **Merkle Trees for anti-entropy repair**
* **SSTables + MemTables (LSM Tree)**

### ⚙️ Performance Features

* Batch RPCs
* Asynchronous writes
* Thread pools
* Connection pooling

---

## 🏆 Why This Project Matters

This project demonstrates fluency in real distributed systems concepts:

* Partitioning & replication
* Hashing & sharding
* Consistency models
* Durable log-based storage
* Network programming
* Concurrency control
* System architecture

It mirrors the fundamentals of:

* **Amazon Dynamo**
* **Cassandra**
* **Riak**
* **etcd**

---

