import os
import json
from pymongo import MongoClient
from tqdm import tqdm
import chromadb
from chromadb.config import Settings

# MongoDB连接信息
CONNECTION_STRING = "mongodb://172.21.213.123:27017/"
DB_NAME = "PortalOnline_230401"
COLLECTION_NAME = "computableModel"

# 连接到MongoDB
client = MongoClient(CONNECTION_STRING)
collection = client[DB_NAME][COLLECTION_NAME]

# 配置ChromaDB的本地设置
chroma_client = chromadb.PersistentClient(path="E:\code\ReproData")

# 创建ChromaDB的集合（如果尚不存在）
chroma_collection_name = "computableModel"
chroma_collection = chroma_client.get_or_create_collection(chroma_collection_name)

# 从MongoDB读取数据并插入到ChromaDB
documents = collection.find()
for document in tqdm(documents):
    embedding = document.get("embedding")
    if embedding:
        metadata = {
            "name": document.get("name"),
            "overview": document.get("overview"),
            "author": document.get("author"),
            "authorships": json.dumps(document.get("authorships")),
            "admins": json.dumps(document.get("admins")),
            "contributors": json.dumps(document.get("contributors"))
        }
        # 过滤掉值为 None 的字段
        metadata = {k: v for k, v in metadata.items() if v is not None}
        
        # 确保ID和embedding字段的格式正确
        chroma_collection.add(str(document["_id"]), embedding, metadata)

print("数据迁移完成")