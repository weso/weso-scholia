# This Docker job split the contents of a
# file by the number of lines indicated in the jobdir directory.
# Run as 'docker run -d -v $pwd:/jobdir wesogroup/split-file-by-lines-job 1000 yourfile segmentName'
FROM alpine:latest
WORKDIR /jobdir
ENTRYPOINT ["split", "-l"]