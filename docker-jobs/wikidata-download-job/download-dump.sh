#!/bin/bash

if [ "$1" = 'ttl' ]; then
    wget https://dumps.wikimedia.org/wikidatawiki/entities/latest-all.ttl.bz2
else
    wget https://dumps.wikimedia.org/wikidatawiki/entities/latest-all.json.bz2
fi

exec "$@"