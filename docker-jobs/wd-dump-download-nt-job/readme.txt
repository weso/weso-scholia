This job download the lastests available Wikidata
dump in NT format and stores it in the given
output folder. For that,

docker run [-d|-it] -v $pwd:/jobdir wesogroup/wd-dump-download-nt-job

Then the file will be stored in the $pwd.
