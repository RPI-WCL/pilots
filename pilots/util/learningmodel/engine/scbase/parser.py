csv = lambda(row): map( float, row.split(','))
csvheader = lambda(row): map( lambda(word): word.strip(), row.split(','))
pilot = lambda(row): map(float, row.split(':')[-1].split(','))
pilotheader = lambda(row): map(lambda(word): word.strip(), row[1:].split(','))