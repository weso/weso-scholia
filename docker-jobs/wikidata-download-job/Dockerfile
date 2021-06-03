# Image to download latest Wikidata Dump
# Run as 'docker run -d --rm -v $pwd:/jobdir wesogroup/wikidata-download-djob'
FROM alpine:latest
WORKDIR /jobdir

COPY ./download-dump.sh .
ENTRYPOINT ["sh","download-dump.sh"]
