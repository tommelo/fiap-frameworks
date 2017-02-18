# 27SCJ
# environment config for redis
# @author nmelo

#update
apt-get update -y

#installing redis
wget http://download.redis.io/redis-stable.tar.gz
tar xvzf redis-stable.tar.gz
cd redis-stable
make
make test
make install
redis-server &



