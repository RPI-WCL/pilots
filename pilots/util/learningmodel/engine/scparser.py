

def get(name):
	return PARSERS[name]

csvfile = lambda(row): map( float, row.split(','))
csvfile_header = lambda(row): map( lambda(word): word.strip(), row.split(','))
pilotfile = lambda(row): map(float, row.split(':')[-1].split(','))
pilot_header = lambda(row): map(lambda(word): word.strip(), row[1:].split(','))


PARSERS = {}
PARSERS["csv"] = csvfile
PARSERS["csvheader"] = csvfile_header
PARSERS["pilot"] = pilotfile
PARSERS["pilotheader"] = pilot_header