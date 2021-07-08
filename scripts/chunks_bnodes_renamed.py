# from shutil import copyfile
from time import time

# copyfile("toy.txt", "toy2.txt")

TARGET_FILE = "toy2.txt"  # Original file path
TEMPLATE_FILE = "chunk{}.nt"  # template to place the parts of the original file in disk
CHUNK_MAX_LINES = 3  # Max size (in number of lines) of each chunk


## clean_line() consts
_TARGET_INDEXES = [0,2]
_TARGET_PREFIX_BNODE = "_:"
_NEW_PREFIX_BNODE = "_:n"
_SEP = " "
_MIN_NUMBER_OF_PIECES_LINE_WITH_A_TRIPLE = 3

def clean_line(target_line):
    pieces = target_line.split(_SEP)
    if len(pieces) < _MIN_NUMBER_OF_PIECES_LINE_WITH_A_TRIPLE:
        return target_line
    modification = False
    for an_index in _TARGET_INDEXES:
        if pieces[an_index].startswith(_TARGET_PREFIX_BNODE):
            pieces[an_index] = _NEW_PREFIX_BNODE + pieces[an_index][2:]
            modification = True
    return target_line if not modification else _SEP.join(pieces)

def write_chunk(a_point, countdown, target_file, dest_template):
    with open(target_file, "r") as f_in:
        f_in.seek(a_point)
        with open(dest_template.format(countdown), "w") as f_out:
            for a_line in f_in:
                f_out.write(clean_line(a_line))

def truncate_file(a_point, file_path):
    with open(file_path, "a") as f_in:
        f_in.seek(a_point)
        f_in.truncate()

def find_offset_points(target_file, lines_per_chunk):
    offset_points = [0]
    tmp_div = 0
    counter = 0
    with open(target_file, "rb") as f:
        for _ in f:
            counter += 1
            if counter / lines_per_chunk > tmp_div:
                tmp_div += 1
                offset_points.append(f.tell())
    return offset_points

def run(target_file, lines_per_chunk, dest_template):
    offset_points = find_offset_points(target_file, lines_per_chunk)
    ini = time()
    for i in reversed(range(len(offset_points))):
        print("Going for a chunk! Countdown: {}".format(i))
        a_point = offset_points[i]
        write_chunk(a_point=a_point,
                    countdown=i,
                    target_file=target_file,
                    dest_template=dest_template)
        truncate_file(a_point, target_file)
        print("Chunk finished! Seconds since init process: {}".format(time() - ini))

if __name__ == "__main__":
    run(target_file=TARGET_FILE,
        lines_per_chunk=CHUNK_MAX_LINES,
        dest_template=TEMPLATE_FILE)
    print("Done!")
