# Image to download latest Wikidata Dump
# Run as 'docker run -d --rm -v $pwd:/jobdir wesogroup/wd-dump-download-ttl-job'
FROM alpine:latest
WORKDIR /jobdir
ENTRYPOINT ["wget","https://dumps.wikimedia.org/wikidatawiki/entities/latest-all.json.bz2"]