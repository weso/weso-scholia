from time import time
import sys

TEMPLATE_FILE = "chunk{}.nt"  # template to place the parts of the original file in disk

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


def truncate_file(target_file, a_point):
    with open(target_file, "a") as f_in:
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
        truncate_file(target_file, a_point)
        print("Chunk finished! Seconds since init process: {}".format(time() - ini))


def get_input_file_and_template_output_file_from_sys_arguments(given_arguments):

    # Check the correct number of arguments. Must be main.py <input_file> <output_directory>.
    if len(given_arguments) < 4:
        print("Wrong number of arguments. <input_file> <output_directory> <chunk_size>")
        exit(-1)

    # Remember that in sys.argv the 0 index corresponds to the python program itself.
    input_file = given_arguments[1]
    output_directory = given_arguments[2]
    chunk_size = int(given_arguments[3])

    # If the output directory path contains the trailiing slash remove it always to normalize the format.
    if output_directory[-1] == '/':
        output_directory = output_directory[:-1]

    # Compute the output template.
    output_template = output_directory + '/' + TEMPLATE_FILE

    # Return a tuple with both the input file and the output template.
    return input_file, output_template, chunk_size


if __name__ == "__main__":
    print('======================================================================')
    arguments = get_input_file_and_template_output_file_from_sys_arguments(sys.argv)
    print('Starting to remove blank nodes.')
    print(f'Input file: {arguments[0]}')
    print(f'Output template: {arguments[1]}')
    print(f'Lines per chunk: {arguments[2]}')
    print('----------------------------------------------------------------------')
    run(
        target_file=arguments[0],
        dest_template=arguments[1],
        lines_per_chunk=arguments[2]
    )
    print('======================================================================')
