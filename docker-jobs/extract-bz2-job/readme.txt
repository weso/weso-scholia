This job extracts a compressed bz2 file. For that,
the docker container 'wesogroup/extract-bz2-job'
has to be run as follows:

docker run [-d|-it] -v $pwd:/jobdir wesogroup/extract-bz2-job file.bz2

Where the $pwd is the directory where the comprssed file is and where
the result will be stored.
