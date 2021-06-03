# This Docker job extracts the contents of a
# bz2 file in the jobdir directory.
# Run as 'docker run -d -v $pwd:/jobdir wesogroup/extract-bz2-job yourfile.bz2'
FROM alpine:latest
WORKDIR /jobdir
ENTRYPOINT ["bzip2", "-d"]