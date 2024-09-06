import os
import json
import logging
import sys
from pymongo import MongoClient
import chromadb
from chromadb.config import Settings
from langchain_openai import OpenAIEmbeddings

# 配置日志记录级别
logging.basicConfig(level=logging.ERROR)  # 设置日志级别为ERROR，抑制INFO级别的日志

OPENAI_API_KEY = "sb-ccc08d92c8d641e071e73c047508bce558f84178e7906c45"
os.environ["OPENAI_API_KEY"] = OPENAI_API_KEY

embeddings = OpenAIEmbeddings()

query = "SWMM"

def search_chromadb(query, collection_name):
    # 配置ChromaDB的本地设置
    chroma_client = chromadb.PersistentClient(path="E:\code\ReproData")

    # 获取集合
    chroma_collection = chroma_client.get_collection(collection_name)

    query_embedding = embeddings.embed_query(query)
    
    # 执行查询
    results = chroma_collection.query(query_embeddings=query_embedding, n_results=1)
    
    return results

def main():
    if len(sys.argv) != 3:
        print("Usage: python query_chromadb.py <query> <collection_name>")
        sys.exit(1)
    # 内容名字
    query = sys.argv[1]
    # 查询的库的集合
    collection_name = sys.argv[2]

    # 执行查询
    results = search_chromadb(query, collection_name)
    
    # 打印结果
    print(results)

    return results
if __name__ == "__main__":
    main()