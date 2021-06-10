This job extracts a compressed bz2 file. For that,
the docker container 'wesogroup/extract-bz2-job'
has to be run as follows:

# This Docker job split the contents of a
# file by the number of lines indicated in the jobdir directory.
# Run as 'docker run -d -v $pwd:/jobdir wesogroup/split-file-by-lines-job 1000 yourfile segmentName'

Where the $pwd is the directory where the comprssed file is and where
the result will be stored.
