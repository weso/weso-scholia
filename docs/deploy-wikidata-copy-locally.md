| Action: | Deploy a local Wikidata Instance | 
| ------ | -------- |
| **Reproducibility:** | 4/5 |
| **Time:** | 5-6 days |
| **Resources:** | 16 Cores, 100 Gb Memory, 1.125Gb Disk and Ethernet |
| **O.S.:** | Rocky Linux (RHEL) |
| **Source:** | [https://addshore.com/2019/10/your-own-wikidata-query-service-with-no-limits/](https://addshore.com/2019/10/your-own-wikidata-query-service-with-no-limits/) |

## 1. Install Docker
This tutorial mainly makes use of the docker tool to extract, transform and load the data into our local Wikibase instance.

In order to install Docker in Rocky Linux (RHEL) or any other similar flavor, like CentOS we have to follow these steps:

### 1.1. Remove any prior installations of docker components
```bash
sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
```

### 1.2. Install yum-utils and add the Docker repository
```bash
sudo yum install -y yum-utils

sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
```

### 1.3. Install Docker Engine
```bash
sudo yum install docker-ce docker-ce-cli containerd.io
```

### 1.4. Start and enable Docker daemon
```bash
sudo systemctl start docker
sudo systemctl enable docker
```

### 1.5. Post-Installation Steps
Once Docker is installed in our system we can left the installation here. The problem is that now in order to run any Docker command we will need to provide the `sudo` order. To skip this we just need to add our user or any user in the machine to the docker group. To do this:

```bash
sudo usermod -aG docker $USER
```

## 2. Download latest Wikidata TTL dump
We, the WESO Research Group, provide a docker image that autamatically downloads the latests ttl dump. For that, first you will need to create a directory on your machine where you will work. Notice that this directory will need at leats 1Tb of space. Then:

```bash
docker run -d -v <local-work-dir>:/jobdir wesogroup/wd-dump-download-json-job
```

The `-d` arguments detaches the container execution from the shell. So you can disconnect without stoping the dump download. **This should take from 30 minutes to 1h.** To check the status you can execute `docker ps`, copy the id of the container and the `docker logs <container-id>`.

## 3. Munge the data
Next step is to munge the data. This will split the original dump file in chunks so it can be processed easier. This is also executed within a Docker container wo you don't have to install anything.

```bash
docker run \
  --entrypoint=/bin/bash \
  -d \
  -v <local-work-dir>:/stuff \
  wikibase/wdqs:0.3.6 \
  ./munge.sh \
  -c 50000 \
  -f /stuff/latest-all.ttl.gz \
  -d /stuff/mungeOut
```

**This will execute for at least 20-24 hours.** The current state can be explored over `docker logs <container-id>` command.

## 4. Deploy Wikidata Query Service
In order to populate the service we first need to run it using the below command. This mounts the directory containing the munged data as well as the directory for storing the service JNL file in. This will also expose the service on port 9999 which will be writable, so if you don’t want other people to access this check your firewall rules. Don’t worry if you don’t have a dockerData directory, as it will be created when you run this command.

```bash
docker run --entrypoint=/runBlazegraph.sh -d \
  -v <local-work-dir>/dockerData:/wdqs/data \
  -v <local-work-dir>:/mnt/disks/ssddata \
  -e HEAP_SIZE="128g" \
  -p 9999:9999 wikibase/wdqs:0.3.6
```

## 5. Load the data
Now we will load the data. For it we need the ID of the Docker container. This can be obtained by means of the `docker ps` command. Then:

```bash
docker exec -it <contianer-id> bash
```

And, once inside:

```bash
/wdqs/loadData.sh -n wdq -d /mnt/disks/ssddata/mungeOut
```

This step might take up to 5 days. So please run it so you can detach form the container at any point without stopping the execution.

Once the the data appears to be loaded, you can try out a shorter version of the cats query from the service examples.

```bash
curl localhost:9999/bigdata/namespace/wdq/sparql?query=%23Cats%0ASELECT%20%3Fitem%20%3FitemLabel%20%0AWHERE%20%0A%7B%0A%20%20%3Fitem%20wdt%3AP31%20wd%3AQ146.%0A%20%20SERVICE%20wikibase%3Alabel%20%7B%20bd%3AserviceParam%20wikibase%3Alanguage%20%22en%2Cen%22.%20%7D%0A%7D
```
