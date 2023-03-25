# transform sql insert statements to execute immediate '' format

mode = 'DROP'

with open('input.sql', 'r', encoding='utf-8') as infile, open('output.sql', 'w', encoding='utf-8') as outfile:
    for line in infile:
        line = line.strip()
        if line.startswith(mode):
            outfile.write("EXECUTE IMMEDIATE '{}';\n".format(line))