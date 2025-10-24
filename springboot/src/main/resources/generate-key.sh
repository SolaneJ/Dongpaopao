#!/bin/bash

# 一键生成本地自签名证书
openssl req -x509 -newkey rsa:2048 -nodes -keyout key.pem -out cert.pem -days 3650 -subj '/CN=localhost' -addext 'subjectAltName=DNS:localhost,IP:127.0.0.1'

echo "证书生成完成:"
echo "  cert.pem - 证书文件"
echo "  key.pem  - 私钥文件"