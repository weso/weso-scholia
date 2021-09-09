import ray
import sys
import glob, os
import progressbar
from SPARQLWrapper import SPARQLWrapper, POST, DIGEST

ray.init()


@ray.remote
def insert_ntriples_file_in_triplestore(file_path, triplestore_address):
    sparql_endpoint = SPARQLWrapper(triplestore_address)
    sparql_endpoint.setHTTPAuth(DIGEST)
    sparql_endpoint.setMethod(POST)
    file = open(file_path)
    file_lines = file.readlines()
    print(f'Inserting {file_path} in {triplestore_address}')
    for file_line in file_lines:
        insert_query = 'INSERT DATA { ' + file_line + ' }'
        sparql_endpoint.setQuery(insert_query)
        try:
            results = sparql_endpoint.query()
        except:
            print(f'Triple not inserted: {file_line}')
        #print(results.response.read())


def get_ntriples_files_in_directory(directory):
    os.chdir(directory)
    return glob.glob("*.nt")


if __name__ == '__main__':
    print('==================================================')
    print('Starting File Parallel Insert in Triplestore')
    input_directory = sys.argv[1]
    triplestore_address = sys.argv[2]
    print(f'Input directory: {input_directory}')
    print(f'Triplestore address: {triplestore_address}')
    print('--------------------------------------------------')
    futures = [insert_ntriples_file_in_triplestore.remote(input_directory+'/'+file_path, triplestore_address)
               for file_path in progressbar.progressbar(get_ntriples_files_in_directory(input_directory))]
    ray.get(futures)
    print('==================================================')
