import os
import time
import sys
import requests
import pytz
from dateutil.parser import parse as parsedate


def get_url_modification_datetime(url: str):
    r = requests.head(url)
    url_time = r.headers['last-modified']
    return parsedate(url_time)


def get_file_modification_datetime(file_path: str):
    file_epoc = os.path.getmtime(file_path)
    return parsedate(time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(file_epoc)))


if __name__ == '__main__':

    if len(sys.argv) < 2:
        print('Error. Not enough arguments.')
        exit(-1)

    url_to_watch = sys.argv[1]  # URL to watch for modifications.
    file_to_watch = sys.argv[2]  # File to watch if it has been modified.

    print('============================================================')
    print('Starting watching loop')
    print(f'URL: {url_to_watch}')
    print(f'File: {file_to_watch}')

    while True:
        print('--------------------------------------------------------')
        current_date = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))
        print(f'Watch iteration started at {current_date}')

        website_last_updated_datetime = get_url_modification_datetime(url_to_watch)
        watching_file_last_updated_data_time = pytz.utc.localize(get_file_modification_datetime(file_to_watch))

        print(f'URL Modification Date: {website_last_updated_datetime}')
        print(f'File Modification Date: {watching_file_last_updated_data_time}')

        if watching_file_last_updated_data_time < website_last_updated_datetime:
            print("The file is outdated")
            # Download the file and append a -2 at the end.
            # Once downloaded remove the previous file and rename the new one.

        time.sleep(2)  # 12 hours in seconds is 43200