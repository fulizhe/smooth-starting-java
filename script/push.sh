#!/bin/bash
# 推送
#

pwd

# 检查参数个数
if [ $# -lt 1 ]; then
  echo "错误：必须传入git commit msg。"
  exit 1
fi

# 打印第一个参数
echo "git commit msg: $1"

export HTTPS_PROXY="socks5://127.0.0.1:7897"
git add -A
git commit -m "$1"

git push