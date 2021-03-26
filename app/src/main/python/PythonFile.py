import numpy as np
import pandas as pd
import xlrd 
import et_xmlfile
import openpyxl
from os.path import dirname, join


#accessing excel file using data frame
filename = join(dirname(__file__), "soilexcel.xlsx")
df = pd.read_excel("soilexcel.xlsx", engine='openpyxl',index_col=1)
ans=str(df[(df.area == 'Navsari'.lower())])
print(ans)