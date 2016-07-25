#include <iostream>
#include <fstream>
#include <string>
#include <stdlib.h> 
#include <sstream>
#include<vector>
#include <sys/stat.h>
#include <unistd.h>
#include <cstring>
#include <iterator>


using namespace std;

string sql[50];
int length = 0;
vector<string> tables;
vector<string> columns;
vector<string> type;
vector<int> noOfColsPerTable;
vector<int> totalColsAddition;
vector<int> records;

vector<string> pkeys;
int totalCols = 0;

string getCols(string tbName)
{
		int posi;
		for(int i = 0; i < tables.size(); i++)
		{
			if(tables.at(i) == tbName)
			{
				posi = i;
			}
		}

		int startC, endC, cols;
		string colnames = "";
		
		if(posi == 0)
		{
			cols = totalColsAddition.at(0);
			for(int i = 0; i < cols; i++)
			{
				colnames = colnames + columns.at(i);
				colnames = colnames + " ";
			}
		}else{
			startC = totalColsAddition.at(posi-1);
			endC = totalColsAddition.at(posi)-1;
			for(int i = startC; i <= endC; i++)
			{
				colnames = colnames + columns.at(i);
				colnames = colnames + " ";
			}
		}
	
	
	return colnames;
}

string getType(string tbName)
{
		int posi;
		for(int i = 0; i < tables.size(); i++)
		{
			if(tables.at(i) == tbName)
			{
				posi = i;
			}
		}

		int startC, endC, cols;
		string types = "";
		
		if(posi == 0)
		{
			cols = totalColsAddition.at(0);
			for(int i = 0; i < cols; i++)
			{
				types = types + type.at(i);
				types = types + " ";
			}
		}else{
			startC = totalColsAddition.at(posi-1);
			endC = totalColsAddition.at(posi)-1;
			for(int i = startC; i <= endC; i++)
			{
				types = types + type.at(i);
				types = types + " ";
			}
		}
	
	return types;
}

void readFileWhr(string tbName, string colnm, string colCond, int posi, string cond, int op, int typ2r)
{
	string fname = tbName + ".tbl";
	int colCount = 0;
	int cols = 0;
	int allCols = 1;
	string outPut = "";
	int startC, endC;
	
	int colNo = 0;
	int upp = 0;
	int low = 0;
	int colNoCond = 0;
	int uppC = 0;
	int lowC = 0;
	
	if(posi == 0)
	{
		cols = totalColsAddition.at(0);
		for(int i = 0; i < cols; i++)
		{
			if(columns.at(i) == colnm)
				colNo = i;
		}
	}else{
		startC = totalColsAddition.at(posi-1);
		endC = totalColsAddition.at(posi);
		cols = endC - startC;
	}
	
	if(posi == 0)
	{
		cols = totalColsAddition.at(0);
		for(int i = 0; i < cols; i++)
		{
			if(columns.at(i) == colCond)
				colNoCond = i;
		}
	}else{
		for(int i = 0; i < columns.size(); i++)
			{
				if(columns.at(i) == colCond)
					uppC = i;
			}
			lowC = totalColsAddition.at(posi-1);
			colNoCond = uppC - lowC;
	}
	
	
	ifstream file (fname, ios::binary | ios::in | ios::app);
	
	if(colnm == "*")
	{
		allCols = 1;
		int match = 0;
		string tempOP = "";
		while(!file.eof())
		{
			int cnt = allCols;
			
			char * buffer = new char[50];
			file.seekg (50*colCount, ios::beg);
			file.read (buffer, 50);
			if(!file.eof())
			{
				tempOP = tempOP + buffer + " ";
				//cout<<buffer<<endl;
				if(allCols == colNoCond+1)
				{	
					//cout<<"cl con: "<<colNoCond<<endl;
					//cout<<"in: "<<tempOP<<endl;
					if(typ2r == 0)
					{
						stringstream str(cond);
						int x;
						str >> x;	
						
						stringstream strbfr(buffer);
						int xbfr;
						strbfr >> xbfr;
						
						if(op == 1)
						{
							if(xbfr <= x)
							{
								match = 1;
								//cout<<"matched"<<endl;
							}
						}else if(op == 2)
						{
							if(xbfr >= x)
							{
								match = 1;
								//cout<<"matched"<<endl;
							}
						}
						else if(op == 3)
						{
							if(xbfr == x)
							{
								match = 1;
								//cout<<"matched"<<endl;
							}
						}
						else if(op == 4)
						{
							if(xbfr > x)
							{
								match = 1;
								//cout<<"matched"<<endl;
							}
						}else{
							if(xbfr < x)
							{
								match = 1;
								//cout<<"matched"<<endl;
							}
						}
					}else
					{
						//cout<<buffer<<" "<<cond<<endl;
						if(buffer == cond)
						{
							match = 1;
						}
					}
				}
				if(allCols == cols)
				{	
					tempOP = tempOP + "\n";
					allCols = 0;
				}
			}
			//cout<<"tm: "<<tempOP<<endl;
			if(match == 1 && cnt == cols){
				outPut = outPut + tempOP;
				tempOP = "";
				cnt = 0;
				match = 0;
			}else if(match == 0 && cnt == cols)
			{
				tempOP = "";
				cnt = 0;
			}
			delete[] buffer;
			colCount++;
			allCols++;
		}
	}else{
		
		allCols = 1;
		int match = 0;
		string tempOP = "";
		colCount = 0;
		
		
		if(posi == 0)
		{
			for(int i = 0; i < cols; i++)
			{
				if(columns.at(i) == colnm)
					colNo = i;
			}
		}else{
			for(int i = 0; i < columns.size(); i++)
			{
				if(columns.at(i) == colnm)
					upp = i;
			}
			low = totalColsAddition.at(posi-1);
			colNo = upp - low;
		}
		
		if(posi == 0)
		{	
			cols = totalColsAddition.at(0);
			for(int i = 0; i < cols; i++)
			{
				if(columns.at(i) == colCond)
					colNoCond = i;
			}
		}else{
			for(int i = 0; i < columns.size(); i++)
			{
				if(columns.at(i) == colCond)
					uppC = i;
			}
			lowC = totalColsAddition.at(posi-1);
			colNoCond = uppC - lowC;
		}
		
		while(!file.eof())
		{
			int cnt = allCols;

			char * buffer = new char[50];
			file.seekg (50*colCount, ios::beg);
			file.read (buffer, 50);
			if(!file.eof())
			{
				
				if(allCols == colNo+1)
					tempOP = tempOP + buffer + " ";
				//cout<<buffer<<endl;
				if(allCols == colNoCond+1)
				{	
					//cout<<"cl con: "<<colNoCond<<endl;
					//cout<<"in: "<<tempOP<<endl;
					if(typ2r == 0)
					{
						stringstream str(cond);
						int x;
						str >> x;	
						
						stringstream strbfr(buffer);
						int xbfr;
						strbfr >> xbfr;
						
						if(op == 1)
						{
							if(xbfr <= x)
							{
								match = 1;
								//cout<<"matched"<<endl;
							}
						}else if(op == 2)
						{
							if(xbfr >= x)
							{
								match = 1;
								//cout<<"matched"<<endl;
							}
						}
						else if(op == 3)
						{
							if(xbfr == x)
							{
								match = 1;
								//cout<<"matched"<<endl;
							}
						}
						else if(op == 4)
						{
							if(xbfr > x)
							{
								match = 1;
								//cout<<"matched"<<endl;
							}
						}else{
							if(xbfr < x)
							{
								match = 1;
								//cout<<"matched"<<endl;
							}
						}
					}else
					{
						//cout<<buffer<<" "<<cond<<endl;
						if(buffer == cond)
						{
							match = 1;
						}
					}
				}
				if(allCols == cols)
				{	
					tempOP = tempOP + "\n";
					allCols = 0;
				}
			}
			//cout<<"tm: "<<tempOP<<endl;
			if(match == 1 && cnt == cols){
				outPut = outPut + tempOP;
				tempOP = "";
				cnt = 0;
				match = 0;
			}else if(match == 0 && cnt == cols)
			{
				tempOP = "";
				cnt = 0;
			}
			delete[] buffer;
			colCount++;
			allCols++;
		}
	}
	cout<<"output: "<<endl;
	cout<<outPut<<endl;
	file.close();	
}

void readFile(string tbName, string colnm, int posi)
{
	string fname = tbName + ".tbl";
	int colCount = 0;
	int cols = 0;
	int allCols = 1;
	string outPut = "";
	int startC, endC;
	
	int colNo = 0;
	int upp = 0;
	int low = 0;
	
	if(posi == 0)
	{
		cols = totalColsAddition.at(0);
		for(int i = 0; i < cols; i++)
		{
			if(columns.at(i) == colnm)
				colNo = i;
		}
	}else{
		startC = totalColsAddition.at(posi-1);
		endC = totalColsAddition.at(posi);
		cols = endC - startC;
	}
	ifstream file (fname, ios::binary | ios::in | ios::app);
	
	if(colnm == "*")
	{
		while(!file.eof())
		{
			char * buffer = new char[50];
			file.seekg (50*colCount, ios::beg);
			file.read (buffer, 50);
			if(!file.eof())
			{
				outPut = outPut + buffer + " ";
				if(allCols == cols)
				{	
					outPut = outPut + "\n";
					allCols = 0;
				}
			}
			delete[] buffer;
			colCount++;
			allCols++;
		}
	}else{
		colCount = 0;
		
		allCols = 1;
		if(posi == 0)
		{
			for(int i = 0; i < cols; i++)
			{
				if(columns.at(i) == colnm)
					colNo = i;
			}
		}else{
			for(int i = 0; i < columns.size(); i++)
			{
				if(columns.at(i) == colnm)
					upp = i;
			}
			low = totalColsAddition.at(posi-1);
			colNo = upp - low;
		}
		
		while(!file.eof())
		{
			char * buffer = new char[50];
			file.seekg (50*colCount, ios::beg);
			file.read (buffer, 50);
			if(!file.eof())
			{
				if(allCols == colNo+1)
					outPut = outPut + buffer + "\n";
				if(allCols == cols)
				{	
					//outPut = outPut + "\n";
					allCols = 0;
				}
			}
			delete[] buffer;
			colCount++;
			allCols++;
		}
	}
	cout<<"output: "<<endl;
	cout<<outPut<<endl;
	file.close();
}

void appendToFile(string tbName, string vals)
{
	
	string fname = tbName + ".tbl";
	int posi;
		for(int i = 0; i < tables.size(); i++)
		{
			if(tables.at(i) == tbName)
			{
				posi = i;
			}
		}
	ofstream file(fname, ios::binary | ios::out | ios::app);
	
	if(!file.is_open())
	{
		cout<<"error while opening the file"<<endl;
	}
	else{
	
		if(vals != "")
		{
	
			string buf; 
			stringstream ss(vals); 
			vector<string> coltokens; 
			while (ss >> buf)
			coltokens.push_back(buf);
			for(int i = 0; i < coltokens.size(); i++)
			{
				char memBuf[50];
				strcpy(memBuf, coltokens.at(i).c_str());
				file.write(memBuf, sizeof(memBuf));
			}
			cout<<"successfully inserted the data"<<endl;
			records.at(posi) = records.at(posi) + 1;
		}else{
			cout<<"No data found to write"<<endl;
		}		
	}
	file.close();
	
	//readFile(tbName);
}

void writeToFile(string tbName, string vals)
{
	string fname = tbName + ".tbl";	
	ofstream file(fname, ios::binary | ios::out | ios::trunc);

	if(!file.is_open())
	{
		cout<<"error while opening the file"<<endl;
	}
	else{
		if(vals != "")
		{
			string buf; 
			stringstream ss(vals); 
			vector<string> coltokens; 
			while (ss >> buf)
			coltokens.push_back(buf);
			for(int i = 0; i < coltokens.size(); i++)
			{
				char memBuf[50];
				strcpy(memBuf, coltokens.at(i).c_str());
				file.write(memBuf, sizeof(memBuf));
			}
			cout<<"successfully created the table"<<endl;
			records.push_back(0);
		}else{
			cout<<"No data found to write"<<endl;
		}
	}
	file.close();
	//readFile(tbName);
}

void updateFile()
{
	
}


void creatTable(string sql)
{
	//cout<<"in CT: "<<endl;
	
    string buf; 
    stringstream ss(sql); 

    vector<string> tokens; 

    while (ss >> buf)
        tokens.push_back(buf);
	
	string tbName;
	
	tbName = tokens.at(2);
	
	int exists = 0;
	int pkey = 0;
	int noOfCols = 0;
	
	if(tables.size() == 0)
	{
		tables.push_back(tbName);
		
			for(int j = 3; j < tokens.size() - 4; j++)
			{	
				if(j%2 == 0)
				{					
					if(tokens.at(j)=="INT," || tokens.at(j)== "INT" || tokens.at(j)=="int," || tokens.at(j)== "int")
					{
						type.push_back("INT");
					}else if(tokens.at(j) == "CHAR" || tokens.at(j) == "char")
					{
						string t = "CHAR(";
						string t1 = ")";
						type.push_back(t + tokens.at(j+1) + t1);
						j++;
					}
				}else
				{
					columns.push_back(tokens.at(j));
					noOfCols++;
					totalCols++;
				}
			}
			
			noOfColsPerTable.push_back(noOfCols);
			totalColsAddition.push_back(totalCols);
			pkeys.push_back(tokens.at(tokens.size()-2));
			
			string cls = getCols(tbName);
			writeToFile(tbName, cls);
	}else{
		for(int j = 0; j < tables.size(); j++)
		{
			if(tables.at(j) == tbName){
				exists = 1;
			}
		}
		if(exists == 1)
		{
			cout<<"Table already exists"<<endl;
		}
		else{
			tables.push_back(tbName);
			
			for(int j = 3; j < tokens.size() - 4; j++)
			{	
				if(j%2 == 0)
				{
					
					if(tokens.at(j)=="INT," || tokens.at(j)== "INT" || tokens.at(j)=="int," || tokens.at(j)== "int")
					{
						type.push_back("INT");
					}else if(tokens.at(j) == "CHAR" || tokens.at(j) == "char")
					{
						string t2 = "CHAR(";
						string t3 = ")";
						type.push_back(t2 + tokens.at(j+1) + t3);
						j++;
					}
				}else
				{
					columns.push_back(tokens.at(j));
					noOfCols++;
					totalCols++;
				}
			}
			
			noOfColsPerTable.push_back(noOfCols);
			totalColsAddition.push_back(totalCols);
			pkeys.push_back(tokens.at(tokens.size()-2));
			
			string cls = getCols(tbName);
			writeToFile(tbName, cls);
		}
	}
	tbName = "";
}

int checkCol(int posi, string colnm)
{
	int startC, endC, cols;
	int colExists = 0;
		
	if(posi == 0)
	{
		cols = totalColsAddition.at(0);
		for(int i = 0; i < cols; i++)
		{
			if(columns.at(i) == colnm)
				colExists = 1;
		}
	}else{
		startC = totalColsAddition.at(posi-1);
		endC = totalColsAddition.at(posi)-1;
		for(int i = startC; i <= endC; i++)
		{
			if(columns.at(i) == colnm)
				colExists = 1;
		}
	}
	return colExists;
}

void selectFrom(string sql)
{
	//cout<<"in SF: "<<sql<<endl;
	if(strstr(sql.c_str(), ";"))
		sql.pop_back();
	int exists = 0;
	int posi = 0;
	int colExists = 0;
	int colExistsW = 0;
	
	int eql = 0;
	int less = 0;
	int grt = 0;
	
	for(int i = 0; i < sql.length(); i++)
	{
		if(sql[i] == '=')
		{
			eql = 1;
			sql[i] = ' ';
		}else if(sql[i] == '<')
		{
			less = 1;
			sql[i] = ' ';
		}else if(sql[i] == '>')
		{
			grt = 1;
			sql[i] = ' ';
		}
	}
	
	string buf; 
    stringstream ss(sql); 
    vector<string> selectTokens; 
    while (ss >> buf)
        selectTokens.push_back(buf);
	
	if(selectTokens.size() >= 4)
	{
		if(selectTokens.at(2) != "from" && selectTokens.at(2) != "FROM" )
		{
			cout<<"invalid query"<<endl;
		}
		else{
			
			string colnm = selectTokens.at(1);
			string tbName = selectTokens.at(3);
			
			if(colnm == "*")
			{
				colExists = 1;
			}
			
			if(selectTokens.size() > 4)
			{
				int existsW = 0;
				int posiW = 0;
				for(int i = 0; i < tables.size(); i++)
				{
					if(tables.at(i) == tbName)
					{	
						existsW = 1;
						posiW = i;
					}
				}
				if(existsW != 1)
				{
					cout<<"Table: "<<tbName<<" not found"<<endl;
				}else
				{
					
					if(selectTokens.at(4) == "WHERE" || selectTokens.at(4) == "where")
					{	
						int typ2r = 0;
						int ok = 0;
						if(colExistsW == 0)
							colExistsW = checkCol(posiW, selectTokens.at(5));
						if(colExistsW != 1)
						{
							cout<<"Column: "<<selectTokens.at(5)<<" does not exist"<<endl;
						}else{
							int colNo = 0;
							if(posiW == 0)
							{
								for(int i = 0; i < totalColsAddition.at(0); i++)
								{
									if(columns.at(i) == selectTokens.at(5))
										colNo = i;
								}
								
								string typ1 = type.at(colNo);
								cout<<typ1<<endl;
								ok = 0;
								if(typ1 == "INT")
								{
									stringstream str1(selectTokens.at(6));
									int x1;
									str1 >> x1;	
									if (!str1)
									{
										ok = 1;
										cout<<"err: Column"<<selectTokens.at(5)<<"is of type int : "<<endl;
									}
								}else{
									grt = 0;
									less = 0;
									typ2r = 1;
									typ1.pop_back();
									string lenstr1 = typ1.substr(5);
									int len1 = atoi( lenstr1.c_str() );
					
									if(strstr(selectTokens.at(6).c_str(), "\'"))
									{
										selectTokens.at(6).pop_back();
										selectTokens.at(6) = selectTokens.at(6).substr(1);
						
										if(selectTokens.at(6).length() > len1)
										{
											ok = 1;
											cout<<"length of string in where condition is more than allocated length for the column"<<endl;	
										}
									}
									else{
										ok = 1;
										cout<<"value is not string.. please check your insert query "<<endl;
									}
								}
							}else{
								int startC,endC;
								int upp = 0;
								int low = 0;
								
								startC = totalColsAddition.at(posiW-1);
								endC = totalColsAddition.at(posiW)-1;
								for(int i = startC; i <= endC; i++)
								{
									if(columns.at(i) == selectTokens.at(5))
										colNo = i;
								}
								string typ = type.at(colNo);
								ok = 0;
								if(typ == "INT")
								{
									stringstream str(selectTokens.at(6));
									int x;
									str >> x;	
									if (!str)
									{
										ok = 1;
										cout<<"err: Column"<<selectTokens.at(5)<<"is of type int : "<<endl;
									}
								}else{
									grt = 0;
									less = 0;
									typ2r = 1;
									typ.pop_back();
									string lenstr = typ.substr(5);
									int len = atoi( lenstr.c_str() );
					
									if(strstr(selectTokens.at(6).c_str(), "\'"))
									{
										selectTokens.at(6).pop_back();
										selectTokens.at(6) = selectTokens.at(6).substr(1);
						
										if(selectTokens.at(6).length() > len)
										{
											ok = 1;
											cout<<"length of string in where condition is more than allocated length for the column"<<endl;	
										}
									}
									else{
										ok = 1;
										cout<<"value is not string.. please check your insert query "<<endl;
									}
								}
							}
							
							//cout<<"whr"<<endl;
						}
						int op;
						if(eql == 1 && less == 1)
						{
							op = 1;
						}else if(eql == 1 && grt == 1)
						{
							op = 2;
						}else if(eql == 1)
						{
							op = 3;
						}else if(grt == 1)
						{
							op = 4;
						}else
						{
							op = 5;
						}
						if(ok == 0)
							readFileWhr(tbName, colnm, selectTokens.at(5), posiW, selectTokens.at(6), op, typ2r);
					}
					else{
							cout<<"Invalid query near: "<<selectTokens.at(4)<<endl;
					}
				}
				
			}
			else{
		
				for(int i = 0; i < tables.size(); i++)
				{
					if(tables.at(i) == tbName)
					{	
						exists = 1;
						posi = i;
					}
				}
				if(exists != 1)
				{
					cout<<"Table: "<<tbName<<" not found"<<endl;
				}else
				{
					if(colExists == 0)
						colExists = checkCol(posi, colnm);
					if(colExists != 1)
					{
						cout<<"Column: "<<colnm<<" does not exist"<<endl;
					}else{
						readFile(tbName, colnm, posi);
					}
				}
			}
		}
		
	}else{
		cout<<"invalid query"<<endl;
	}
	
}

int checKDupPK(string tbName, int posCol, string val, int cols)
{
	cout<<"hereA"<<endl;
	string fname = tbName + ".tbl";
	int colCount = 0;
	if(cols == 1)
	{
		cout<<"hereB"<<endl;
		colCount = 0 + cols;
	}else if(cols == posCol+1)
	{
		cout<<"hereC"<<endl;
		//cout<<"last pk"<<endl;
	}else{
		cout<<"hereD"<<endl;
		colCount = cols + posCol;
	}
	cout<<"hereE"<<endl;
	ifstream file (fname, ios::binary | ios::in | ios::app);
	while(!file.eof())
	{
		char * buffer = new char[50];
		file.seekg (50*colCount, ios::beg);
		file.read (buffer, 50);
		if(!file.eof())
		{
			if(buffer == val)
			{
				return 1;
			}
			
		}	
		delete[] buffer;
		colCount = colCount + cols;
	}
	file.close();
	cout<<"here8"<<endl;
	return 0;
}

void validateData(string tbName, vector<string> values)
{
	
	int exists = 0;
	int posi = 0;
	for(int j = 0; j < tables.size(); j++)
	{
		if(tables.at(j) == tbName){
			exists = 1;			
			posi = j;
		}
	}
	if(exists == 1)
	{
	
		string types = getType(tbName);
		int ok = 0;
		string buf; 
		stringstream ss(types); 
		vector<string> tp; 
		while (ss >> buf)
			tp.push_back(buf);
		if(tp.size() != values.size())
		{
			cout<<"please insert values for exact no of columns"<<endl;
			ok = 1;
		}
		else{
	
			for(int i = 0; i < tp.size(); i++)
			{
				if(tp.at(i) == "INT")
				{
					stringstream str(values.at(i));
					int x;
					str >> x;	
					if (!str)
					{
						ok = 1;
						cout<<"err: not an int for column: "<<i<<endl;
					}
				}
				if(strstr(tp.at(i).c_str(), "CHAR"))
				{
					tp.at(i).pop_back();
					string lenstr = tp.at(i).substr(5);
					int len = atoi( lenstr.c_str() );
					
					if(strstr(values.at(i).c_str(), "\'"))
					{
						values.at(i).pop_back();
						values.at(i) = values.at(i).substr(1);
						
						if(values.at(i).length() > len)
						{
							ok = 1;
							cout<<"length of string is more than allocated length for the column no: "<<i<<endl;	
						}
					}
					else{
						ok = 1;
						cout<<"value is not string.. please check your insert query "<<endl;
					}
				}
			}
	
		}
		if(ok == 0)
		{
	
			string pk = pkeys.at(posi);
			
			string vls2send = "";
			for(int i = 0; i < values.size(); i++)
			{
				vls2send = vls2send + values.at(i);
				vls2send = vls2send + " ";
			}
		
			if(pk != "NO")
			{
	
				int startC, endC, cols;
				string types = "";
				int posCol = 0;
				if(posi == 0)
				{
					cols = totalColsAddition.at(0);
					for(int i = 0; i < cols; i++)
					{
						if(columns.at(i) == pk)
							posCol = i;
					}
				}else{
	
					startC = totalColsAddition.at(posi-1);
					endC = totalColsAddition.at(posi)-1;
					for(int i = startC; i <= endC; i++)
					{
						if(columns.at(i) == pk)
							posCol = i;
					}
					cols = totalColsAddition.at(posi) - startC;
					//cout<<"here7: "<<cols<<" pos: "<<posCol<<endl;
				}
				
				int dup = 0;
				//if(cols > 1 && posCol != cols-1)
					//dup = checKDupPK(tbName, posCol, values.at(posCol), cols);
				if(dup == 1)
				{
					cout<<"Record found with duplicate primary key"<<endl;
				}
				else{
					
					appendToFile(tbName, vls2send);
				}
			}else
			{
				appendToFile(tbName, vls2send);
			}
		}
		
	}
	else{
		cout<<"Table does not exists or invalid table name"<<endl;
	}
}

void insertInto(string sql)
{
	//cout<<"in II: "<<endl;
	string buf; 
    stringstream ss(sql); 

    vector<string> tokens; 

    while (ss >> buf)
        tokens.push_back(buf);
	
	string ip = "";
	
	for(int i = 4; i < tokens.size(); i++)
	{
		ip = ip + tokens.at(i);
	}
		
	string temp = "";

		temp = ip;
			
		for  (int j = 0; j < temp.length(); j++)
		{
			if(temp[j] == ',')
			{
				temp[j] = ' ';
			}
			if(temp[j] == ';')
			{
				temp.pop_back();
			}
		}
		ip = temp;
	string buf1; 
    stringstream ss1(ip); 

    vector<string> tokens1; 

    while (ss1 >> buf1)
        tokens1.push_back(buf1);
	
	validateData(tokens.at(2), tokens1);
}

void createCatalog()
{
	ofstream outfile;

	outfile.open("catalog.txt", ios:: out | ios::trunc);
	
	for(int i = 0; i < tables.size(); i++)
	{		
		outfile << "tablename= "<<tables.at(i)<<"\n";
		outfile << "columns= ";
		
		string cls = getCols(tables.at(i));
		
		string bufC; 
		stringstream ssC(cls); 
		vector<string> cl; 
		while (ssC >> bufC)
			cl.push_back(bufC);
		
		string ty = getType(tables.at(i));
		string buf; 
		stringstream ss(ty); 
		vector<string> tp; 
		while (ss >> buf)
			tp.push_back(buf);
		
		int recSize = 0;
		
		for(int j = 0; j < cl.size(); j++)
		{
			outfile <<cl.at(j)<<":"<<tp.at(j)<<",";
			if(tp.at(j) == "INT")
			{
				recSize = recSize + 4;
			}
			else{
				tp.at(j).pop_back();
				string lenstr = tp.at(j).substr(5);
				int len = atoi( lenstr.c_str() );
				recSize = recSize + len;
			}
		}
		outfile <<"\n";
		outfile << "primary key= "<<pkeys.at(i)<<"\n";
		outfile <<"recordsize= "<<recSize<<"\n";
		
		int totSize = recSize * records.at(i);
		
		outfile <<"totalsize= "<<totSize<<"\n";
		outfile <<"records= "<<records.at(i)<<"\n";
		
	}
	
	outfile.close();
}

void showTables()
{
	cout<<"Tables are: "<<endl;
	for(int i = 0; i < tables.size(); i++)
	{
		cout<<tables.at(i)<<" "<<endl;
	}
}

void showTableT(string sql)
{
	if(strstr(sql.c_str(), ";"))
		sql.pop_back();
	
	string bufT; 
    stringstream ssT(sql); 

    vector<string> tokens; 
	while (ssT >> bufT)
        tokens.push_back(bufT);
	
	if(tokens.size() != 3)
	{
		cout<<"invalid query"<<endl;
	}else
	{
	
		int exists = 0;
		int posi = 0;
		for(int i = 0; i < tables.size(); i++)
		{
			if(tables.at(i) == tokens.at(2))
			{
				exists = 1;
				posi = i;
			}
		}
		if(exists == 0)
		{
			cout<<"Table "<<tokens.at(2)<<" does not exists"<<endl;
		}
		else
		{
			string cls = getCols(tokens.at(2));
		
			string bufC; 
			stringstream ssC(cls); 
			vector<string> cl; 
			while (ssC >> bufC)
				cl.push_back(bufC);
		
			string ty = getType(tokens.at(2));
			string buf; 
			stringstream ss(ty); 
			vector<string> tp; 
			while (ss >> buf)
				tp.push_back(buf);
			
			cout<<"Table name= "<<tokens.at(2)<<endl;
			cout<<"Columns= ";
			for(int j = 0; j < cl.size(); j++)
			{
				cout <<cl.at(j)<<":"<<tp.at(j)<<",";
			}
			cout<<endl;
			cout << "primary key= "<<pkeys.at(posi)<<"\n";
		}
	}
}
void checkStart(int no)
{
		string temp;
		temp = sql[no];
		if(temp == "QUIT" || temp == "quit" || temp == "QUIT;" || temp == "quit;")
		{
			if(no > 0)
				createCatalog();
			cout<<"Good Bye... !!!"<<endl;
		}
		else
		{
			int j = 0;
			string firstWord ;
			do{
				firstWord = firstWord + temp[j];
				j++;
			}while(temp[j-1] != ' ');
		
			if(firstWord == "create " || firstWord == "CREATE " || firstWord == "Create " )
			{
				creatTable(sql[no]);
			}else if(firstWord == "select " || firstWord == "SELECT " || firstWord == "Select ")
			{
				selectFrom(sql[no]);
			}else if (firstWord == "insert " || firstWord == "INSERT " || firstWord == "Insert ")
			{
				insertInto(sql[no]);
			}else if (sql[no] == "show tables;" || sql[no] == "SHOW TABLES;" || sql[no] == "show tables" || sql[no] == "SHOW TABLES" )
			{
				showTables();
			}
			else if (firstWord == "SHOW " || firstWord == "show " )
			{
				showTableT(sql[no]);
			}
			else{
				cout << "this is not a valid query"<<endl;
			}
		
			firstWord = "";
		}
		temp = "";
}

void removePara(int no)
{
		string temp ;

		temp = sql[no];
			
		for  (int j = 0; j < temp.length(); j++)
		{
			if(temp[j] == '(')
			{
				temp[j] = ' ';
			}
			
			if(temp[j] == ')')
			{
				temp[j] = ' ';
			}
		}
		sql[no] = temp;
		
		temp = "";
}

int main()
{
   do{
		cout << "SQL> ";
		getline (cin,sql[length]);
		length ++;
		removePara(length - 1);
		checkStart(length - 1);
	}while(sql[length - 1] != "QUIT" && sql[length - 1] != "quit" && sql[length] != "QUIT" && sql[length] != "quit" );
		
   return 0;
}
