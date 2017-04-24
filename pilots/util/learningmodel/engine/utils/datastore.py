import csv
import sqlite3
import sys
if __name__ == '__main__':
	filename = sys.argv[1]
	csvfile = sys.argv[2]
	tabelname = sys.argv[3]
	requested_cols = ["Vtrue_ktas", "AMprs_inHG", "AMtmp_degC","curnt___lb","alpha__deg", "mode", "cruise_phase"]
	dr = csv.DictReader(open(csvfile, "rU"))
	dr.fieldnames = map(lambda(name): name.strip(), dr.fieldnames)
	to_db = [ (map(lambda(col_name): float(i[col_name]), requested_cols)) + [tabelname] for i in dr]
	print to_db[1]
	connection = sqlite3.connect(filename)
	cur = connection.cursor()
	columns = ""
	columns_insert = ""
	for i in requested_cols:
		columns += ( i + " REAL,")
		columns_insert += (i + ",")
	columns = columns[:-1]
	columns_insert = columns_insert[:-1]
	question_marks = ['?']*len(requested_cols)
	question_marks_str = reduce(lambda a,b: a+","+b, question_marks)
	cur.execute("CREATE TABLE IF NOT EXISTS \"main\" ( ROWINDEX INTEGER PRIMARY KEY AUTOINCREMENT, CATEGORY TEXT, %s );"%(columns))
	insert_query = "INSERT INTO \"main\" ( %s, CATEGORY ) VALUES ( %s, ? )"%(columns_insert, question_marks_str)
	print insert_query
	cur.executemany(insert_query, to_db)
	connection.commit()
